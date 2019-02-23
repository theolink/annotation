package com.tiansi.annotation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tiansi.annotation.domain.OriginVideo;
import com.tiansi.annotation.domain.Trial;
import com.tiansi.annotation.domain.Users;
import com.tiansi.annotation.domain.Video;
import com.tiansi.annotation.exception.ErrorCode;
import com.tiansi.annotation.exception.TiansiException;
import com.tiansi.annotation.mapper.OriginVideoMapper;
import com.tiansi.annotation.service.DirectoriesService;
import com.tiansi.annotation.service.OriginVideoService;
import com.tiansi.annotation.service.TrialService;
import com.tiansi.annotation.service.VideoService;
import com.tiansi.annotation.util.AddressUtil;
import com.tiansi.annotation.util.DateUtil;
import com.tiansi.annotation.util.Props;
import com.tiansi.annotation.util.VideoRange;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OriginVideoServiceImpl extends ServiceImpl<OriginVideoMapper, OriginVideo> implements OriginVideoService {
    @Autowired
    private TrialService trialService;
    @Autowired
    private DirectoriesService directoriesService;
    @Autowired
    private VideoService videoService;
    @Autowired
    private Props props;
    @Autowired
    private AddressUtil addressUtil;

    @Override
    public int scan(Users processor) {
        int videoNum = 0;
        try {
            ArrayList<String> directoryNames = directoriesService.notScan();
            for (String directoryName : directoryNames) {
                videoNum += scan(props.getOriginVideoHome() + "/" + directoryName, processor);
            }
            directoriesService.addDirectories(directoryNames);
        } catch (TiansiException e) {
            e.printStackTrace();
        }
        return videoNum;
    }

    @Override
    public int scan(String path, Users processor) throws TiansiException {

        File directory = new File(path);
        if (!directory.isDirectory()) {
            throw new TiansiException(ErrorCode.INVALID_PARAMETER, "Parameter 'path' is not a directory!");
        } else {
            File[] fileList = directory.listFiles();
            if (fileList == null || fileList.length == 0) {
                return 0;
            }
            int totalVideosNum = 0;
            ArrayList<OriginVideo> originVideos = new ArrayList<>();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    totalVideosNum += scan(fileList[i].getAbsolutePath(), processor);
                } else {
                    String fileName = fileList[i].getName();
                    if (isVideo(fileName)) {
                        String absolutePath = fileList[i].getAbsolutePath();
                        System.out.println("absolutePath is :" + absolutePath);
                        String videoPath = addressUtil.toServerAddress(absolutePath);
                        String videoName = addressUtil.getFileName(videoPath);
                        OriginVideo originVideo = new OriginVideo();
                        originVideo.setName(videoName);
                        originVideo.setAddress(videoPath);
                        originVideo.setUploader(processor.getId());
                        originVideos.add(originVideo);
                    }
                }
            }
            if (originVideos.size() > 0) {
                String trialName = addressUtil.getTrialName(path);
                System.out.println("trialName is:" + trialName);
                Trial trial = new Trial();
                trial.setName(trialName);
                trial.setUploader(processor.getId());
                Long trialId = trialService.insert(trial);
                originVideos.forEach(originVideo -> originVideo.setTrialId(trialId));
                saveBatch(originVideos);
                return originVideos.size() + totalVideosNum;
            } else {
                return totalVideosNum;
            }
        }
    }

    @Override
    public boolean delete(List<Long> originIds) {
        return removeByIds(originIds);
    }

    @Override
    public Page find(Long id, Long trialId, String name, Long uploader, Date uploadDateStart, Date uploadDateEnd, Integer preDeal,
                     Long preDealer, Date preDealDateStart, Date preDealDateEnd, Integer currentPage, Integer pageSize) {
        QueryWrapper<OriginVideo> queryWrapper = new QueryWrapper<>();
        if (id != null && id != 0) {
            queryWrapper = queryWrapper.eq("id", id);
        }
        if (trialId != null && trialId != 0) {
            queryWrapper = queryWrapper.eq("trial_id", trialId);
        }
        if (!StringUtils.isEmpty(name)) {
            queryWrapper = queryWrapper.like("name", name);
        }
        if (uploader != null && uploader != 0) {
            queryWrapper = queryWrapper.eq("uploader", uploader);
        }
        if (uploadDateStart != null) {
            queryWrapper = queryWrapper.ge("upload_date", uploadDateStart);
        }
        if (uploadDateEnd != null) {
            queryWrapper = queryWrapper.le("upload_date", uploadDateEnd);
        }
        if (preDeal != null) {
            queryWrapper = queryWrapper.eq("pre_deal", preDeal);
        }
        if (preDealer != null && preDealer != 0) {
            queryWrapper = queryWrapper.eq("pre_dealer", preDealer);
        }
        if (preDealDateStart != null) {
            queryWrapper = queryWrapper.ge("pre_deal_date", preDealDateStart);
        }
        if (preDealDateEnd != null) {
            queryWrapper = queryWrapper.le("pre_deal_date", preDealDateEnd);
        }
        currentPage = currentPage != null && currentPage > 0 ? currentPage : 1;
        pageSize = pageSize != null && pageSize > 0 ? pageSize : 10;
        Page<OriginVideo> page = new Page<>(currentPage, pageSize);
        return (Page) page(page, queryWrapper);
    }

    @Override
    public int divide(Users processor) {
        List<OriginVideo> originVideos = getUndivided();
        return divide(originVideos, processor);
    }

    @Override
    public int divide(List<OriginVideo> originVideos, Users processor) {
        startDivide(getIds(originVideos), processor);
        int dividedNum = 0;
        for (OriginVideo originVideo : originVideos) {
            dividedNum += divide(originVideo);
            endDivide(originVideo.getId());
        }
        return dividedNum;
    }


    @Override
    public void startDivide(Long originId, Users processor) {
        OriginVideo originVideo = new OriginVideo();
        originVideo.setPreDeal(1);
        originVideo.setPreDealer(processor.getId());
        update(originVideo, new UpdateWrapper<OriginVideo>().eq("id", originId));
    }

    @Override
    public void startDivide(List<Long> originIds, Users processor) {
        OriginVideo originVideo = new OriginVideo();
        originVideo.setPreDeal(1);
        originVideo.setPreDealer(processor.getId());
        update(originVideo, new UpdateWrapper<OriginVideo>().in("id", originIds));
    }

    @Override
    public void endDivide(Long originId) {
        OriginVideo originVideo = new OriginVideo();
        originVideo.setPreDeal(2);
        originVideo.setPreDealDate(new Date());
        update(originVideo, new UpdateWrapper<OriginVideo>().eq("id", originId));
    }

    @Override
    public void endDivide(List<Long> originIds) {
        OriginVideo originVideo = new OriginVideo();
        originVideo.setPreDeal(2);
        originVideo.setPreDealDate(new Date());
        update(originVideo, new UpdateWrapper<OriginVideo>().in("id", originIds));
    }

    /**
     * 拆分画面拼接的原始视频
     *
     * @param originVideo 原始视频
     * @return 分割成功的视频数
     */
    private int divide(OriginVideo originVideo) {
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
            return processedPaths.size();
        }
        return 0;
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
    private boolean isVideo(String name) {
        String suffix = name.substring(name.lastIndexOf(".") + 1);
        for (String videoType : props.getVideoTypes()) {
            if (suffix.equalsIgnoreCase(videoType)) {
                return true;
            }
        }
        return false;
    }

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

    private String generateDividedPath() {
        String dividedPath = props.getVideoHome() + "/" + DateUtil.getDay();
        File file = new File(dividedPath);
        if (!file.exists()) {
            file.mkdir();
        }
        return dividedPath;
    }

    private List<OriginVideo> getUndivided() {
        return list(new QueryWrapper<OriginVideo>().eq("pre_deal", 0));
    }

    private List<Long> getIds(List<OriginVideo> originVideos) {
        List<Long> ids = new ArrayList<>();
        originVideos.forEach(originVideo -> ids.add(originVideo.getId()));
        return ids;
    }
}
