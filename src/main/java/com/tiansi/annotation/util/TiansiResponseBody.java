package com.tiansi.annotation.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TiansiResponseBody implements Serializable {
    private String status="200";
    private String msg="OK";
    private Object result;
    private String jwtToken;

    public TiansiResponseBody(Object result) {
        this.result = result;
    }
}
