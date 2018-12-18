package com.tiansi.annotation.util;

import java.util.HashMap;

public class Result<T> extends HashMap<String,T> {
	public Result() {
	}

	public Result(String key, T value) {
		super();
		this.put(key, value);
	}
}
