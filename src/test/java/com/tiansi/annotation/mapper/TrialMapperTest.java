package com.tiansi.annotation.mapper;

import com.tiansi.annotation.domain.Trial;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TrialMapperTest {
    @Autowired
    private TrialMapper trialMapper;

    @Test
    public void testSelect() {
        List<Trial> trials = trialMapper.selectList(null);
        Assert.assertEquals(3, trials.size());
    }

    @Test
    public void saveTest() {
        Trial trial = new Trial();
        trial.setVideoNum(1);
        trial.setUploader(1L);
        trial.setName("测试");
        trialMapper.save(trial);
        System.out.println(trial.getId());
    }
}
