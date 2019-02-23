package com.tiansi.annotation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tiansi.annotation.domain.Clips;
import com.tiansi.annotation.domain.Trial;
import com.tiansi.annotation.domain.Video;
import com.tiansi.annotation.exception.ErrorCode;
import com.tiansi.annotation.exception.TiansiException;
import com.tiansi.annotation.mapper.VideoMapper;
import com.tiansi.annotation.service.ClipsService;
import com.tiansi.annotation.service.DirectoriesService;
import com.tiansi.annotation.service.TrialService;
import com.tiansi.annotation.service.VideoService;

import com.tiansi.annotation.util.DateUtil;
import com.tiansi.annotation.util.Props;
import com.tiansi.annotation.util.VideoClips;
import com.tiansi.annotation.util.VideoRange;
import io.swagger.annotations.ApiParam;
import io.swagger.models.auth.In;
import org.apache.commons.io.FileUtils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {
    @Autowired
    private DirectoriesService directoriesService;
    @Autowired
    private ClipsService clipsService;
    @Autowired
    private TrialService trialService;
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
    public List<Video> findSomeones(Long userId) {
        return this.list(new QueryWrapper<Video>().eq("tagger", userId));
    }

    @Override
    public List<Video> findSomeonesTagged(Long userId) {
        return this.list(new QueryWrapper<Video>().eq("tagger", userId).eq("tagged", 2));
    }

    @Override
    public List<Video> findSomeonesTagging(Long userId) {
        return this.list(new QueryWrapper<Video>().eq("tagger", userId).eq("tagged", 1));
    }

    @Override
    public Integer interceptFrame(String videoPath, String outputPath, double start, double end, int step) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        VideoCapture videoCapture = new VideoCapture(videoPath);
        Integer frameNum = 0;
        if (videoCapture.isOpened()) {
            int fps = (int) videoCapture.get(Videoio.CAP_PROP_FPS);
            List<Integer> locations = getLocations(start, end, fps, step);
            List<Mat> frames = getSpecificFrames(videoCapture, locations);
            frameNum =  frames.size();
            for (int i = 0; i < frames.size(); i++) {
                Imgcodecs.imwrite(outputPath + "\\" + i + ".jpg", frames.get(i));
            }
        }
        videoCapture.release();
        return frameNum;
    }

    @Override
    public void cutVideo(Long videoId) throws Exception {
        String basePath = props.getClipHome();
        int step = props.getStepSize();

        Video video = getById(videoId);
        if (video == null) {
            throw new Exception("指定Id的视频不存在");
        }
        VideoClips videoClips = new VideoClips(video);
        if (videoClips.getClipsInfo() == null || videoClips.getClipsInfo().size() == 0) {
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
            Integer frameNum = interceptFrame(video.getAddress(), outputPath, numPair.getStart(), numPair.getEnd(), step);
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
            clips.setAddress(props.getServerAddress() + "\\clips\\video-" + videoId + "\\clip-" + numPair.getStart() + "-" + numPair.getEnd() + ".zip");
            clips.setFrameNum(frameNum);
            clips.setVideoId(videoId);
            clips.setName("clip-" + numPair.getStart() + "-" + numPair.getEnd());
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

//    @Override
//    public int addVideos() {
//        int videoNum = 0;
//        try {
//            ArrayList<String> directoryNames = directoriesService.notScan();
//            for (String directoryName : directoryNames) {
//                videoNum += scan(directoryName);
//            }
//        } catch (TiansiException e) {
//            e.printStackTrace();
//        }
//        return videoNum;
//    }

//    /**
//     * 扫描视频
//     *
//     * @param path 路径
//     * @return
//     */
//    public int scan(String path) {
//        //目录中视频数
//        int flag = 0;
//        //视频总数
//        int totalVideos = 0;
//        ArrayList<String> directories = new ArrayList<>();
//        ArrayList<Video> videos = new ArrayList<>();
//        File directory = new File(path);
//        if (!directory.isDirectory()) {
//            return 0;
//        } else {
//            File[] fileList = directory.listFiles();
//            for (int i = 0; i < fileList.length; i++) {
//                //如果当前是文件夹，进入递归扫描文件夹
//                if (fileList[i].isDirectory()) {
//                    directories.add(fileList[i].getAbsolutePath());
//                    //递归扫描下面的文件夹
//                    totalVideos += scan(fileList[i].getAbsolutePath());
//                }
//                //非文件夹
//                else {
//                    String fileName = fileList[i].getName();
//                    if (isVideo(fileName)) {
//                        flag++;
//                        String videoAbsolutePath = fileList[i].getAbsolutePath();
//                        String videoPath = videoAbsolutePath.replace("F:\\data", "192.168.1.120:8080");
//                        Video video = new Video();
//                        video.setAddress(videoPath);
//                        video.setTagged(0);
//                        videos.add(video);
//                    }
//                }
//            }
//            if (flag > 0) {
//                String trialPath = path.replace("F:\\data\\annotation\\video\\origin\\", "");
//                String trialName = trialPath.replace("\\", "-");
//                Date currentDate = new Date();
//                Trial trial = new Trial();
//                trial.setName(trialName);
//                trial.setUploadDate(currentDate);
//                trial.setVideoNum(flag);
//                int trialId = trialService.insert(trial);
//                videos.forEach(video -> video.setTrialId(trialId));
//                saveBatch(videos);
//                return flag;
//            } else {
//                return totalVideos;
//            }
//        }
//    }



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

//    @Override
//    public ArrayList<String> divide(String originPath, String outputPath) {
//        ArrayList<String> processedPath = new ArrayList<>();
//        try {
//            int dividedMode = getDividedMode(originPath);
//            List<VideoRange> videoRanges;
//            if(dividedMode==0){
//                String videoPath = outputPath + "\\" + System.currentTimeMillis() + (int)(Math.random()*100000) + ".mp4";
//                try {
//                    Files.copy(new File(originPath).toPath(),new File(videoPath).toPath());
//                    processedPath.add(videoPath);
//                    return processedPath;
//                }catch (Exception e){
//                    e.printStackTrace();
//                    return processedPath;
//                }
//            }else if (dividedMode == 1) {
//                videoRanges = props.getVideoRanges1();
//            } else {
//                videoRanges = props.getVideoRanges2();
//            }
//            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//            VideoCapture videoCapture = new VideoCapture(originPath);
//            int width = (int) videoCapture.get(Videoio.CAP_PROP_FRAME_WIDTH);
//            int height = (int) videoCapture.get(Videoio.CAP_PROP_FRAME_HEIGHT);
//            int fps = (int) videoCapture.get(Videoio.CAP_PROP_FPS);
//            int totalFrames = (int) videoCapture.get(Videoio.CV_CAP_PROP_FRAME_COUNT);
//
//            //定义VideoWriter
//            List<VideoWriter> videoWriters = new ArrayList<>();
//            List<Integer> rowStart = new ArrayList<>();
//            List<Integer> rowEnd = new ArrayList<>();
//            List<Integer> colStart = new ArrayList<>();
//            List<Integer> colEnd = new ArrayList<>();
//            for (int i = 0; i < videoRanges.size(); i++) {
//                String videoPath = outputPath + "\\" + System.currentTimeMillis() + (int)(Math.random()*100000) + ".mp4";
//                VideoRange videoRange = videoRanges.get(i);
//                Size size = new Size(videoRange.getXRange() * width, videoRange.getYRange() * height);
//                VideoWriter videoWriter = new VideoWriter(videoPath, VideoWriter.fourcc('m', 'p', '4', 'v'), fps, size);
//                videoWriters.add(videoWriter);
//                rowStart.add((int) (height * videoRange.getY1()));
//                rowEnd.add((int) (height * videoRange.getY2()));
//                colStart.add((int) (width * videoRange.getX1()));
//                colEnd.add((int) (width * videoRange.getX2()));
//                processedPath.add(videoPath);
//            }
//            //一帧一帧处理避免内存不足
//            for (int i = 1; i <= totalFrames; i++) {
//                Mat mat = new Mat();
//                videoCapture.set(Videoio.CAP_PROP_POS_FRAMES, i);
//                videoCapture.read(mat);
//                //视频提取的最后几帧可能是空帧，原因不明
//                if (mat.rows() == 0 || mat.cols() == 0) {
//                    System.out.println("loss frames = " + totalFrames + " - " + (i - 1) + " = " + (totalFrames - i + 1));
//                    mat.release();
//                    break;
//                }
//                for (int j = 0; j < videoWriters.size(); j++) {
//                    Mat subMat = mat.submat(rowStart.get(j), rowEnd.get(j), colStart.get(j), colEnd.get(j));
//                    videoWriters.get(j).write(subMat);
//                    subMat.release();
//                }
//                mat.release();
//            }
//            videoWriters.forEach(VideoWriter::release);
//            videoCapture.release();
//            return processedPath;
//        } catch (
//                TiansiException e) {
//            e.printStackTrace();
//        }
//        return processedPath;
//    }

    //TODO 图像边界检测获取切割模式
//
//    /**
//     *  获取切割模式
//     * @param originPath 路径
//     * @return 切割模式
//     * @throws TiansiException
//     */
//    private int getDividedMode(String originPath) throws TiansiException {
//        if (originPath.toLowerCase().contains("150.120.0.10")) {
//            return 0;
//        } else if (originPath.toLowerCase().contains("150.120.30.4") || originPath.toLowerCase().contains("150.120.30.21")
//                || originPath.toLowerCase().contains("150.120.30.23")) {
//            return 1;
//        } else if (originPath.toLowerCase().contains("150.120.30.12") || originPath.toLowerCase().contains("150.120.30.16")
//                || originPath.toLowerCase().contains("150.120.202.202")) {
//            return 2;
//        }else  if (originPath.toLowerCase().contains("150.120.30.19")){
//            if(originPath.toLowerCase().contains("150.120.30.19_01_201706")||originPath.toLowerCase().contains("150.120.30.19_01_201707")
//                    ||originPath.toLowerCase().contains("150.120.30.19_01_201708")||originPath.toLowerCase().contains("150.120.30.19_01_201709")){
//                return 1;
//            }else {
//                return 2;
//            }
//        }else {
//            throw new TiansiException(ErrorCode.INVALID_CONFIG, "Unknown divided mode");
//        }
//    }
}
