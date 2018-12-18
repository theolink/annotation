package com.tiansi.annotation.controller;

import com.tiansi.annotation.service.ClipsInfoService;
import com.tiansi.annotation.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/clipsInfo")
@Api(value = "视频区间接口")
public class ClipsInfoController {
	@Autowired
	private ClipsInfoService clipsInfoService;

	@RequestMapping("/getByVideoId")
	@ApiOperation(value = "获取指定视频的AB区间信息")
	@ApiImplicitParam(name = "videoId", value = "视频ID", dataType = "Integer")
	public Map getByVideoId(@RequestParam int videoId) {
		return clipsInfoService.findByVideoId(videoId);
	}

	@RequestMapping("/save/{videoId}")
	@ApiOperation(value = "保存视频的AB区间信息")
	@ApiImplicitParam(name = "videoId", value = "视频ID", dataType = "Integer")
	public Result save(@PathVariable int videoId, @RequestBody Map clipsInfos) {
		boolean result = clipsInfoService.save(videoId, clipsInfos);
		return new Result<>("SaveSuccessful", result);
	}
}
