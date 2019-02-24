package com.tiansi.annotation.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tiansi.annotation.domain.Users;
import com.tiansi.annotation.domain.Video;
import com.tiansi.annotation.exception.TiansiException;

import java.util.Date;
import java.util.List;

public interface VideoService extends IService<Video> {

    boolean delete(List<Long> ids);

    Page find(Long id, Long trialId, String name, Long originVideoId, Integer tagged, Long tagger, Date tagDateStart,
              Date tagDateEnd, Integer currentPage, Integer pageSize);

    List<Video> findAll();

    List<Video> findUntagged();

    List<Video> findSomeones(Long userId);

    List<Video> findSomeonesTagged(Long userId);

    List<Video> findSomeonesTagging(Long userId);

    boolean segment(Video segmentVideo, Users processor) throws TiansiException;

}
