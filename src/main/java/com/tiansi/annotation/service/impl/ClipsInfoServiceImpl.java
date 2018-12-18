package com.tiansi.annotation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tiansi.annotation.domain.ClipsInfo;
import com.tiansi.annotation.domain.Video;
import com.tiansi.annotation.mapper.ClipsInfoMapper;
import com.tiansi.annotation.service.ClipsInfoService;
import com.tiansi.annotation.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClipsInfoServiceImpl extends ServiceImpl<ClipsInfoMapper, ClipsInfo> implements ClipsInfoService {
	@Override
	public Map<Integer, Integer> findByVideoId(int videoId) {
		return toMap(getListByVideoId(videoId));
	}

	@Override
	public List<ClipsInfo> getListByVideoId(int videoId) {
		return this.list(new QueryWrapper<ClipsInfo>().eq("video_id", videoId));
	}

	@Override
	public Map<Integer, Integer> toMap(List<ClipsInfo> clipsInfos) {
		Map<Integer, Integer> clipsInfoMap = new HashMap<>();
		clipsInfos.forEach(clipsInfo -> clipsInfoMap.put(clipsInfo.getStartTime(), clipsInfo.getEndTime()));
		return clipsInfoMap;
	}

	@Override
	@SuppressWarnings(value = "unchecked")
	public boolean save(int videoId, Map clipsInfos) {
		List<ClipsInfo> clipsInfoList = new ArrayList<>();
		clipsInfos.forEach((key, value) -> {
			ClipsInfo clipsInfo = new ClipsInfo(videoId, (int) key, (int) value);
			clipsInfoList.add(clipsInfo);
		});
		return this.saveBatch(clipsInfoList);
	}
}
