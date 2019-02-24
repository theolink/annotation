package com.tiansi.annotation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tiansi.annotation.domain.OriginVideo;
import com.tiansi.annotation.domain.Users;
import com.tiansi.annotation.domain.Video;
import com.tiansi.annotation.exception.ErrorCode;
import com.tiansi.annotation.exception.TiansiException;
import com.tiansi.annotation.mapper.VideoMapper;
import com.tiansi.annotation.service.VideoService;

import com.tiansi.annotation.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@EnableAsync
@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {
    @Autowired
    private VideoUtil videoUtil;

    @Override
    public boolean delete(List<Long> ids) {
        return removeByIds(ids);
    }

    @Override
    public Page find(Long id, Long trialId, String name, Long originVideoId, Integer tagged, Long tagger,
                     Date tagDateStart, Date tagDateEnd, Integer currentPage, Integer pageSize) {
        QueryWrapper<Video> queryWrapper = new QueryWrapper<>();
        if (id != null) {
            queryWrapper = queryWrapper.eq("id", id);
        }
        if (trialId != null) {
            queryWrapper = queryWrapper.eq("trial_id", trialId);
        }
        if (!StringUtils.isEmpty(name)) {
            queryWrapper = queryWrapper.like("name", name);
        }
        if (originVideoId != null) {
            queryWrapper = queryWrapper.eq("origin_video_id", originVideoId);
        }
        if (tagged != null) {
            queryWrapper = queryWrapper.eq("tagged", tagged);
        }
        if (tagger != null) {
            queryWrapper = queryWrapper.eq("tagger", tagger);
        }
        if (tagDateStart != null) {
            queryWrapper = queryWrapper.ge("tag_date", tagDateStart);
        }
        if (tagDateEnd != null) {
            queryWrapper = queryWrapper.le("tag_date", tagDateEnd);
        }
        currentPage = currentPage != null && currentPage > 0 ? currentPage : 1;
        pageSize = pageSize != null && pageSize > 0 ? pageSize : 10;
        Page<Video> page = new Page<>(currentPage, pageSize);
        return (Page) page(page, queryWrapper);
    }


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
    public boolean segment(Video segmentVideo, Users processor) throws TiansiException {
        if (segmentVideo == null || segmentVideo.getId() == null) {
            throw new TiansiException(ErrorCode.INVALID_PARAMETER, "video and videoId can not be null !");
        }
        Video video = getById(segmentVideo.getId());
        if (video == null) {
            throw new TiansiException(ErrorCode.ENTITY_NOT_EXIST, "Video whose id is " + segmentVideo.getId() + " doesn't exist!");
        }
        video.setClipsInfo(segmentVideo.getClipsInfo());
        video.setTagger(processor.getId());
        video.setTagged(1);
        video.setTagDate(new Date());
        boolean result = updateById(video);
        if (result) {
            try {
                videoUtil.cutVideo(video);
            } catch (TiansiException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

}
