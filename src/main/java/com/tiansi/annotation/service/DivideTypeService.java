package com.tiansi.annotation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tiansi.annotation.domain.DivideType;
import com.tiansi.annotation.domain.Users;
import com.tiansi.annotation.exception.TiansiException;

import java.util.List;

public interface DivideTypeService extends IService<DivideType> {
    boolean addType(DivideType divideType, Users users);

    List<Long> getIds(List<DivideType> divideTypes);

    boolean delete(List<Long> ids);

    boolean update(DivideType divideType, Users users) throws TiansiException;

    List find();
}
