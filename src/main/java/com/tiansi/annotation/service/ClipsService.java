package com.tiansi.annotation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tiansi.annotation.domain.Clips;

import java.util.List;

public interface ClipsService extends IService<Clips> {
    List<Clips> findByVideoId(Long videoId);
    List<Clips> findByVideoIdUntagged(Long videoId);
    List<Clips> findAll();
    List<Clips> findAllUntagged();
    boolean deleteByVideoId(Long videoId);
    boolean tag(Clips clips);
    boolean clear(Long id);
    int clearBatch(List<Long> ids);
}
