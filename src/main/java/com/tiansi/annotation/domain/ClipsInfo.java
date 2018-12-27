package com.tiansi.annotation.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClipsInfo {
	private int id;
	private int videoId;
	private int startTime;
	private int endTime;

	public ClipsInfo(int videoId, int startTime, int endTime) {
		setVideoId(videoId);
		setStartTime(startTime);
		setEndTime(endTime);
	}
}
