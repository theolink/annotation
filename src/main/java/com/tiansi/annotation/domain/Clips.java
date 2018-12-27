package com.tiansi.annotation.domain;

import lombok.Data;

@Data
public class Clips {
	private int id;
	private int videoId;
	private Long frameNum;
	private String address;
	private String xmlAddress;
	private String tag;
	private int tagged;
	private int tagger;
}
