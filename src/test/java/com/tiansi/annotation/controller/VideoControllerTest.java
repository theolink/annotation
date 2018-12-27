package com.tiansi.annotation.controller;

import com.alibaba.fastjson.JSON;
import com.tiansi.annotation.domain.Video;
import com.tiansi.annotation.util.NumPair;
import com.tiansi.annotation.util.VideoClips;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class VideoControllerTest {
	private MockMvc mockMvc;
	@Autowired
	private WebApplicationContext webApplicationContext;


	@Before
	public void step(){
		this.mockMvc= MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
	}

	@Test
	public void getAllTest() throws Exception{
		MvcResult result=mockMvc.perform(get("/video/getAll"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}

	@Test
	public void getTest() throws Exception{
		int videoId=1;
		MvcResult result =mockMvc.perform(get("/video/"+videoId))
				.andExpect(status().isOk())
				.andExpect(content().contentType((MediaType.APPLICATION_JSON_UTF8)))
				.andReturn();
		System.out.println(result.getResponse().getContentAsString());
	}

	@Test
	public void segmentTest() throws Exception{
		VideoClips videoClips =new VideoClips();
		videoClips.setId(1);
		videoClips.setTagger(2);

		List<NumPair> numPairs=new ArrayList<>();
		numPairs.add(new NumPair(1L,9L));
		numPairs.add(new NumPair(11L,99L));
		numPairs.add(new NumPair(100L,110L));
		numPairs.add(new NumPair(111L,222L));
		numPairs.add(new NumPair(333L,444L));

		videoClips.setClipsInfo(numPairs);

		String requestContent = JSON.toJSONString(videoClips);
		mockMvc.perform(post("/video/segment")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestContent)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("SaveSuccessful").value(true));

		MvcResult result =mockMvc.perform(get("/video/1"))
				.andExpect(status().isOk())
				.andExpect(content().contentType((MediaType.APPLICATION_JSON_UTF8)))
				.andReturn();
		System.out.println(result.getResponse().getContentAsString());

	}
}
