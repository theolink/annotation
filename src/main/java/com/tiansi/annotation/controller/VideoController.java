package com.tiansi.annotation.controller;

import com.tiansi.annotation.domain.Users;
import com.tiansi.annotation.exception.TiansiException;
import com.tiansi.annotation.service.VideoService;
import com.tiansi.annotation.model.Result;
import com.tiansi.annotation.model.TiansiResponseBody;
import com.tiansi.annotation.domain.body.VideoRequestBody;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.jws.soap.SOAPBinding;
import java.security.Principal;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/video")
@Api(value = "视频接口")
public class VideoController {
    @Autowired
    private VideoService videoService;

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除视频")
    @ApiImplicitParam(name = "ids", value = "庭审ids", paramType = "query", dataType = "List<Long>")
    public TiansiResponseBody delete(@RequestParam List<Long> ids) {
        return new TiansiResponseBody(videoService.delete(ids));
    }

    @RequestMapping(value = "/find", method = RequestMethod.GET)
    @ApiOperation(value = "查找视频")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "视频id", paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "trialId", value = "庭审id", paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "name", value = "视频名称", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "originVideoId", value = "原始视频id", paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "tagged", value = "区间标记状态：3：未分配，0：已分配，未标记，1：已标记，剪切中，2：已剪切", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "tagger", value = "标记者Id", paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "tagDateStart", value = "标记时间范围起点 yyyy-MM-dd", paramType = "query", dataType = "Date"),
            @ApiImplicitParam(name = "tagDateEnd", value = "标记时间范围终点 yyyy-MM-dd ", paramType = "query", dataType = "Date"),
            @ApiImplicitParam(name = "currentPage", value = "当前页码", paramType = "query", dataType = "int", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页数据量,默认为10", paramType = "query", dataType = "int")
    })
    public TiansiResponseBody find(@RequestParam(required = false) Long id, @RequestParam(required = false) Long trialId,
                                   @RequestParam(required = false) String name, @RequestParam(required = false) Long originVideoId,
                                   @RequestParam(required = false) Integer tagged, @RequestParam(required = false) Long tagger,
                                   @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(required = false) Date tagDateStart,
                                   @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(required = false) Date tagDateEnd,
                                   @RequestParam Integer currentPage, @RequestParam(required = false) Integer pageSize) {
        return new TiansiResponseBody(videoService.find(id, trialId, name, originVideoId, tagged, tagger, tagDateStart,
                tagDateEnd, currentPage, pageSize));
    }

    @RequestMapping(value = "/segment", method = RequestMethod.POST)
    @ApiOperation(value = "分割区间，保存视频的AB区间信息")
    public TiansiResponseBody segment(@RequestBody @ApiParam(name = "VideoRequestBody对象", value = "传入JSON格式", required = true)
                                              VideoRequestBody videoRequestBody, Authentication authentication) {
        try {
            return new TiansiResponseBody(videoService.segment(videoRequestBody.toVideo(),(Users) authentication.getPrincipal()));
        } catch (TiansiException e) {
            return new TiansiResponseBody(e.getErrorCode(), e.getMessage());
        }
    }
    @RequestMapping(value = "/assign", method = RequestMethod.GET)
    @ApiOperation(value = "分配“设置类型”任务")
    @ApiImplicitParam(name = "amount", value = "分配任务数量", paramType = "query", dataType = "Integer")
    public TiansiResponseBody assign(@RequestParam Integer amount, Authentication authentication) {
        return new TiansiResponseBody(videoService.assign(amount, (Users) authentication.getPrincipal()));
    }

}
