package com.tiansi.annotation.domain;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

@Data
public class Users {
	private int id;
	private String username;
	private String password;
	private String role;

	@TableLogic
	private int isDeleted;
}
