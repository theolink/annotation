package com.tiansi.annotation.util;

import com.tiansi.annotation.domain.*;
import com.tiansi.annotation.domain.body.DivideTypeRequestBody;
import com.tiansi.annotation.service.DivideTypeService;
import com.tiansi.annotation.service.OriginVideoService;
import com.tiansi.annotation.service.TrialService;
import com.tiansi.annotation.service.VideoService;
import com.tiansi.annotation.model.Props;
import com.tiansi.annotation.model.VideoRange;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Component
public class OriginVideoUtil {
    @Autowired
    private OriginVideoService originVideoService;
    @Autowired
    private TrialService trialService;
    @Autowired
    private VideoService videoService;
    @Autowired
    private Props props;
    @Autowired
    private AddressUtil addressUtil;
    @Autowired
    private DivideTypeService divideTypeService;

    /**
     * 拆分画面拼接的原始视频
     *
     * @param originVideos 原始视频
     */
    @Async
    public void divide(List<OriginVideo> originVideos) {
        for (OriginVideo originVideo : originVideos) {
            divide(originVideo);
            originVideoService.endDivide(originVideo.getId());
        }
    }

    /**
     * 拆分画面拼接的原始视频
     *
     * @param originVideo 原始视频
     */
    private void divide(OriginVideo originVideo) {
        List<String> processedPaths = divide(addressUtil.toLocalAddress(originVideo.getAddress()), generateDividedPath(), originVideo.getDivideType());
        if (processedPaths.size() > 0) {
            Trial trial = trialService.getById(originVideo.getTrialId());
            trial.setVideoNum(processedPaths.size());
            trialService.updateById(trial);
            processedPaths.forEach(processedPath -> {
                Video video = new Video();
                video.setAddress(addressUtil.toServerAddress(processedPath));
                video.setTrialId(originVideo.getTrialId());
                video.setOriginVideoId(originVideo.getId());
                video.setName(addressUtil.getFileName(processedPath));
                videoService.save(video);
            });
        }
    }

    /**
     * 拆分画面拼接的原始视频
     *
     * @param originPath 原始视频路径
     * @param outputPath 输出路径
     * @return 分割后的视频路径
     */
    private ArrayList<String> divide(String originPath, String outputPath, Long divideTypeId) {
        ArrayList<String> processedPath = new ArrayList<>();
        DivideTypeRequestBody divideTypeRequestBody = new DivideTypeRequestBody(divideTypeService.getById(divideTypeId));
        List<VideoRange> videoRanges = divideTypeRequestBody.getVideoRanges();
        if (videoRanges == null || videoRanges.size() == 0) {

            String videoPath = outputPath + "/" + System.currentTimeMillis() + (int) (Math.random() * 100000) + ".mp4";
            try {
                Files.copy(new File(originPath).toPath(), new File(videoPath).toPath());
                processedPath.add(videoPath);
                return processedPath;
            } catch (Exception e) {
                e.printStackTrace();
                return processedPath;
            }
        }
        // originPath = addressUtil.disFormat(addressUtil.toLocalAddress(originPath));
        originPath = addressUtil.toLocalAddress(originPath);
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        VideoCapture videoCapture = new VideoCapture(originPath);
        int width = (int) videoCapture.get(Videoio.CAP_PROP_FRAME_WIDTH);
        int height = (int) videoCapture.get(Videoio.CAP_PROP_FRAME_HEIGHT);
        int fps = (int) videoCapture.get(Videoio.CAP_PROP_FPS);
        int totalFrames = (int) videoCapture.get(Videoio.CV_CAP_PROP_FRAME_COUNT);

        //定义VideoWriter
        List<VideoWriter> videoWriters = new ArrayList<>();
        List<Integer> rowStart = new ArrayList<>();
        List<Integer> rowEnd = new ArrayList<>();
        List<Integer> colStart = new ArrayList<>();
        List<Integer> colEnd = new ArrayList<>();
        for (int i = 0; i < videoRanges.size(); i++) {
            String videoPath = outputPath + "/" + System.currentTimeMillis() + (int) (Math.random() * 100000) + ".mp4";
            VideoRange videoRange = videoRanges.get(i);
            Size size = new Size(videoRange.getXRange() * width, videoRange.getYRange() * height);
            //h5 video标签支持avc编码器的mp4
            VideoWriter videoWriter = new VideoWriter(videoPath, VideoWriter.fourcc('a', 'v', 'c', '1'), fps, size);
            videoWriters.add(videoWriter);
            rowStart.add((int) (height * videoRange.getY1()));
            rowEnd.add((int) (height * videoRange.getY2()));
            colStart.add((int) (width * videoRange.getX1()));
            colEnd.add((int) (width * videoRange.getX2()));
            processedPath.add(videoPath);
        }
        //一帧一帧处理避免内存不足
        System.out.println("Start to divide...Origin Video Path: " + originPath);
        System.out.println("Video Capture status: opening " + videoCapture.isOpened());
        System.out.println("Divide type " + divideTypeId + ", Video writer size: " + videoWriters.size());
        for (int i = 1; i <= totalFrames; i++) {
            Mat mat = new Mat();
            System.out.println("Total frame: " + totalFrames + ", processing frame: " + i);
            videoCapture.set(Videoio.CAP_PROP_POS_FRAMES, i);
            videoCapture.read(mat);
            //视频提取的最后几帧可能是空帧，原因不明
            if (mat.rows() == 0 || mat.cols() == 0) {
                mat.release();
                break;
            }
            for (int j = 0; j < videoWriters.size(); j++) {
                Mat subMat = mat.submat(rowStart.get(j), rowEnd.get(j), colStart.get(j), colEnd.get(j));
                videoWriters.get(j).write(subMat);
                subMat.release();
            }
            mat.release();
        }
        videoWriters.forEach(VideoWriter::release);
        videoCapture.release();
        return processedPath;
//        } catch (TiansiException e) {
//            e.printStackTrace();
//        }
//        return processedPath;
    }

