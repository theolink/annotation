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

    boolean segment(Video segmentVideo, Users processor) throws TiansiException;

    /**
     * 分配任务
     *
     * @param amount 任务数量
     * @return 是否成功
     */
    int assign(Integer amount, Users users);
}
