package com.tiansi.annotation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tiansi.annotation.domain.OriginVideo;
import com.tiansi.annotation.domain.Trial;
import com.tiansi.annotation.domain.Users;
import com.tiansi.annotation.util.OriginVideoUtil;
import com.tiansi.annotation.exception.ErrorCode;
import com.tiansi.annotation.exception.TiansiException;
import com.tiansi.annotation.mapper.OriginVideoMapper;
import com.tiansi.annotation.model.Props;
import com.tiansi.annotation.service.DirectoriesService;
import com.tiansi.annotation.service.OriginVideoService;
import com.tiansi.annotation.service.TrialService;
import com.tiansi.annotation.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@EnableAsync
public class OriginVideoServiceImpl extends ServiceImpl<OriginVideoMapper, OriginVideo> implements OriginVideoService {
    @Autowired
    private TrialService trialService;
    @Autowired
    private DirectoriesService directoriesService;
    @Autowired
    private Props props;
    @Autowired
    private AddressUtil addressUtil;
    @Autowired
    private OriginVideoUtil originVideoUtil;

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
                    if (originVideoUtil.isVideo(fileName)) {
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
        if (id != null) {
            queryWrapper = queryWrapper.eq("id", id);
        }
        if (trialId != null ) {
            queryWrapper = queryWrapper.eq("trial_id", trialId);
        }
        if (!StringUtils.isEmpty(name)) {
            queryWrapper = queryWrapper.like("name", name);
        }
        if (uploader != null ) {
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
        if (preDealer != null ) {
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
    public void divide(Users processor) {
        List<OriginVideo> originVideos = getUndivided();
        startDivide(getIds(originVideos), processor);
        originVideoUtil.divide(originVideos);
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

    private List<OriginVideo> getUndivided() {
        return list(new QueryWrapper<OriginVideo>().eq("pre_deal", 0));
    }

    private List<Long> getIds(List<OriginVideo> originVideos) {
        List<Long> ids = new ArrayList<>();
        originVideos.forEach(originVideo -> ids.add(originVideo.getId()));
        return ids;
    }
}
