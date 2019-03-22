package com.tiansi.annotation.controller;

import com.tiansi.annotation.domain.Users;
import com.tiansi.annotation.service.OriginVideoService;
import com.tiansi.annotation.model.TiansiResponseBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/originVideo")
@Api(value = "原始视频接口")
public class OriginVideoController {
    @Autowired
    private OriginVideoService originVideoService;

    @RequestMapping(value = "/scan", method = RequestMethod.GET)
    @ApiOperation(value = "扫描原始视频")
    public TiansiResponseBody scan(Authentication authentication) {
        int num = originVideoService.scan((Users) authentication.getPrincipal());
        originVideoService.middleImg();
        return new TiansiResponseBody(num);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除原始视频")
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
            @ApiImplicitParam(name = "preDeal", value = "处理（分割）状态：0：未分配，1：已分配，未设置类型，2：已设置类型，未分割，3：分割中，4：已分割", paramType = "query", dataType = "Integer"),
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
        return new TiansiResponseBody(originVideoService.find(id, trialId, name, uploader, uploadDateStart,
                uploadDateEnd, preDeal, preDealer, preDealDateStart, preDealDateEnd, currentPage, pageSize));
    }

    @RequestMapping(value = "/divide", method = RequestMethod.GET)
    @ApiOperation(value = "分割原始视频")
    public TiansiResponseBody divide(Authentication authentication) {
        originVideoService.divide((Users) authentication.getPrincipal());
        return new TiansiResponseBody();
    }

    @RequestMapping(value = "/type", method = RequestMethod.GET)
    @ApiOperation(value = "设置原始视频分割类型")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "原始视频id", paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "typeId", value = "类型id", paramType = "query", dataType = "Long"),
    })
    public TiansiResponseBody type(@RequestParam Long id, @RequestParam Long typeId) {
        return new TiansiResponseBody(originVideoService.type(id, typeId));
    }

    @RequestMapping(value = "/types", method = RequestMethod.POST)
    @ApiOperation(value = "设置原始视频分割类型")
    @ApiImplicitParam(name = "typeList", value = "类型设置map; key: originVideoId, value: divideTypeId", paramType = "query", dataType = "Map<Long,Long>")
    public TiansiResponseBody type(@RequestBody Map<Long, Long> typeList) {
        return new TiansiResponseBody(originVideoService.type(typeList));
    }

    @RequestMapping(value = "/untyped", method = RequestMethod.GET)
    @ApiOperation(value = "取消原始视频分割类型")
    @ApiImplicitParam(name = "id", value = "原始视频id", paramType = "query", dataType = "Long")
    public TiansiResponseBody untyped(@RequestParam Long id, @RequestParam Long typeId) {
        return new TiansiResponseBody(originVideoService.type(id, typeId));
    }


    @RequestMapping(value = "/assign", method = RequestMethod.GET)
    @ApiOperation(value = "分配“设置类型”任务")
    @ApiImplicitParam(name = "amount", value = "分配任务数量", paramType = "query", dataType = "Integer")
    public TiansiResponseBody assign(@RequestParam Integer amount, Authentication authentication) {
        return new TiansiResponseBody(originVideoService.assign(amount, (Users) authentication.getPrincipal()));
    }
}
