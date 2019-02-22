package com.tiansi.annotation.domain;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

@Data
public class Clips {
	private int id;
	private String name;
	private int videoId;
	private Long frameNum;
	private String address;
	private String xmlAddress;
	private String tag;
	private int tagged;
	private int tagger;

	@TableLogic
	private int isDeleted;
}
