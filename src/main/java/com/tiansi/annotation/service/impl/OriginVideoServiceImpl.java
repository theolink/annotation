package com.tiansi.annotation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sun.org.apache.xpath.internal.operations.Div;
import com.tiansi.annotation.domain.*;
import com.tiansi.annotation.service.*;
import com.tiansi.annotation.util.OriginVideoUtil;
import com.tiansi.annotation.exception.ErrorCode;
import com.tiansi.annotation.exception.TiansiException;
import com.tiansi.annotation.mapper.OriginVideoMapper;
import com.tiansi.annotation.model.Props;
import com.tiansi.annotation.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private DivideTypeService divideTypeService;
    @Autowired
    private VideoService videoService;

    @Override
    public int scan(Users processor) {
        int videoNum = 0;
        try {
            // 扫描法庭视频文件夹
            ArrayList<String> directoryNames = directoriesService.notScan(props.getOriginVideoHome());
            for (String directoryName : directoryNames) {
                videoNum += scan(props.getOriginVideoHome() + "/" + directoryName, processor, null);
            }
            directoriesService.addDirectories(directoryNames);

            //扫描扮演视频文件夹
            ArrayList<String> undividedDirectoryNames = directoriesService.notScan(props.getUndividedVideoHome());
            for (String undividedNames : undividedDirectoryNames) {
                videoNum += scan(props.getUndividedVideoHome() + "/" + undividedNames, processor, 1);
            }
            directoriesService.addDirectories(undividedDirectoryNames);
        } catch (TiansiException e) {
            e.printStackTrace();
        }

        return videoNum;
    }

    @Override
    public int scan(String path, Users processor, Integer mode) throws TiansiException {

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
                    totalVideosNum += scan(fileList[i].getAbsolutePath(), processor, mode);
                } else {
                    String fileName = fileList[i].getName();
                    if (originVideoUtil.isVideo(fileName)) {
                        String absolutePath = fileList[i].getAbsolutePath();
                        String videoPath = addressUtil.toServerAddress(absolutePath);
                        String videoName = addressUtil.getFileName(absolutePath);
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
                Trial trial = new Trial();
                trial.setName(trialName);
                trial.setUploader(processor.getId());
                Long trialId = trialService.insert(trial);
                if (mode == null) {
                    originVideos.forEach(originVideo -> originVideo.setTrialId(trialId));
                } else {
                    Long divideType = divideTypeService.getOne(new QueryWrapper<DivideType>().eq("name", "一分")).getId();
                    List<Video> videos = new ArrayList<>();
                    originVideos.forEach(originVideo -> {
                        originVideo.setTrialId(trialId);
                        originVideo.setDivideType(divideType);
                        originVideo.setPreDeal(4);
                        Video video = new Video();
                        video.setOriginVideoId(originVideo.getId());
                        video.setName(originVideo.getName());
                        video.setAddress(originVideo.getAddress());
                        videos.add(video);
                    });
                    videoService.saveBatch(videos);
                }
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
        if (trialId != null) {
            queryWrapper = queryWrapper.eq("trial_id", trialId);
        }
        if (!StringUtils.isEmpty(name)) {
            queryWrapper = queryWrapper.like("name", name);
        }
        if (uploader != null) {
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
        if (preDealer != null) {
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
        page = (Page<OriginVideo>) page(page, queryWrapper);
        return page;
    }

    @Override
    public void divide(Users processor) {
        List<OriginVideo> originVideos = getUndividedTyped(processor);
        startDivide(getIds(originVideos), processor);
        originVideoUtil.divide(originVideos);
    }

    @Override
    public void startDivide(Long originId, Users processor) {
        if(originId==null){
            return;
        }
        this.baseMapper.startDivide(3, originId);
    }

    @Override
    public void startDivide(List<Long> originIds, Users processor) {
        if(originIds.isEmpty()){
            return;
        }
        this.baseMapper.startDivide(3, originIds);
    }

    @Override
    public void endDivide(Long originId) {
        if(originId==null){
            return;
        }
        this.baseMapper.endDivide(4, new Date(), originId);
    }

    @Override
    public void endDivide(List<Long> originIds) {
        if(originIds.isEmpty()){
            return;
        }
        this.baseMapper.endDivide(4, new Date(), originIds);
    }

    @Override
    public void middleImg() {
        List<OriginVideo> originVideos = list(new QueryWrapper<OriginVideo>().isNull("img_path"));
        originVideoUtil.middleImgs(originVideos);
    }


    @Override
    public boolean type(Long id, Long typeId) throws TiansiException {
        if (id == null || typeId == null) {
            throw new TiansiException(ErrorCode.INVALID_PARAMETER, "Id and typeId can not be null !");
        }
        DivideType divideType = divideTypeService.getById(typeId);
        if (divideType == null) {
            throw new TiansiException(ErrorCode.ENTITY_NOT_EXIST, "DivideType with id " + typeId + " not exist!");
        }
        OriginVideo originVideo = getById(id);
        if (originVideo == null) {
            throw new TiansiException(ErrorCode.ENTITY_NOT_EXIST, "OriginVideo with id " + id + " not exist!");
        }
        originVideo.setDivideType(typeId);
        originVideo.setTypeName(divideType.getName());
        originVideo.setPreDeal(2);
        return updateById(originVideo);
    }

    @Override
    public boolean untyped(Long id) throws TiansiException {
        if (id == null) {
            throw new TiansiException(ErrorCode.INVALID_PARAMETER, "Id can not be null !");
        }
        OriginVideo originVideo = getById(id);
        if (originVideo == null) {
            throw new TiansiException(ErrorCode.ENTITY_NOT_EXIST, "OriginVideo with Id: " + id + " is not exist !");
        }
        if (!originVideo.getPreDeal().equals(2)) {
            throw new TiansiException(ErrorCode.INVALID_PARAMETER, "Only typed but not divided origin video can be untyped !");
        }
        originVideo.setPreDeal(1);
        originVideo.setDivideType(null);
        return updateById(originVideo);
    }

    @Override
    public int assign(Integer amount, Users users) {
        Page<OriginVideo> page = new Page<>(1, amount);
        page = (Page<OriginVideo>) page(page, new QueryWrapper<OriginVideo>().eq("pre_deal", 0));
        List<OriginVideo> originVideos = page.getRecords();
        originVideos.forEach(originVideo -> {
            if (originVideo.getDivideType() == null) {
                originVideo.setPreDeal(1);
            } else {
                originVideo.setPreDeal(2);
            }
            originVideo.setPreDealer(users.getId());
        });
        if (originVideos.isEmpty()) {
            return 0;
        }
        if (updateBatchById(originVideos)) {
            return originVideos.size();
        }
        return 0;
    }


    private List<OriginVideo> getUndividedTyped(Users processor) {
        return list(new QueryWrapper<OriginVideo>().eq("pre_deal", 2).eq("pre_dealer", processor.getId()));
    }

    private List<Long> getIds(List<OriginVideo> originVideos) {
        List<Long> ids = new ArrayList<>();
        originVideos.forEach(originVideo -> ids.add(originVideo.getId()));
        return ids;
    }
}
