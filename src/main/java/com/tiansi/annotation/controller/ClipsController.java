package com.tiansi.annotation.controller;

import com.tiansi.annotation.domain.Clips;
import com.tiansi.annotation.service.ClipsService;
import com.tiansi.annotation.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clips")
@Api(value = "视频片段接口")
public class ClipsController {
    @Autowired
    private ClipsService clipsService;

    @RequestMapping(value = "/findById", method = RequestMethod.GET)
    @ApiOperation(value = "根据区间Id获取区间")
    @ApiImplicitParam(name = "id", value = "区间id", required = true, dataType = "Integer")
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
    @ApiImplicitParam(name = "videoId", value = "视频id", required = true, dataType = "Integer")
    public Result getByVideoId(@RequestParam() int videoId) {
        return new Result("clips", clipsService.findByVideoId(videoId));
    }

    @RequestMapping(value = "/findByVideoIdUntagged", method = RequestMethod.GET)
    @ApiOperation(value = "根据videoId获得未标记区间")
    @ApiImplicitParam(name = "videoId", value = "视频id", required = true, dataType = "Integer")
    public Result getByVideoIdUntagged(@RequestParam() int videoId) {
        return new Result("clips", clipsService.findByVideoIdUntagged(videoId));
    }

    @RequestMapping(value = "/tag", method = RequestMethod.POST)
    @ApiOperation(value = "标记，保存标记信息")
    public Result tag(@RequestBody @ApiParam(value = "区间对象，其中id、tagger、tag字段为必须，tag为前端标签、框的json形式") Clips clips) {
        if (clips == null || clips.getId() <= 0)
            return new Result("SaveSuccessful", false).add("msg", "参数错误");
        if (clips.getTagger() <= 0) {
            return new Result("SaveSuccessful", false).add("msg", "非法tagger");
        }
        return new Result("SaveSuccessful", clipsService.tag(clips));
    }

    @RequestMapping(value = "/clear", method = RequestMethod.GET)
    @ApiOperation(value = "清除标签及框信息")
    @ApiImplicitParam(name = "id", value = "区间id", required = true, dataType = "Integer")
    public Result clear(@RequestParam() int id) {
        return new Result("clips", clipsService.clear(id));
    }

    @RequestMapping(value = "/clearBatch", method = RequestMethod.GET)
    @ApiOperation(value = "批量清除标签及框信息")
    @ApiImplicitParam(name = "ids", value = "区间id列表", required = true, dataType = "Integer")
    public Result clearBatch(@RequestBody @ApiParam(value = "id列表") List<Integer> ids) {
        return new Result("clips", clipsService.clearBatch(ids));
    }
}
