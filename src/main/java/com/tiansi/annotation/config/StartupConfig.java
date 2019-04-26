package com.tiansi.annotation.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tiansi.annotation.domain.OriginVideo;
import com.tiansi.annotation.domain.Video;
import com.tiansi.annotation.exception.TiansiException;
import com.tiansi.annotation.service.OriginVideoService;
import com.tiansi.annotation.service.VideoService;
import com.tiansi.annotation.util.OriginVideoUtil;
import com.tiansi.annotation.util.VideoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@EnableAsync
public class StartupConfig implements ApplicationRunner {
@Autowired
private OriginVideoService originVideoService;
@Autowired
private VideoService videoService;
@Autowired
private OriginVideoUtil originVideoUtil;
@Autowired
private VideoUtil videoUtil;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<OriginVideo> undivided=originVideoService.list(new QueryWrapper<OriginVideo>().eq("pre_deal",3));
        List<Video> unSegment=videoService.list(new QueryWrapper<Video>().eq("tagged",1));
        originVideoUtil.divide(undivided);
        try {
            for (Video video : unSegment) {
                videoUtil.cutVideo(video);
            }
        }catch (TiansiException e){
            e.printStackTrace();
        }
    }
}
