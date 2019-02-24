package com.tiansi.annotation.service;

import com.tiansi.annotation.exception.TiansiException;
import com.tiansi.annotation.model.Props;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class DirectoriesServiceTest {
    @Autowired
    private DirectoriesService directoriesService;
    @Autowired
    private Props props;

    @Test
    public void notScanTest() {
        ArrayList<String> directoriesNotScan = new ArrayList<>();
        try {
            directoriesNotScan = directoriesService.notScan();
        } catch (TiansiException e) {
            e.printStackTrace();
        }
        directoriesNotScan.forEach(directory -> System.out.println(directory));
        directoriesService.addDirectories(directoriesNotScan);
        File testFile = new File(props.getOriginVideoHome() + "\\testDirectory" + System.currentTimeMillis());
        if (!testFile.exists()) {
            testFile.mkdir();
        }
        System.out.println("After add:");
        try {
            directoriesNotScan = directoriesService.notScan();
        } catch (TiansiException e) {
            e.printStackTrace();
        }
        directoriesNotScan.forEach(directory -> System.out.println(directory));
        Assert.assertEquals(1, directoriesNotScan.size());
        testFile.delete();
    }
}
