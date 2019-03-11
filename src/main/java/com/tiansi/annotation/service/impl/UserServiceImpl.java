package com.tiansi.annotation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tiansi.annotation.domain.Users;
import com.tiansi.annotation.mapper.UsersMapper;
import com.tiansi.annotation.service.UserService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserServiceImpl extends ServiceImpl<UsersMapper, Users> implements UserService {
    @Override
    public Users loadUserByUsername(String username) throws UsernameNotFoundException {
        Users users = findByUsername(username);
        if (users == null) {
            throw new UsernameNotFoundException("User is not exist !");
        }
        return users;
    }

    public Users findByUsername(String username) {
        return getOne(new QueryWrapper<Users>().eq("username", username));
    }
}
