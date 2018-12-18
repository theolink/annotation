package com.tiansi.annotation.domain;

import lombok.Data;

import java.util.Date;

@Data
public class Trial {
	private int id;
	private String name;
	private int videoNum;
	private Date uploadDate;
	private int uploader;
}
