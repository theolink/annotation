package com.tiansi.annotation.domain.body;

import com.tiansi.annotation.domain.*;
import com.tiansi.annotation.exception.ErrorCode;
import com.tiansi.annotation.exception.TiansiException;
import com.tiansi.annotation.service.OriginVideoService;
import com.tiansi.annotation.service.TrialService;
import com.tiansi.annotation.service.VideoService;
import com.tiansi.annotation.util.AddressUtil;
import com.tiansi.annotation.util.DateUtil;
import com.tiansi.annotation.model.Props;
import com.tiansi.annotation.model.VideoRange;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
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

    /**
     * 拆分画面拼接的原始视频
     *
     * @param originVideos 原始视频
     */
    @Async
    public void divide(List<OriginVideo> originVideos) {
        for (OriginVideo originVideo : originVideos) {
            System.out.println("开始分割第" + originVideos.indexOf(originVideo) + "个视频......");
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
        List<String> processedPaths = divide(addressUtil.toLocalAddress(originVideo.getAddress()), generateDividedPath());
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
    private ArrayList<String> divide(String originPath, String outputPath) {
        ArrayList<String> processedPath = new ArrayList<>();
        try {
            int dividedMode = getDividedMode(originPath);
            List<VideoRange> videoRanges;
            if (dividedMode == 0) {
                String videoPath = outputPath + "/" + System.currentTimeMillis() + (int) (Math.random() * 100000) + ".mp4";
                try {
                    Files.copy(new File(originPath).toPath(), new File(videoPath).toPath());
                    processedPath.add(videoPath);
                    return processedPath;
                } catch (Exception e) {
                    e.printStackTrace();
                    return processedPath;
                }
            } else if (dividedMode == 1) {
                videoRanges = props.getVideoRanges1();
            } else {
                videoRanges = props.getVideoRanges2();
            }
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
            for (int i = 1; i <= totalFrames; i++) {
                Mat mat = new Mat();
                videoCapture.set(Videoio.CAP_PROP_POS_FRAMES, i);
                videoCapture.read(mat);
                //视频提取的最后几帧可能是空帧，原因不明
                if (mat.rows() == 0 || mat.cols() == 0) {
                    System.out.println("loss frames = " + totalFrames + " - " + (i - 1) + " = " + (totalFrames - i + 1));
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
        } catch (TiansiException e) {
            e.printStackTrace();
        }
        return processedPath;
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

    //TODO 图像边界检测获取切割模式

    /**
     * 获取切割模式
     *
     * @param originPath 路径
     * @return 切割模式
     * @throws TiansiException TiansiException
     */
    private int getDividedMode(String originPath) throws TiansiException {
        if (originPath.toLowerCase().contains("150.120.0.10")) {
            return 0;
        } else if (originPath.toLowerCase().contains("150.120.30.4") || originPath.toLowerCase().contains("150.120.30.21")
                || originPath.toLowerCase().contains("150.120.30.23")) {
            return 1;
        } else if (originPath.toLowerCase().contains("150.120.30.12") || originPath.toLowerCase().contains("150.120.30.16")
                || originPath.toLowerCase().contains("150.120.202.202")) {
            return 2;
        } else if (originPath.toLowerCase().contains("150.120.30.19")) {
            if (originPath.toLowerCase().contains("150.120.30.19_01_201706") || originPath.toLowerCase().contains("150.120.30.19_01_201707")
                    || originPath.toLowerCase().contains("150.120.30.19_01_201708") || originPath.toLowerCase().contains("150.120.30.19_01_201709")) {
                return 1;
            } else {
                return 2;
            }
        } else {
            throw new TiansiException(ErrorCode.INVALID_CONFIG, "Unknown divided mode");
        }
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

}
