package com.tiansi.annotation.controller;

import com.tiansi.annotation.service.ClipsInfoService;
import com.tiansi.annotation.util.Result;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/clipsInfo")
@Api(value = "视频区间接口")
public class ClipsInfoController {
    @Autowired
    private ClipsInfoService clipsInfoService;

    @RequestMapping(value = "/getByVideoId", method = RequestMethod.GET)
    @ApiOperation(value = "获取指定视频的AB区间信息")
    @ApiImplicitParam(name = "videoId", value = "视频ID", dataType = "Integer")
    public Map getByVideoId(@RequestParam int videoId) {
        return clipsInfoService.findByVideoId(videoId);
    }

    @RequestMapping(value = "/save/{videoId}", method = RequestMethod.POST)
    @ApiOperation(value = "保存视频的AB区间信息")
    @ApiImplicitParam(name = "videoId", value = "视频ID", paramType = "path", dataType = "Integer")
    public Result save(@PathVariable int videoId,
                       @RequestBody @ApiParam(value = "区间信息Map:[[开始帧，结束帧],[开始帧，结束帧]，...]") Map clipsInfos) {
        boolean result = clipsInfoService.save(videoId, clipsInfos);
        return new Result("SaveSuccessful", result);
    }

    @RequestMapping(value = "/update/{videoId}", method = RequestMethod.POST)
    @ApiOperation(value = "更新视频的AB区间信息")
    @ApiImplicitParam(name = "videoId", value = "视频ID", dataType = "Integer")
    public Result update(@PathVariable int videoId,
                         @RequestBody @ApiParam(value = "区间信息Map:[[开始帧，结束帧],[开始帧，结束帧]，...]") Map clipsInfos) {
        boolean result = clipsInfoService.update(videoId, clipsInfos);
        return new Result("SaveSuccessful", result);
    }
}
