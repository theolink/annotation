package com.tiansi.annotation.domain;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.util.Date;

@Data
public class Trial {
	private int id;
	private String name;
	private int videoNum;
	private Date uploadDate;
	private int uploader;

	@TableLogic
	private int isDeleted;
}
