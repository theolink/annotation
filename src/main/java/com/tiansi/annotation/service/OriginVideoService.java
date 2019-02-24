package com.tiansi.annotation.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tiansi.annotation.domain.OriginVideo;
import com.tiansi.annotation.domain.Users;
import com.tiansi.annotation.exception.TiansiException;
import org.hibernate.validator.constraints.URL;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface OriginVideoService extends IService<OriginVideo> {
    /**
     * 扫描视频
     *
     * @param processor 执行者
     * @return 添加视频个数
     */
    int scan(Users processor);

    /**
     * 扫描原始视频数据
     *
     * @param path      扫描路径
     * @param processor 执行者
     * @return 添加视频个数
     */
    int scan(String path, Users processor) throws TiansiException;

    boolean delete(List<Long> originIds);

    /**
     * 条件查询
     *
     * @param id               id
     * @param trialId          trialId
     * @param name             name
     * @param uploader         uploader
     * @param uploadDateStart  uploadDateStart
     * @param uploadDateEnd    uploadDateEnd
     * @param preDeal          preDeal
     * @param preDealer        preDealer
     * @param preDealDateStart preDealDateStart
     * @param preDealDateEnd   preDealDateEnd
     * @return 查询结果
     */
    Page find(Long id, Long trialId, String name, Long uploader, Date uploadDateStart, Date uploadDateEnd, Integer preDeal,
              Long preDealer, Date preDealDateStart, Date preDealDateEnd, Integer currentPage, Integer pageSize);

    /**
     * 切割原始视频
     *
     * @param processor 执行者
     */
    void divide(Users processor);

    /**
     * 开始分割:将原始视频分割状态标记为分割中
     *
     * @param originId  原始视频ID
     * @param processor 执行者ID
     */
    void startDivide(Long originId, Users processor);

    /**
     * 开始分割:将原始视频分割状态标记为分割中
     *
     * @param originIds 原始视频ID
     * @param processor 执行者ID
     */
    void startDivide(List<Long> originIds, Users processor);

    /**
     * 结束分割:将原始视频分割状态标记为已分割
     *
     * @param originId 原始视频ID
     */
    void endDivide(Long originId);

    /**
     * 结束分割:将原始视频分割状态标记为已分割
     *
     * @param originIds 原始视频ID
     */
    void endDivide(List<Long> originIds);
}
