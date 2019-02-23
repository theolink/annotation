package com.tiansi.annotation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tiansi.annotation.domain.Trial;
import com.tiansi.annotation.mapper.TrialMapper;
import com.tiansi.annotation.service.TrialService;
import org.springframework.stereotype.Service;

@Service
public class TrialServiceImpl extends ServiceImpl<TrialMapper, Trial> implements TrialService {
    @Override
    public Long insert(Trial trial) {
        baseMapper.save(trial);
        return trial.getId();
    }
}
