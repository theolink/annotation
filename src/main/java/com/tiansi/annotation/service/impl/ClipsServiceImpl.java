package com.tiansi.annotation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tiansi.annotation.domain.Clips;
import com.tiansi.annotation.domain.Users;
import com.tiansi.annotation.domain.body.ClipsRequestBody;
import com.tiansi.annotation.exception.ErrorCode;
import com.tiansi.annotation.exception.TiansiException;
import com.tiansi.annotation.mapper.ClipsMapper;
import com.tiansi.annotation.service.ClipsService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Service
public class ClipsServiceImpl extends ServiceImpl<ClipsMapper, Clips> implements ClipsService {

    @Override
    public boolean delete(List<Long> ids) {
        return removeByIds(ids);
    }

    @Override
    public boolean deleteByVideoId(Long videoId) {
        return remove(new QueryWrapper<Clips>().eq("video_id", videoId));
    }

    @Override
    public Page find(Long id, Long videoId, String name, Integer tagged, Long tagger, Date tagDateStart, Date tagDateEnd,
                     Integer currentPage, Integer pageSize) {
        QueryWrapper<Clips> queryWrapper = new QueryWrapper<>();
        if (id != null) {
            queryWrapper = queryWrapper.eq("id", id);
        }
        if (videoId != null) {
            queryWrapper = queryWrapper.eq("video_id", videoId);
        }
        if (!StringUtils.isEmpty(name)) {
            queryWrapper = queryWrapper.like("name", name);
        }
        if (tagged != null) {
            queryWrapper = queryWrapper.eq("tagged", tagged);
        }
        if (tagger != null) {
            queryWrapper = queryWrapper.eq("tagger", tagger);
        }
        if (tagDateStart != null) {
            queryWrapper = queryWrapper.ge("tag_date", tagDateStart);
        }
        if (tagDateEnd != null) {
            queryWrapper = queryWrapper.le("tag_date", tagDateEnd);
        }
        currentPage = currentPage != null && currentPage > 0 ? currentPage : 1;
        pageSize = pageSize != null && pageSize > 0 ? pageSize : 10;
        Page<Clips> page = new Page<>(currentPage, pageSize);
        page = (Page<Clips>) page(page, queryWrapper);
        Page<ClipsRequestBody> clipsRequestBodyPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        clipsRequestBodyPage.setPages(page.getPages()).setRecords(ClipsRequestBody.fromClips(page.getRecords()));
        return clipsRequestBodyPage;
    }

    @Override
    public List<Clips> findByVideoId(Long videoId) {
        return list(new QueryWrapper<Clips>().eq("video_id", videoId));
    }

    @Override
    public List<Clips> findByVideoIdUntagged(Long videoId) {
        return list(new QueryWrapper<Clips>().eq("video_id", videoId).eq("tagged", 0));
    }

    @Override
    public List<Clips> findAll() {
        return list(null);
    }

    @Override
    public List<Clips> findAllUntagged() {
        return list(new QueryWrapper<Clips>().eq("tagged", 0));
    }


    @Override
    public boolean tag(Clips clips, Users processor) throws TiansiException {
        if (clips == null || clips.getId() == null) {
            throw new TiansiException(ErrorCode.INVALID_PARAMETER, "Clips and clipsId can not be null !");
        }
        Clips originClips = getById(clips.getId());
        if (originClips == null) {
            throw new TiansiException(ErrorCode.ENTITY_NOT_EXIST, "Clips which has id " + clips.getId() + " is not exist !");
        }
        originClips.setTag(clips.getTag());
        originClips.setTagger(processor.getId());
        originClips.setTagged(2);
        originClips.setTagDate(new Date());
        return updateById(originClips);
    }

    @Override
    public boolean clear(Long id) throws TiansiException {
        if (id == null) {
            throw new TiansiException(ErrorCode.INVALID_PARAMETER, "ClipsId can not be null !");
        }
        Clips clips = getById(id);
        if (clips == null) {
            throw new TiansiException(ErrorCode.ENTITY_NOT_EXIST, "Clips which has id " + id + " is not exist !");
        }
        clips.setTag("");
        return updateById(clips);
    }

    @Override
    public int clearBatch(List<Long> ids) throws TiansiException {
        int flag = 0;
        for (Long id : ids) {
            if (clear(id)) {
                flag++;
            }
        }
        return flag;
    }

}
