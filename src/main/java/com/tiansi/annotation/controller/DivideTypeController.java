package com.tiansi.annotation.controller;

import com.tiansi.annotation.domain.Users;
import com.tiansi.annotation.domain.body.DivideTypeRequestBody;
import com.tiansi.annotation.exception.TiansiException;
import com.tiansi.annotation.model.TiansiResponseBody;
import com.tiansi.annotation.service.DivideTypeService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/type")
@Api(value = "原始视频分割类型接口")
public class DivideTypeController {
    @Autowired
    private DivideTypeService divideTypeService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "添加分割类型")
    public TiansiResponseBody add(@RequestBody @ApiParam(name = "DivideTypeRequestBody对象", value = "传入JSON格式", required = true)
                                          DivideTypeRequestBody divideTypeRequestBody, Authentication authentication) {
        return new TiansiResponseBody(divideTypeService.addType(divideTypeRequestBody.toDivideType(), (Users) authentication.getPrincipal()));
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除分割类型")
    @ApiImplicitParam(name = "ids", value = "分割类型ID", paramType = "query", dataType = "List<Long>")
    public TiansiResponseBody delete(@RequestParam List<Long> ids) {
        return new TiansiResponseBody(divideTypeService.delete(ids));
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiOperation(value = "更改分割类型")
    public TiansiResponseBody update(@RequestBody @ApiParam(name = "DivideTypeRequestBody对象", value = "传入JSON格式", required = true)
                                             DivideTypeRequestBody divideTypeRequestBody, Authentication authentication) {
        try {
            return new TiansiResponseBody(divideTypeService.update(divideTypeRequestBody.toDivideType(), (Users) authentication.getPrincipal()));
        } catch (TiansiException e) {
            e.printStackTrace();
            return new TiansiResponseBody(e);
        }
    }

    @RequestMapping(value = "/find", method = RequestMethod.GET)
    @ApiOperation(value = "查找分割类型")
    public TiansiResponseBody find() {
        return new TiansiResponseBody(divideTypeService.find());
    }
}
