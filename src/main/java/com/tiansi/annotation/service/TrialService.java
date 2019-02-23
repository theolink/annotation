package com.tiansi.annotation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tiansi.annotation.domain.Trial;

public interface TrialService extends IService<Trial> {
    Long insert(Trial trial);
}
