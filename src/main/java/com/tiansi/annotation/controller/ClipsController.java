package com.tiansi.annotation.controller;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clips")
@Api(value = "视频片段接口")
public class ClipsController {
}
