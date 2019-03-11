package com.tiansi.annotation.security;

import com.alibaba.fastjson.JSON;
import com.tiansi.annotation.model.TiansiResponseBody;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TiansiLogoutSuccessHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        TiansiResponseBody tiansiResponseBody = new TiansiResponseBody();
        tiansiResponseBody.setStatus("100");
        tiansiResponseBody.setMsg("Logout Success!");
        httpServletResponse.getWriter().write(JSON.toJSONString(tiansiResponseBody));
    }
}
