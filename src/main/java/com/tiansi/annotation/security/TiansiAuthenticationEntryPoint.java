package com.tiansi.annotation.security;

import com.alibaba.fastjson.JSON;
import com.tiansi.annotation.model.TiansiResponseBody;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TiansiAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        TiansiResponseBody tiansiResponseBody = new TiansiResponseBody();
        tiansiResponseBody.setStatus("000");
        tiansiResponseBody.setMsg("Need Authorities!");
        httpServletResponse.getWriter().write(JSON.toJSONString(tiansiResponseBody));
    }
}
