package com.tiansi.annotation.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tiansi.annotation.domain.Users;
import com.tiansi.annotation.exception.TiansiException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService extends IService<Users>, UserDetailsService {
    Users loadUserByUsername(String username) throws UsernameNotFoundException;

    Users findByUsername(String username);

    boolean add(Users users) throws TiansiException;

    boolean delete(Long id, Users users) throws TiansiException;

    boolean update(Long id, String username, String role, Users users) throws TiansiException;

    boolean changePassword(Long id, String oldPwd, String newPwd, Users users) throws TiansiException;

    Page find(Long id, String username, String role, Integer currentPage, Integer pageSize);

}
