package com.tiansi.annotation.service;

import com.tiansi.annotation.util.DateUtil;
import com.tiansi.annotation.util.Props;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.ArrayList;

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
        String outputPath = props.getClipHome();
        double start = 2.3;
        double end = 5.6;
        int step = 1;
        videoService.interceptFrame(videoPath, outputPath, start, end, step);
    }

    @Test
    public void cutVideoTest() {
        try {
            videoService.cutVideo(1L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Test
//    public void divideTest() {
////        String originPath = "F:\\data\\annotation\\video\\origin\\龙泉-2019-2-19\\行政\\(2017)川0112行初18号第2次庭审" +
////                "\\150.120.30.19_01_20170705101006_20170705101015.mp4";
////        String originPath = "F:\\data\\annotation\\video\\origin\\龙泉-2019-2-19\\行政\\(2017)川0112行初18号第2次庭审" +
////                "\\150.120.30.19_01_20170705110127_20170705110617.mp4";
//        String originPath ="F:\\data\\annotation\\video\\origin\\龙泉驿-2019-2-21\\刑事\\(2018)川0112刑初612号第1次庭审" +
//                "\\150.120.0.10_02_20181010144249_20181010145825.mp4";
//        String outputPath = props.getVideoHome()+"\\"+ DateUtil.getDay();
//        File file =new File(outputPath);
//        if(!file.exists()){
//            file.mkdir();
//        }
//        ArrayList<String> processedPaths=videoService.divide(originPath,outputPath);
//        processedPaths.forEach(processedPath-> System.out.println(processedPath));
//    }
}
