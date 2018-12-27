package com.tiansi.annotation.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ClipsServiceTest {
    @Autowired
    private ClipsService clipsService;

    @Test
    public void deleteByVideoIdTest() {
        Assert.assertEquals(true, clipsService.deleteByVideoId(1));
    }
}
