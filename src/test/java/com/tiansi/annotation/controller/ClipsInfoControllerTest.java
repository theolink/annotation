package com.tiansi.annotation.controller;

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

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ClipsInfoControllerTest {
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;


    @Before
    public void step(){
        this.mockMvc= MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    public void saveTest() throws Exception{
        int videoId=33055;
        Map<Integer,Integer> clipsInfos = new HashMap<>();
        clipsInfos.put(234,575);
        clipsInfos.put(90000,21312);
        MvcResult result=mockMvc.perform(post("/clipsInfo//save/{videoId}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();
        System.out.println(result.getResponse().getContentAsString());
    }
}
