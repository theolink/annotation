package com.tiansi.annotation.domain;

import lombok.Data;

@Data
public class Video {
	private int id;
	private int trialId;
	private String address;
	private int length;
	private int tagged;
	private int tagger;
}
