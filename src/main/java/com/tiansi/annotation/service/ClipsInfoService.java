package com.tiansi.annotation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tiansi.annotation.domain.ClipsInfo;

import java.util.List;
import java.util.Map;

public interface ClipsInfoService extends IService<ClipsInfo> {
	Map<Integer,Integer> findByVideoId(int videoId);
	List<ClipsInfo> getListByVideoId(int videoId);
	Map<Integer,Integer> toMap(List<ClipsInfo> clipsInfos);
	boolean save(int videoId,Map clipsInfos);
	boolean update(int videoId,Map clipsInfos);
}
