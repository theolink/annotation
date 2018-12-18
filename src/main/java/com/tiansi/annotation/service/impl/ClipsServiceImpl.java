package com.tiansi.annotation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tiansi.annotation.domain.Clips;
import com.tiansi.annotation.mapper.ClipsMapper;
import com.tiansi.annotation.service.ClipsService;
import org.springframework.stereotype.Service;

@Service
public class ClipsServiceImpl extends ServiceImpl<ClipsMapper, Clips> implements ClipsService {
}
