package com.tiansi.annotation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tiansi.annotation.domain.DivideType;
import com.tiansi.annotation.domain.Users;
import com.tiansi.annotation.exception.ErrorCode;
import com.tiansi.annotation.exception.TiansiException;
import com.tiansi.annotation.mapper.DivideTypeMapper;
import com.tiansi.annotation.service.DivideTypeService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DivideTypeServiceImpl extends ServiceImpl<DivideTypeMapper, DivideType> implements DivideTypeService {
    @Override
    public boolean addType(DivideType divideType, Users users) {
        divideType.setCreator(users.getId());
        return save(divideType);
    }

    @Override
    public List<Long> getIds(List<DivideType> divideTypes) {
        List<Long> ids = new ArrayList<>();
        divideTypes.forEach(divideType -> ids.add(divideType.getId()));
        return ids;
    }

    @Override
    public boolean delete(List<Long> ids) {
        return removeByIds(ids);
    }

    @Override
    public boolean update(DivideType divideType, Users users) throws TiansiException {
        if (divideType == null || divideType.getId() == null) {
            throw new TiansiException(ErrorCode.INVALID_PARAMETER, "DivideType and id can not be null !");
        }
        DivideType oldVersion = getById(divideType.getId());
        if (oldVersion == null) {
            throw new TiansiException(ErrorCode.ENTITY_NOT_EXIST, "DivideType with id " + divideType.getId() + "isn't exist !");
        }
        if (!oldVersion.getCreator().equals(users.getId())) {
            throw new TiansiException(ErrorCode.LIMITED_AUTHORITY, "Limited authority !");
        }
        if (!divideType.getName().isEmpty()) {
            oldVersion.setName(divideType.getName());
        }
        oldVersion.setVideoRanges(divideType.getVideoRanges());
        return updateById(oldVersion);
    }

    @Override
    public List find() {
        return list(null);
    }
}
