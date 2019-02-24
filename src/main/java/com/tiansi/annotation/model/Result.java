package com.tiansi.annotation.model;

import lombok.NoArgsConstructor;

import java.util.HashMap;

@NoArgsConstructor
public class Result extends HashMap<String, Object> {

    public Result(String key, Object value) {
        super();
        this.put(key, value);
    }

    public Result add(String key, Object value) {
        this.put(key, value);
        return this;
    }
}
