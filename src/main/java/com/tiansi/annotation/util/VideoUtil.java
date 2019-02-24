package com.tiansi.annotation.util;

import com.tiansi.annotation.domain.Clips;
import com.tiansi.annotation.domain.Video;
import com.tiansi.annotation.domain.body.VideoRequestBody;
import com.tiansi.annotation.exception.ErrorCode;
import com.tiansi.annotation.exception.TiansiException;
import com.tiansi.annotation.model.Props;
import com.tiansi.annotation.service.ClipsService;
import org.apache.commons.io.FileUtils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class VideoUtil {
    @Autowired
    private Props props;
    @Autowired
    private ClipsService clipsService;
    @Autowired
    private AddressUtil addressUtil;

    /**
     * @param video video
     * @apiNote 对指定video进行剪辑，对其所有AB区间生成帧文件，并压缩保存
     */
    @Async
    public void cutVideo(Video video) throws TiansiException {
        String clipHome = props.getClipHome();
        int step = props.getStepSize();
        if (video == null) {
            throw new TiansiException(ErrorCode.INVALID_PARAMETER, "Video can not be null !");
        }
        VideoRequestBody videoRequestBody = new VideoRequestBody(video);
        if (videoRequestBody.getClipsInfo() == null || videoRequestBody.getClipsInfo().size() == 0) {
            return;
        }
        String basePath = clipHome + "/video-" + video.getId();
        File videoDir = new File(basePath);
        //删除上个版本的区间
        if (videoDir.exists()) {
            try {
                FileUtils.deleteDirectory(videoDir);
            } catch (Exception e) {
                e.printStackTrace();
                throw new TiansiException(ErrorCode.FILE_OPERATION_ERROR, e.getMessage());
            }
        }
        clipsService.deleteByVideoId(video.getId());
        if (!videoDir.mkdir()) {
            throw new TiansiException(ErrorCode.FILE_OPERATION_ERROR, "Create segment output directory of video" + video.getId() + " failed !");
        }
        videoRequestBody.getClipsInfo().forEach(numPair -> {
            try {
                String clipName = "clip-" + numPair.getStart() + "-" + numPair.getEnd();
                String outputPath = basePath + "/" + clipName;
                File outputDir = new File(outputPath);
                if (!outputDir.exists()) {
                    if (!outputDir.mkdir()) {
                        throw new TiansiException(ErrorCode.FILE_OPERATION_ERROR, "Create clip named " + clipName + " output " +
                                "directory of video" + video.getId() + " failed !");
                    }
                }
                Integer frameNum = interceptFrame(video.getAddress(), outputPath, numPair.getStart(), numPair.getEnd(), step);
                try {
                    String zipFilePath = compress(outputPath, outputPath + ".zip");
                    FileUtils.deleteDirectory(outputDir);
                    Clips clips = new Clips();
                    clips.setAddress(addressUtil.toServerAddress(zipFilePath));
                    clips.setFrameNum(frameNum);
                    clips.setVideoId(video.getId());
                    clips.setName("video-" + video.getId() + "-" + clipName);
                    clipsService.save(clips);
                } catch (Exception e) {
                    throw new TiansiException(ErrorCode.FILE_OPERATION_ERROR, e.getMessage());
                }
            } catch (TiansiException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * @param videoPath  视频路径
     * @param outputPath 视频输出路径
     * @param start      开始时间
     * @param end        结束时间
     * @param step       步长
     * @return 输出路径
     * @apiNote 生成帧文件
     */
    private Integer interceptFrame(String videoPath, String outputPath, double start, double end, int step) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        VideoCapture videoCapture = new VideoCapture(videoPath);
        int frameNum = 0;
        if (videoCapture.isOpened()) {
            int fps = (int) videoCapture.get(Videoio.CAP_PROP_FPS);
            List<Integer> locations = getLocations(start, end, fps, step);
            List<Mat> frames = getSpecificFrames(videoCapture, locations);
            frameNum = frames.size();
            for (int i = 0; i < frames.size(); i++) {
                Imgcodecs.imwrite(outputPath + "/" + i + ".jpg", frames.get(i));
            }
            frames.forEach(Mat::release);
        }
        videoCapture.release();
        return frameNum;
    }

    /**
     * @param start    开始时间
     * @param end      结束时间
     * @param fps      fps
     * @param stepSize 步进
     * @return 区间帧位置列表
     * @apiNote 根据区间开始结束时间计算区间的帧位置
     */
    private List<Integer> getLocations(double start, double end, int fps, int stepSize) {
        List<Integer> locations = new ArrayList<>();
        int startLocation = (int) Math.ceil(start * fps);
        int endLocation = (int) Math.floor(end * fps);
        int location = startLocation;
        while (location <= endLocation) {
            locations.add(location);
            location += stepSize;
        }
        return locations;
    }

    /**
     * @param videoCapture videoCapture对象
     * @param locations    帧位置
     * @return 帧列表
     * @apiNote 获取视频指定帧
     */
    private List<Mat> getSpecificFrames(VideoCapture videoCapture, List<Integer> locations) {
        List<Mat> frames = new ArrayList<>();
        for (int location : locations) {
            Mat frame = new Mat();
            videoCapture.set(Videoio.CAP_PROP_POS_FRAMES, location);
            videoCapture.read(frame);
            frames.add(frame);
        }
        return frames;
    }


    /**
     * @param source 源文件夹位置
     * @param output 输出压缩文件位置及名字
     * @return 输出文件路径
     * @apiNote 压缩文件夹
     */
    private String compress(String source, String output) throws TiansiException {
        File sourceFile = new File(source);
        if (!sourceFile.exists()) {
            throw new TiansiException(ErrorCode.FILE_OPERATION_ERROR, source + "doesn't exist !");
        }
        if (!sourceFile.isDirectory()) {
            throw new TiansiException(ErrorCode.FILE_OPERATION_ERROR, source + "is not a directory !");
        }
        File outputFile = new File(output);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
            File[] files = sourceFile.listFiles();
            if (files == null || files.length == 0) {
                outputFile.createNewFile();
                return output;
            }
            for (File file : files) {
                try {
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
                    ZipEntry zipEntry = new ZipEntry(sourceFile.getName() + File.separator + file.getName());
                    zipOutputStream.putNextEntry(zipEntry);
                    int count;
                    byte[] buf = new byte[1024];
                    while ((count = bufferedInputStream.read(buf)) != -1) {
                        zipOutputStream.write(buf, 0, count);
                    }
                    bufferedInputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            zipOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

}
