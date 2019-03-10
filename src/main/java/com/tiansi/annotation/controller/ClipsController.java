package com.tiansi.annotation.controller;

import com.tiansi.annotation.domain.Clips;
import com.tiansi.annotation.domain.Users;
import com.tiansi.annotation.domain.body.ClipsRequestBody;
import com.tiansi.annotation.exception.TiansiException;
import com.tiansi.annotation.model.TiansiResponseBody;
import com.tiansi.annotation.service.ClipsService;
import com.tiansi.annotation.model.Result;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/clips")
@Api(value = "视频片段接口")
public class ClipsController {
    @Autowired
    private ClipsService clipsService;
    private Users users = new Users(1L, "nmsl", "123456", "ADMIN", 0);

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除片段")
    @ApiImplicitParam(name = "ids", value = "庭审ids", paramType = "query", dataType = "List<Long>")
    public TiansiResponseBody delete(@RequestParam List<Long> ids) {
        return new TiansiResponseBody(clipsService.delete(ids));
    }

    @RequestMapping(value = "/find", method = RequestMethod.GET)
    @ApiOperation(value = "查找片段")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "片段id", paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "videoId", value = "视频id", paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "name", value = "片段名称", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "tagged", value = "标签标注状态：0：未标注，1：标注中，2：已标注", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "tagger", value = "标注者Id", paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "tagDateStart", value = "标注时间范围起点 yyyy-MM-dd", paramType = "query", dataType = "Date"),
            @ApiImplicitParam(name = "tagDateEnd", value = "标注时间范围终点 yyyy-MM-dd ", paramType = "query", dataType = "Date"),
            @ApiImplicitParam(name = "currentPage", value = "当前页码", paramType = "query", dataType = "int", required = true),
            @ApiImplicitParam(name = "pageSize", value = "每页数据量,默认为10", paramType = "query", dataType = "int")
    })
    public TiansiResponseBody find(@RequestParam(required = false) Long id, @RequestParam(required = false) Long videoId,
                                   @RequestParam(required = false) String name, @RequestParam(required = false) Integer tagged,
                                   @RequestParam(required = false) Long tagger,
                                   @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(required = false) Date tagDateStart,
                                   @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(required = false) Date tagDateEnd,
                                   @RequestParam Integer currentPage, @RequestParam(required = false) Integer pageSize) {
        return new TiansiResponseBody(clipsService.find(id, videoId, name, tagged, tagger, tagDateStart, tagDateEnd,
                currentPage, pageSize));
    }

    @RequestMapping(value = "/findById", method = RequestMethod.GET)
    @ApiOperation(value = "根据区间Id获取区间")
    @ApiImplicitParam(name = "id", value = "区间id", paramType = "query", required = true, dataType = "Integer")
    public Result getById(@RequestParam() int id) {
        return new Result("clips", clipsService.getById(id));
    }

    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    @ApiOperation(value = "获取所有区间")
    public Result getAll() {
        return new Result("clips", clipsService.findAll());
    }

    @RequestMapping(value = "/findAllUntagged", method = RequestMethod.GET)
    @ApiOperation(value = "获取所有未标记区间")
    public Result getAllUntagged() {
        return new Result("clips", clipsService.findAllUntagged());
    }


    @RequestMapping(value = "/findByVideoId", method = RequestMethod.GET)
    @ApiOperation(value = "根据videoId获取区间")
    @ApiImplicitParam(name = "videoId", value = "视频id", paramType = "query", required = true, dataType = "Long")
    public Result getByVideoId(@RequestParam() Long videoId) {
        return new Result("clips", clipsService.findByVideoId(videoId));
    }

    @RequestMapping(value = "/findByVideoIdUntagged", method = RequestMethod.GET)
    @ApiOperation(value = "根据videoId获得未标记区间")
    @ApiImplicitParam(name = "videoId", value = "视频id", paramType = "query", required = true, dataType = "Long")
    public Result getByVideoIdUntagged(@RequestParam() Long videoId) {
        return new Result("clips", clipsService.findByVideoIdUntagged(videoId));
    }

    @RequestMapping(value = "/tag", method = RequestMethod.POST)
    @ApiOperation(value = "标记，保存标记信息")
    public TiansiResponseBody tag(@RequestBody @ApiParam(name = "ClipsRequestBody对象", value = "传入JSON格式", required = true)
                                          ClipsRequestBody clipsRequestBody) {
        System.out.println("tag:"+clipsRequestBody.getTag());
        try {
            return new TiansiResponseBody(clipsService.tag(clipsRequestBody.toClips(), users));
        } catch (TiansiException e) {
            return new TiansiResponseBody(e.getErrorCode(), e.getMessage());
        }
    }

    @RequestMapping(value = "/clear", method = RequestMethod.GET)
    @ApiOperation(value = "清除标签及框信息")
    @ApiImplicitParam(name = "id", value = "片段id", required = true, paramType = "query", dataType = "Long")
    public TiansiResponseBody clear(@RequestParam Long id) {
        try {
            return new TiansiResponseBody(clipsService.clear(id));
        } catch (TiansiException e) {
            return new TiansiResponseBody(e.getErrorCode(), e.getMessage());
        }
    }

    @RequestMapping(value = "/clearBatch", method = RequestMethod.GET)
    @ApiOperation(value = "批量清除标签及框信息")
    @ApiImplicitParam(name = "ids", value = "区间id列表", required = true, dataType = "Long")
    public TiansiResponseBody clearBatch(@RequestBody @ApiParam(value = "id列表") List<Long> ids) {
        try {
            return new TiansiResponseBody(clipsService.clearBatch(ids));
        } catch (TiansiException e) {
            return new TiansiResponseBody(e.getErrorCode(), e.getMessage());
        }
    }
}
