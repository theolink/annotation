package com.tiansi.annotation.controller;

import com.tiansi.annotation.service.VideoService;
import com.tiansi.annotation.util.Result;
import com.tiansi.annotation.util.VideoClips;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/video")
@Api(value = "视频接口")
public class VideoController {
    @Autowired
    private VideoService videoService;

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "获取所有视频")
    public Result getAll() {
        return new Result("videos", VideoClips.fromVideos(videoService.findAll()));
    }

    @RequestMapping(value = "/getUntagged", method = RequestMethod.GET)
    @ApiOperation(value = "获取未标注AB区间的视频")
    public Result getUntagged() {
        return new Result("videos", VideoClips.fromVideos(videoService.findUntagged()));
    }

    @RequestMapping(value = "/getSomeones", method = RequestMethod.GET)
    @ApiOperation(value = "获取指定用户标记过AB区间及标记AB区间中的视频")
    @ApiImplicitParam(name = "id", value = "用户id", dataType = "Integer")
    public Result getSomeones(@RequestParam() int id) {
        return new Result("videos", VideoClips.fromVideos(videoService.findSomeones(id)));
    }

    @RequestMapping(value = "/getSomeonesTagged", method = RequestMethod.GET)
    @ApiOperation(value = "获取指定用户标记过AB区间的视频")
    @ApiImplicitParam(name = "id", value = "用户id", dataType = "Integer")
    public Result getSomeonesTagged(@RequestParam() int id) {
        return new Result("videos", VideoClips.fromVideos(videoService.findSomeonesTagged(id)));
    }

    @RequestMapping(value = "/getSomeonesTagging", method = RequestMethod.GET)
    @ApiOperation(value = "获取指定用户标记AB区间中的视频")
    @ApiImplicitParam(name = "id", value = "用户id", dataType = "Integer")
    public Result getSomeonesTagging(@RequestParam() int id) {
        return new Result("videos", VideoClips.fromVideos(videoService.findSomeonesTagging(id)));
    }

    @RequestMapping(value = "/{videoId}", method = RequestMethod.GET)
    @ApiOperation(value = "获取指定id的视频")
    @ApiImplicitParam(name = "id", value = "用户id", dataType = "Integer")
    public Result get(@PathVariable int videoId) {
        return new Result("video", videoService.getById(videoId).toVideoClips());
    }

    @RequestMapping(value = "/segment", method = RequestMethod.POST)
    @ApiOperation(value = "分割区间，保存视频的AB区间信息")
    public Result segment(@RequestBody @ApiParam(value = "视频对象，其中id、tagger、clipsInfo字段为必须，clipsInfo格式:[[A,B],[A,B]..]")
                               VideoClips videoClips) {
        if(videoClips==null||videoClips.getId()<=0){
            return new Result("SaveSuccessful", false).add("msg","非法参数");
        }
        if(videoClips.getTagger()<=0){
            return new Result("SaveSuccessful", false).add("msg","非法tagger");
        }
        boolean result = videoService.segment(videoClips.toVideo());
        return new Result("SaveSuccessful", result);
    }

}
