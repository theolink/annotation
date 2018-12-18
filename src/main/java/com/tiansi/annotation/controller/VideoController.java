package com.tiansi.annotation.controller;

import com.tiansi.annotation.service.VideoService;
import com.tiansi.annotation.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
		return new Result<List>("videos", videoService.findAll());
	}

	@RequestMapping(value = "/getUntagged", method = RequestMethod.GET)
	@ApiOperation(value = "获取未标注AB区间的视频")
	public Result getUntagged() {
		return new Result<List>("videos", videoService.findUntagged());
	}

	@RequestMapping(value = "/getSomeones", method = RequestMethod.GET)
	@ApiOperation(value = "获取指定用户标记过AB区间及标记AB区间中的视频")
	@ApiImplicitParam(name = "id", value = "用户id", dataType = "Integer")
	public Result getSomeones(@RequestParam() int id) {
		return new Result<List>("videos", videoService.findSomeones(id));
	}

	@RequestMapping(value = "/getSomeonesTagged", method = RequestMethod.GET)
	@ApiOperation(value = "获取指定用户标记过AB区间的视频")
	@ApiImplicitParam(name = "id", value = "用户id", dataType = "Integer")
	public Result getSomeonesTagged(@RequestParam() int id) {
		return new Result<List>("videos", videoService.findSomeonesTagged(id));
	}

	@RequestMapping(value = "/getSomeonesTagging", method = RequestMethod.GET)
	@ApiOperation(value = "获取指定用户标记AB区间中的视频")
	@ApiImplicitParam(name = "id", value = "用户id", dataType = "Integer")
	public Result getSomeonesTagging(@RequestParam() int id) {
		return new Result<List>("videos", videoService.findSomeonesTagging(id));
	}
}
