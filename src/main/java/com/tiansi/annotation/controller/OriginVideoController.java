package com.tiansi.annotation.controller;

import com.tiansi.annotation.domain.Users;
import com.tiansi.annotation.service.OriginVideoService;
import com.tiansi.annotation.util.TiansiResponseBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/originVideo")
@Api(value = "原始视频接口")
public class OriginVideoController {
    @Autowired
    private OriginVideoService originVideoService;
    private Users users = new Users(1L, "nmsl", "123456", "ADMIN", 0);

    @RequestMapping(value = "/scan", method = RequestMethod.GET)
    @ApiOperation(value = "扫描视频")
    public TiansiResponseBody scan() {
        return new TiansiResponseBody(originVideoService.scan(users));
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    @ApiOperation(value = "扫描视频")
    @ApiImplicitParam(name = "ids", value = "庭审ids", paramType = "query", dataType = "List<Long>")
    public TiansiResponseBody delete(@RequestParam List<Long> ids) {
        return new TiansiResponseBody(originVideoService.delete(ids));
    }

    @RequestMapping(value = "/find", method = RequestMethod.GET)
    @ApiOperation(value = "查找原始视频")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "原始视频id", paramType = "query", dataType = "Long", required = false),
            @ApiImplicitParam(name = "trialId", value = "庭审id", paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "name", value = "原始视频名称", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "uploader", value = "上传者id", paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "uploadDateStart", value = "上传时间范围起点 yyyy-MM-dd", paramType = "query", dataType = "Date"),
            @ApiImplicitParam(name = "uploadDateEnd", value = "上传时间范围终点 yyyy-MM-dd ", paramType = "query", dataType = "Date"),
            @ApiImplicitParam(name = "preDeal", value = "处理（分割）状态：0：未处理，1：处理中，2：已处理", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "preDealer", value = "处理者Id", paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "preDealDateStart", value = "处理时间范围起点 yyyy-MM-dd", paramType = "query", dataType = "Date"),
            @ApiImplicitParam(name = "preDealDateEnd", value = "处理时间范围终点 yyyy-MM-dd", paramType = "query", dataType = "Date"),
            @ApiImplicitParam(name = "currentPage", value = "当前页码", paramType = "query", dataType = "int", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页数据量,默认为10", paramType = "query", dataType = "int")
    })
    public TiansiResponseBody find(@RequestParam(required = false) Long id, @RequestParam(required = false) Long trialId,
                                   @RequestParam(required = false) String name, @RequestParam(required = false) Long uploader,
                                   @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(required = false) Date uploadDateStart,
                                   @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(required = false) Date uploadDateEnd,
                                   @RequestParam(required = false) Integer preDeal, @RequestParam(required = false) Long preDealer,
                                   @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(required = false) Date preDealDateStart,
                                   @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(required = false) Date preDealDateEnd,
                                   @RequestParam Integer currentPage, @RequestParam(required = false) Integer pageSize) {
        System.out.println(uploadDateStart);
        return new TiansiResponseBody(originVideoService.find(id, trialId, name, uploader, uploadDateStart,
                uploadDateEnd, preDeal, preDealer, preDealDateStart, preDealDateEnd, currentPage, pageSize));
    }

    @RequestMapping(value = "/divide", method = RequestMethod.GET)
    @ApiOperation(value = "分割视频")
    public void divide() {
        originVideoService.divide(users);
    }
}
