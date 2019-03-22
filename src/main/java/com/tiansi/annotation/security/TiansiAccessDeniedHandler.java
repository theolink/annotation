package com.tiansi.annotation.security;

import com.alibaba.fastjson.JSON;
import com.tiansi.annotation.model.TiansiResponseBody;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TiansiAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        TiansiResponseBody tiansiResponseBody = new TiansiResponseBody();
        tiansiResponseBody.setStatus("300");
        tiansiResponseBody.setMsg("Access Denied!");
        httpServletResponse.getWriter().write(JSON.toJSONString(tiansiResponseBody));
    }
}
