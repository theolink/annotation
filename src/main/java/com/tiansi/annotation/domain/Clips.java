package com.tiansi.annotation.domain;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

@Data
public class Clips {
	private Long id;
	private String name;
	private Long videoId;
	private Integer frameNum;
	private String address;
	private String xmlAddress;
	private String tag;
	private Integer tagged;
	private Long tagger;

	@TableLogic
	private Integer isDeleted;
}
