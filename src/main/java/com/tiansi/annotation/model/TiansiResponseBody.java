package com.tiansi.annotation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TiansiResponseBody implements Serializable {
    private String status = "200";
    private String msg = "OK";
    private Object result;
    private String jwtToken;

    public TiansiResponseBody(Object result) {
        this.result = result;
    }

    public TiansiResponseBody(String status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public TiansiResponseBody(int errorCode, String msg) {
        this.status = String.valueOf(errorCode);
        this.msg = msg;
    }
}
