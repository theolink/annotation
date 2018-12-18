package com.tiansi.annotation.domain;

import lombok.Data;

@Data
public class ClipsInfo {
	private int id;
	private int videoId;
	private int startTime;
	private int endTime;

	public ClipsInfo() {
	}

	public ClipsInfo(int videoId, int startTime, int endTime) {
		setVideoId(videoId);
		setStartTime(startTime);
		setEndTime(endTime);
	}

	public ClipsInfo(int id, int videoId, int startTime, int endTime) {
		this.id = id;
		this.videoId = videoId;
		this.startTime = startTime;
		this.endTime = endTime;
	}
}
