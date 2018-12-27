package com.tiansi.annotation.service;

import com.tiansi.annotation.util.Props;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class VideoServiceTest {
    @Autowired
    private VideoService videoService;
    @Autowired
    private Props props;

    @Test
    public void interceptFrameTest() {
        String videoPath = "F:\\data\\imgTest\\1.mp4";
        String outputPath = props.getVideoOutputDir();
        double start = 2.3;
        double end = 5.6;
        int step = 1;
        videoService.interceptFrame(videoPath, outputPath, start, end, step);
    }

    @Test
    public void cutVideoTest() {
        try {
            videoService.cutVideo(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
