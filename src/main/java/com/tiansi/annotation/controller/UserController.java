package com.tiansi.annotation.controller;

import com.tiansi.annotation.domain.Users;
import com.tiansi.annotation.domain.body.UserRequestBody;
import com.tiansi.annotation.exception.ErrorCode;
import com.tiansi.annotation.exception.TiansiException;
import com.tiansi.annotation.model.TiansiResponseBody;
import com.tiansi.annotation.service.UserService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@Api(value = "用户接口")
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "添加用户")
    public TiansiResponseBody add(@RequestBody @ApiParam(name = "UserRequestBody对象", value = "传入JSON格式", required = true)
                                          UserRequestBody userRequestBody) {
        try {
            return new TiansiResponseBody(userService.add(userRequestBody.toUsers()));
        } catch (TiansiException e) {
            e.printStackTrace();
            return new TiansiResponseBody(e);
        }
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除用户")
    @ApiImplicitParam(name = "id", value = "用户Id", paramType = "query", dataType = "Long")
    public TiansiResponseBody delete(@RequestParam Long id, Authentication authentication) {
        try {
            return new TiansiResponseBody(userService.delete(id, (Users) authentication.getPrincipal()));
        } catch (TiansiException e) {
            e.printStackTrace();
            return new TiansiResponseBody(e);
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiOperation(value = "更改用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户Id", paramType = "query", dataType = "Long", required = true),
            @ApiImplicitParam(name = "username", value = "用户名", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "role", value = "角色,管理员可改", paramType = "query", dataType = "String")
    })
    public TiansiResponseBody update(@RequestBody Map param, Authentication authentication) {
        Long id = Long.valueOf(param.get("id").toString());
        String username = param.get("username").toString();
        String role = param.get("role").toString();
        try {
            return new TiansiResponseBody(userService.update(id, username, role, (Users) authentication.getPrincipal()));
        } catch (TiansiException e) {
            e.printStackTrace();
            return new TiansiResponseBody(e);
        }
    }

    @RequestMapping(value = "/changePwd", method = RequestMethod.POST)
    @ApiOperation(value = "更改密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户Id", paramType = "query", dataType = "Long", required = true),
            @ApiImplicitParam(name = "oldPwd", value = "旧密码,管理员可不需", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "newPwd", value = "新密码", paramType = "query", dataType = "String", required = true)
    })
    public TiansiResponseBody changePwd(@RequestBody Map param, Authentication authentication) {
        Long id = Long.valueOf(param.get("id").toString());
        String oldPwd = param.get("oldPwd").toString();
        String newPwd = param.get("newPwd").toString();
        if (StringUtils.isEmpty(newPwd)) {
            return new TiansiResponseBody(new TiansiException(ErrorCode.INVALID_PARAMETER, "NewPwd can not be null !"));
        }
        try {
            return new TiansiResponseBody(userService.changePassword(id, oldPwd, newPwd, (Users) authentication.getPrincipal()));
        } catch (TiansiException e) {
            e.printStackTrace();
            return new TiansiResponseBody(e);
        }
    }

    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    @ApiOperation(value = "重置密码")
    @ApiImplicitParam(name = "id", value = "用户Id", paramType = "query", dataType = "Long", required = true)
    public TiansiResponseBody reset(@RequestParam Long id) {
        try {
            return new TiansiResponseBody(userService.reset(id));
        } catch (TiansiException e) {
            return new TiansiResponseBody(e);
        }
    }

    @RequestMapping(value = "/find", method = RequestMethod.GET)
    @ApiOperation(value = "查找用户")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "原始视频id", paramType = "query", dataType = "Long", required = false),

            @ApiImplicitParam(name = "username", value = "用户名", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "role", value = "角色", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "currentPage", value = "当前页码", paramType = "query", dataType = "int", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页数据量,默认为10", paramType = "query", dataType = "int")
    })
    public TiansiResponseBody find(@RequestParam(required = false) Long id, @RequestParam(required = false) String
            username,
                                   @RequestParam(required = false) String role, @RequestParam Integer currentPage,
                                   @RequestParam(required = false) Integer pageSize) {
        return new TiansiResponseBody(userService.find(id, username, role, currentPage, pageSize));
    }
}

