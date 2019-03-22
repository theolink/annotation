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

    boolean tag(Clips clips, Users processor) throws TiansiException;

    boolean clear(Long id, Users users) throws TiansiException;

    int clearBatch(List<Long> ids, Users users) throws TiansiException;

    /**
     * 分配任务
     *
     * @param amount 任务数量
     * @return 是否成功
     */
    int assign(Integer amount, Users users);
}
