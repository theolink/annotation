package com.tiansi.annotation.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tiansi.annotation.domain.Clips;
import com.tiansi.annotation.domain.Users;
import com.tiansi.annotation.exception.TiansiException;

import java.util.Date;
import java.util.List;

public interface ClipsService extends IService<Clips> {
    boolean delete(List<Long> ids);

    boolean deleteByVideoId(Long videoId);

    Page find(Long id, Long videoId, String name, Integer tagged, Long tagger, Date tagDateStart,
              Date tagDateEnd, Integer currentPage, Integer pageSize);

    List<Clips> findByVideoId(Long videoId);

    List<Clips> findByVideoIdUntagged(Long videoId);

    List<Clips> findAll();

    List<Clips> findAllUntagged();

    boolean tag(Clips clips, Users processor) throws TiansiException;

    boolean clear(Long id) throws TiansiException;

    int clearBatch(List<Long> ids) throws TiansiException;
}
