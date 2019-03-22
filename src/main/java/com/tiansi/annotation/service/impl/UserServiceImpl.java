package com.tiansi.annotation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tiansi.annotation.domain.OriginVideo;
import com.tiansi.annotation.domain.Users;
import com.tiansi.annotation.domain.body.UserRequestBody;
import com.tiansi.annotation.exception.ErrorCode;
import com.tiansi.annotation.exception.TiansiException;
import com.tiansi.annotation.mapper.UsersMapper;
import com.tiansi.annotation.service.UserService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    @Override
    public boolean add(Users users) throws TiansiException {
        if (users == null || users.getUsername() == null || users.getPassword() == null || users.getRole() == null) {
            throw new TiansiException(ErrorCode.INVALID_PARAMETER, "Parameters can not be null !");
        }
        if (!(users.getRole().equals("ADMIN") || users.getRole().equals("USER"))) {
            throw new TiansiException(ErrorCode.INVALID_PARAMETER, "Invalid role !");
        }
        if (findByUsername(users.getUsername()) != null) {
            throw new TiansiException(ErrorCode.INVALID_PARAMETER, "User with name " + users.getUsername() + " has been exist !");
        }
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        users.setPassword(bCryptPasswordEncoder.encode(users.getPassword()));
        users.setId(null);
        return save(users);
    }

    //TODO 删除后系列操作
    @Override
    public boolean delete(Long id, Users users) throws TiansiException {
        if (!(id.equals(users.getId()) || users.getRole().equals("ADMIN"))) {
            throw new TiansiException(ErrorCode.LIMITED_AUTHORITY, "Limited Authority !");
        }
        return removeById(id);
    }

    @Override
    public boolean update(Long id, String username, String role, Users users) throws TiansiException {
        if (!(id.equals(users.getId()) || users.getRole().equals("ADMIN"))) {
            throw new TiansiException(ErrorCode.LIMITED_AUTHORITY, "Limited Authority !");
        }
        Users beforeUpdate = getById(id);
        if (beforeUpdate == null) {
            throw new TiansiException(ErrorCode.ENTITY_NOT_EXIST, "User with id " + id + " is not exist !");
        }
        if (!StringUtils.isEmpty(username)) {
            beforeUpdate.setUsername(username);
        }
        if (users.getRole().equals("ADMIN") && (!StringUtils.isEmpty(role)) && (role.equals("ADMIN") || role.equals("USER"))) {
            beforeUpdate.setRole(role);
        }
        return updateById(beforeUpdate);
    }

    @Override
    public boolean changePassword(Long id, String oldPwd, String newPwd, Users users) throws TiansiException {
        Users beforeUpdate = getById(id);
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        if (beforeUpdate == null) {
            throw new TiansiException(ErrorCode.ENTITY_NOT_EXIST, "User with id " + id + " is not exist !");
        }
        if (users.getRole().equals("ADMIN")) {
            beforeUpdate.setPassword(bCryptPasswordEncoder.encode(newPwd));
        } else {
            if (bCryptPasswordEncoder.encode(oldPwd).equals(beforeUpdate.getPassword())) {
                beforeUpdate.setPassword(bCryptPasswordEncoder.encode(newPwd));
            } else {
                throw new TiansiException(ErrorCode.LIMITED_AUTHORITY, "Limited Authority !");
            }
        }
        return updateById(beforeUpdate);
    }

    @Override
    public Page find(Long id, String username, String role, Integer currentPage, Integer pageSize) {
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        if (id != null) {
            queryWrapper = queryWrapper.eq("id", id);
        }
        if (!StringUtils.isEmpty(username)) {
            queryWrapper = queryWrapper.like("username", username);
        }
        if (!StringUtils.isEmpty(role)) {
            queryWrapper = queryWrapper.eq("role", role);
        }
        currentPage = currentPage != null && currentPage > 0 ? currentPage : 1;
        pageSize = pageSize != null && pageSize > 0 ? pageSize : 10;

        Page<Users> page = new Page<>(currentPage, pageSize);
        page = (Page<Users>) page(page, queryWrapper);
        Page<UserRequestBody> userRequestBodyPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        userRequestBodyPage.setPages(page.getPages()).setRecords(UserRequestBody.fromUsers(page.getRecords()));
        return userRequestBodyPage;
    }
}
