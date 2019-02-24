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
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/video")
@Api(value = "视频接口")
public class VideoController {
    @Autowired
    private VideoService videoService;
    private Users users = new Users(1L, "nmsl", "123456", "ADMIN", 0);

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
            @ApiImplicitParam(name = "tagged", value = "区间标记状态：0：未标记，1：已标记，剪切中，2：已剪切", paramType = "query", dataType = "Integer"),
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

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "获取所有视频")
    public Result getAll() {
        return new Result("videos", VideoRequestBody.fromVideos(videoService.findAll()));
    }

    @RequestMapping(value = "/getUntagged", method = RequestMethod.GET)
    @ApiOperation(value = "获取未标注AB区间的视频")
    public Result getUntagged() {
        return new Result("videos", VideoRequestBody.fromVideos(videoService.findUntagged()));
    }

    @RequestMapping(value = "/getSomeones", method = RequestMethod.GET)
    @ApiOperation(value = "获取指定用户标记过AB区间及标记AB区间中的视频")
    @ApiImplicitParam(name = "id", value = "用户id", paramType = "query", dataType = "Long")
    public Result getSomeones(@RequestParam() Long id) {
        return new Result("videos", VideoRequestBody.fromVideos(videoService.findSomeones(id)));
    }

    @RequestMapping(value = "/getSomeonesTagged", method = RequestMethod.GET)
    @ApiOperation(value = "获取指定用户标记过AB区间的视频")
    @ApiImplicitParam(name = "id", value = "用户id", paramType = "query", dataType = "Long")
    public Result getSomeonesTagged(@RequestParam() Long id) {
        return new Result("videos", VideoRequestBody.fromVideos(videoService.findSomeonesTagged(id)));
    }

    @RequestMapping(value = "/getSomeonesTagging", method = RequestMethod.GET)
    @ApiOperation(value = "获取指定用户标记AB区间中的视频")
    @ApiImplicitParam(name = "id", value = "用户id", paramType = "query", dataType = "Long")
    public Result getSomeonesTagging(@RequestParam() Long id) {
        return new Result("videos", VideoRequestBody.fromVideos(videoService.findSomeonesTagging(id)));
    }

    @RequestMapping(value = "/{videoId}", method = RequestMethod.GET)
    @ApiOperation(value = "获取指定id的视频")
    @ApiImplicitParam(name = "id", value = "用户id", dataType = "Integer")
    public Result get(@PathVariable int videoId) {
        return new Result("video", videoService.getById(videoId).requestBody());
    }

    @RequestMapping(value = "/segment", method = RequestMethod.POST)
    @ApiOperation(value = "分割区间，保存视频的AB区间信息")
    public TiansiResponseBody segment(@RequestBody @ApiParam(name = "VideoRequestBody对象", value = "传入JSON格式", required = true)
                                              VideoRequestBody videoRequestBody) {
        try {
            return new TiansiResponseBody(videoService.segment(videoRequestBody.toVideo(), users));
        } catch (TiansiException e) {
            return new TiansiResponseBody(e.getErrorCode(), e.getMessage());
        }
    }


}
