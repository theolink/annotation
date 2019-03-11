package com.tiansi.annotation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tiansi.annotation.domain.Users;
import com.tiansi.annotation.exception.TiansiException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService extends IService<Users>, UserDetailsService {
    Users loadUserByUsername(String username) throws UsernameNotFoundException;

    Users findByUsername(String username);



}
