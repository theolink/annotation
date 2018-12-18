package com.tiansi.annotation.service;

import com.tiansi.annotation.domain.Video;
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

	@Test
	public void interceptFrameTest(){
		String videoPath="C:\\Users\\Theo\\Desktop\\1.mp4";
		String outputPath="C:\\Users\\Theo\\Desktop\\imgTest";
		int start=2;
		int end=5;
		int step=1;
		videoService.interceptFrame(videoPath,outputPath,start,end,step);
	}

	@Test
	public void cutVideoTest(){
		String videoPath="C:\\Users\\Theo\\Desktop\\1.mp4";
		Video video=new Video();
		video.setId(1);
		video.setAddress(videoPath);
		videoService.cutVideo(video);
	}
}
