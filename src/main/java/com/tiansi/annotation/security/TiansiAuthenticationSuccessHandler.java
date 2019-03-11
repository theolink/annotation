package com.tiansi.annotation.security;

import com.alibaba.fastjson.JSON;
import com.tiansi.annotation.domain.Users;
import com.tiansi.annotation.model.TiansiResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class TiansiAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        TiansiResponseBody tiansiResponseBody = new TiansiResponseBody();
        tiansiResponseBody.setStatus("200");
        tiansiResponseBody.setMsg("Login Success!");

        Users users = (Users) authentication.getPrincipal();
        String jwtToken = jwtTokenUtil.generateToken(users);
        tiansiResponseBody.setJwtToken(jwtToken);

        httpServletResponse.getWriter().write(JSON.toJSONString(tiansiResponseBody));
    }
}
