//package com.tiansi.annotation.service;
//
//import com.tiansi.annotation.exception.TiansiException;
//import com.tiansi.annotation.model.Props;
//import org.junit.Assert;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//@Transactional
//public class DirectoriesServiceTest {
//    @Autowired
//    private DirectoriesService directoriesService;
//    @Autowired
//    private Props props;
//
//    @Test
//    public void notScanTest() {
//        ArrayList<String> directoriesNotScan = new ArrayList<>();
//        try {
//            directoriesNotScan = directoriesService.notScan(props.getOriginVideoHome());
//        } catch (TiansiException e) {
//            e.printStackTrace();
//        }
//        directoriesNotScan.forEach(directory -> System.out.println(directory));
//        directoriesService.addDirectories(directoriesNotScan);
//        File testFile = new File(props.getOriginVideoHome() + "\\testDirectory" + System.currentTimeMillis());
//        if (!testFile.exists()) {
//            testFile.mkdir();
//        }
//        System.out.println("After add:");
//        try {
//            directoriesNotScan = directoriesService.notScan(props.getOriginVideoHome());
//        } catch (TiansiException e) {
//            e.printStackTrace();
//        }
//        directoriesNotScan.forEach(directory -> System.out.println(directory));
//        Assert.assertEquals(1, directoriesNotScan.size());
//        testFile.delete();
//    }
//    @Test
//    public void removeTest(){
//        List<String> list1=new ArrayList<>();
//        list1.add("111");
//        list1.add("222");
//        list1.add("333");
//        List<String> list2=new ArrayList<>();
//        list2.add("444");
//        list2.add("222");
//        list2.add("333");
//        list1.removeAll(list2);
//        System.out.println(list1);
//
//    }
//}
