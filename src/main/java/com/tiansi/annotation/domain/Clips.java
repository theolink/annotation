package com.tiansi.annotation.domain;

import lombok.Data;

@Data
public class Clips {
	private int id;
	private int videoId;
	private int frameNum;
	private String address;
	private String xmlAddress;
	private int tagged;
	private int tagger;
}
