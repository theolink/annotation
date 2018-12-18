package com.tiansi.annotation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tiansi.annotation.domain.Video;

import java.util.List;

public interface VideoService extends IService<Video> {
	List<Video> findAll();

	List<Video> findUntagged();

	List<Video> findSomeones(int userId);

	List<Video> findSomeonesTagged(int userId);

	List<Video> findSomeonesTagging(int userId);

	String interceptFrame(String videoPath, String outputPath, int start, int end, int step);

	void cutVideo(Video video);
}
