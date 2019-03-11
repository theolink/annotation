package com.tiansi.annotation.security;

import com.alibaba.fastjson.JSON;
import com.tiansi.annotation.model.TiansiResponseBody;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TiansiAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        TiansiResponseBody tiansiResponseBody = new TiansiResponseBody();
        tiansiResponseBody.setStatus("400");
        tiansiResponseBody.setMsg("Login Failure!");
        httpServletResponse.getWriter().write(JSON.toJSONString(tiansiResponseBody));
    }
}