    /**
     * 判断是否视频格式
     *
     * @param name 文件名称
     * @return 是否视频格式
     */
    public boolean isVideo(String name) {
        String suffix = name.substring(name.lastIndexOf(".") + 1);
        for (String videoType : props.getVideoTypes()) {
            if (suffix.equalsIgnoreCase(videoType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 生成原始视频分割保存路径
     *
     * @return 原始视频分割保存路径
     */
    private String generateDividedPath() {
        String dividedPath = props.getVideoHome() + "/" + DateUtil.getDay();
        File file = new File(dividedPath);
        if (!file.exists()) {
            file.mkdir();
        }
        return dividedPath;
    }


    @Async
    public void middleImgs(List<OriginVideo> originVideos) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        String basePath = props.getImageHome() + "/" + DateUtil.getDay();
        File imgBaseDirectory = new File(basePath);
        if (!imgBaseDirectory.exists()) {
            imgBaseDirectory.mkdir();
        }
        originVideos.forEach(originVideo -> {
            originVideo.setImgPath(middleImg(originVideo, basePath));
        });
        if (originVideos.isEmpty()) {
            return;
        }
        originVideoService.updateBatchById(originVideos);
    }

    private String middleImg(OriginVideo originVideo, String directory) {
        String originVideoPath = addressUtil.toLocalAddress(originVideo.getAddress());
        VideoCapture videoCapture = new VideoCapture(originVideoPath);
        String imgPath = null;
        if (videoCapture.isOpened()) {
            int fps = (int) videoCapture.get(Videoio.CAP_PROP_FPS);
            Integer middle = fps / 2 == 0 ? 1 : fps / 2;

            Mat frame = new Mat();
            videoCapture.set(Videoio.CAP_PROP_POS_FRAMES, middle);
            videoCapture.read(frame);
            if (frame.empty() || frame.cols() == 0 || frame.rows() == 0) {
                videoCapture.set(Videoio.CAP_PROP_POS_FRAMES, 1);
                videoCapture.read(frame);
            }
            imgPath = directory + "/" + originVideo.getId() + ".jpg";
            Imgcodecs.imwrite(imgPath, frame);
            frame.release();
        }
        videoCapture.release();
        return addressUtil.addressFormat(addressUtil.toServerAddress(imgPath));
    }

}
