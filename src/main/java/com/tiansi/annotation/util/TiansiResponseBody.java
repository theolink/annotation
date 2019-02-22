package com.tiansi.annotation.util;

import lombok.Data;

import java.io.Serializable;

@Data
public class TiansiResponseBody implements Serializable {
    private String status;
    private String msg;
    private Object result;
    private String jwtToken;
}
