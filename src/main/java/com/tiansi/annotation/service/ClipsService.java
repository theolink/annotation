package com.tiansi.annotation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tiansi.annotation.domain.Clips;

import java.util.List;

public interface ClipsService extends IService<Clips> {
    List<Clips> findByVideoId(int videoId);
    List<Clips> findByVideoIdUntagged(int videoId);
    List<Clips> findAll();
    List<Clips> findAllUntagged();
    boolean deleteByVideoId(int videoId);
    boolean tag(Clips clips);
    boolean clear(int id);
    int clearBatch(List<Integer> ids);
}
