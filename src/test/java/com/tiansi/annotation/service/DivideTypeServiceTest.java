//package com.tiansi.annotation.service;
//
//import com.alibaba.fastjson.JSONObject;
//import com.tiansi.annotation.domain.DivideType;
//import com.tiansi.annotation.domain.Users;
//import com.tiansi.annotation.model.Props;
//import com.tiansi.annotation.model.VideoRange;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class DivideTypeServiceTest {
//    @Autowired
//    private Props props;
//    @Autowired
//    private DivideTypeService divideTypeService;
//    @Autowired
//    private UserService userService;
//
//    @Test
//    public void addTest() {
////        List<VideoRange> videoRanges1 = props.getVideoRanges1();
////        List<VideoRange> videoRanges2 = props.getVideoRanges2();
//        if (divideTypeService.list(null).size() >= 3) {
//            return;
//        }
//        List<VideoRange> videoRanges1 = new ArrayList<>();
//        List<VideoRange> videoRanges2 = new ArrayList<>();
//        //四分
//        VideoRange videoRange11 = new VideoRange(0, 0.5, 0, 0.5);
//        VideoRange videoRange12 = new VideoRange(0.5, 1, 0, 0.5);
//        VideoRange videoRange13 = new VideoRange(0, 0.5, 0.5, 1);
//        VideoRange videoRange14 = new VideoRange(0.5, 1, 0.5, 1);
//        videoRanges1.add(videoRange11);
//        videoRanges1.add(videoRange12);
//        videoRanges1.add(videoRange13);
//        videoRanges1.add(videoRange14);
//        //六分
//        VideoRange videoRange21 = new VideoRange(0, 0.6666666666666666, 0, 0.6666666666666666);
//        VideoRange videoRange22 = new VideoRange(0.6666666666666666, 1, 0, 0.3333333333333333);
//        VideoRange videoRange23 = new VideoRange(0.6666666666666666, 1, 0.3333333333333333, 0.6666666666666666);
//        VideoRange videoRange24 = new VideoRange(0, 0.3333333333333333, 0.6666666666666666, 1);
//        VideoRange videoRange25 = new VideoRange(0.3333333333333333, 0.6666666666666666, 0.6666666666666666, 1);
//        VideoRange videoRange26 = new VideoRange(0.6666666666666666, 1, 0.6666666666666666, 1);
//        videoRanges2.add(videoRange21);
//        videoRanges2.add(videoRange22);
//        videoRanges2.add(videoRange23);
//        videoRanges2.add(videoRange24);
//        videoRanges2.add(videoRange25);
//        videoRanges2.add(videoRange26);
//
//        Users users = userService.getById(2);
//        DivideType divide1 = new DivideType();
//        divide1.setName("一分");
//        DivideType divide4 = new DivideType();
//        divide4.setName("四分");
//        divide4.setVideoRanges(JSONObject.toJSONString(videoRanges1));
//        DivideType divide6 = new DivideType();
//        divide6.setName("六分");
//        divide6.setVideoRanges(JSONObject.toJSONString(videoRanges2));
//        divideTypeService.addType(divide1, users);
//        divideTypeService.addType(divide4, users);
//        divideTypeService.addType(divide6, users);
//
//    }
//}
