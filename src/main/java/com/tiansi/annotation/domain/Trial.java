package com.tiansi.annotation.domain;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.util.Date;

@Data
public class Trial {
	private Long id;
	private String name;
	private Integer videoNum;
	private Date uploadDate;
	private Long uploader;

	@TableLogic
	private Integer isDeleted;
}
