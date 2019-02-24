package com.tiansi.annotation.service;

import com.tiansi.annotation.domain.OriginVideo;
import com.tiansi.annotation.domain.Users;
import com.tiansi.annotation.model.Props;
import com.tiansi.annotation.util.OriginVideoUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OriginVideoServiceTest {
    @Autowired
    private Props props;
    @Autowired
    private OriginVideoService originVideoService;
    @Autowired
    private OriginVideoUtil originVideoUtil;

    @Test
    public void pathTest() {
        String path1 = "F:\\data\\annotation";
        File file1 = new File(path1);
        System.out.println(file1.getAbsolutePath());
        String path2 = "F:/data/imgTest";
        File file2 = new File(path2);
        System.out.println(file2.getAbsolutePath());
    }

    @Test
    public void scanTest() {
        Users users = new Users();
        users.setId(1L);
        System.out.println(originVideoService.scan(users));
    }

    @Test
    public void startDivideTest() {
        Users users = new Users();
        users.setId(1L);
        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        originVideoService.startDivide(ids, users);
    }

    @Test
    public void divideTest() {
        List<OriginVideo> originVideos = new ArrayList<>();
        OriginVideo originVideo = new OriginVideo();
        originVideo.setId(1L);
        originVideos.add(originVideo);
        Users users = new Users();
        users.setId(1L);
        originVideoUtil.divide(originVideos);
    }
}
