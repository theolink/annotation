package com.tiansi.annotation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tiansi.annotation.domain.Clips;
import com.tiansi.annotation.domain.ClipsInfo;
import com.tiansi.annotation.domain.Video;
import com.tiansi.annotation.mapper.VideoMapper;
import com.tiansi.annotation.service.ClipsInfoService;
import com.tiansi.annotation.service.ClipsService;
import com.tiansi.annotation.service.VideoService;

import com.tiansi.annotation.util.Props;
import com.tiansi.annotation.util.VideoClips;
import org.apache.commons.io.FileUtils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {
    @Autowired
    private ClipsService clipsService;
    @Autowired
    private Props props;

    @Override
    public List<Video> findAll() {
        return this.list(null);
    }

    @Override
    public List<Video> findUntagged() {
        return this.list(new QueryWrapper<Video>().eq("tagged", 0));
    }

    @Override
    public List<Video> findSomeones(int userId) {
        return this.list(new QueryWrapper<Video>().eq("tagger", userId));
    }

    @Override
    public List<Video> findSomeonesTagged(int userId) {
        return this.list(new QueryWrapper<Video>().eq("tagger", userId).eq("tagged", 2));
    }

    @Override
    public List<Video> findSomeonesTagging(int userId) {
        return this.list(new QueryWrapper<Video>().eq("tagger", userId).eq("tagged", 1));
    }

    @Override
    public Long interceptFrame(String videoPath, String outputPath, double start, double end, int step) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        VideoCapture videoCapture = new VideoCapture(videoPath);
        long frameNum = 0L;
        if (videoCapture.isOpened()) {
            int fps = (int) videoCapture.get(Videoio.CAP_PROP_FPS);
            List<Integer> locations = getLocations(start, end, fps, step);
            List<Mat> frames = getSpecificFrames(videoCapture, locations);
            frameNum = (long) frames.size();
            for (int i = 0; i < frames.size(); i++) {
                Imgcodecs.imwrite(outputPath + "\\" + i + ".jpg", frames.get(i));
            }
        }
        videoCapture.release();
        return frameNum;
    }

    @Override
    public void cutVideo(int videoId) throws Exception {
        String basePath = props.getVideoOutputDir();
        int step = props.getStepSize();

        Video video = getById(videoId);
        if (video == null) {
            throw new Exception("指定Id的视频不存在");
        }
        VideoClips videoClips = new VideoClips(video);
        if (videoClips.getClipsInfo().size() == 0) {
            return;
        }
        // 当前视频输出目录
        String videoDirPath = basePath + "\\video-" + videoId;
        File videoDir = new File(videoDirPath);
        //删除上个版本的区间
        if (videoDir.exists()) {
            FileUtils.deleteDirectory(videoDir);
        }
        clipsService.deleteByVideoId(videoId);
        videoDir.mkdir();

        videoClips.getClipsInfo().forEach(numPair -> {
            // 当前区间输出目录
            String outputPath = videoDirPath + "\\clip-" + numPair.getStart() + "-" + numPair.getEnd();
            File outputDir = new File(outputPath);
            if (!outputDir.exists()) {
                outputDir.mkdir();
            }
            //提取区间帧并保存文件夹
            Long frameNum = interceptFrame(video.getAddress(), outputPath, numPair.getStart(), numPair.getEnd(), step);
            //压缩文件夹
            String zipFilePath = compress(outputPath, outputPath + ".zip");
            //删除源文件夹
            try {
                FileUtils.deleteDirectory(outputDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
            outputDir.delete();
            Clips clips = new Clips();
            clips.setAddress(zipFilePath);
            clips.setFrameNum(frameNum);
            clips.setVideoId(videoId);
            clipsService.save(clips);
        });
    }

    @Override
    public boolean segment(Video video) {
        Video originVideo = getById(video.getId());
        if (originVideo == null) {
            return false;
        }
        originVideo.setClipsInfo(video.getClipsInfo());
        originVideo.setTagger(video.getTagger());
        originVideo.setTagged(1);
        boolean result = updateById(originVideo);
        //提取区间
        if (result) {
            try {
                cutVideo(video.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * @param source 源文件夹位置
     * @param output 输出压缩文件位置及名字
     * @return 输出文件路径
     * @apiNote 压缩文件夹
     */
    private String compress(String source, String output) {
        File sourceFile = new File(source);
        if (!sourceFile.exists()) {
            throw new RuntimeException(source + "不存在");
        }
        if (!sourceFile.isDirectory()) {
            throw new RuntimeException(source + "不是文件夹");
        }
        File outputFile = new File(output);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
            File[] files = sourceFile.listFiles();
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

}
