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
    public boolean tag(Clips clips, Users processor) throws TiansiException {
        if (clips == null || clips.getId() == null) {
            throw new TiansiException(ErrorCode.INVALID_PARAMETER, "Clips and clipsId can not be null !");
        }
        if (!clips.getTagger().equals(processor.getId())) {
            throw new TiansiException(ErrorCode.LIMITED_AUTHORITY, "No authority to tag other user's clips !");
        }
        Clips originClips = getById(clips.getId());
        if (originClips == null) {
            throw new TiansiException(ErrorCode.ENTITY_NOT_EXIST, "Clips which has id " + clips.getId() + " is not exist !");
        }
        originClips.setTag(clips.getTag());
        originClips.setTagged(2);
        originClips.setTagDate(new Date());
        return updateById(originClips);
    }

    @Override
    public boolean clear(Long id, Users users) throws TiansiException {
        if (id == null) {
            throw new TiansiException(ErrorCode.INVALID_PARAMETER, "ClipsId can not be null !");
        }
        Clips clips = getById(id);
        if (clips == null) {
            throw new TiansiException(ErrorCode.ENTITY_NOT_EXIST, "Clips which has id " + id + " is not exist !");
        }
        if (!clips.getTagger().equals(users.getId())) {
            throw new TiansiException(ErrorCode.LIMITED_AUTHORITY, "No authority to clear other user's clips !");
        }
        clips.setTag("");
        clips.setTagged(1);
        return updateById(clips);
    }

    @Override
    public int clearBatch(List<Long> ids, Users users) throws TiansiException {
        int flag = 0;
        for (Long id : ids) {
            if (clear(id, users)) {
                flag++;
            }
        }
        return flag;
    }

    @Override
    public int assign(Integer amount, Users users) {
        Page<Clips> page = new Page<>(1, amount);
        page = (Page<Clips>) page(page, new QueryWrapper<Clips>().eq("tagged", 0));
        List<Clips> clipsList = page.getRecords();
        clipsList.forEach(clips -> {
            clips.setTagged(1);
            clips.setTagger(users.getId());
        });
        if (clipsList.isEmpty()) {
            return 0;
        }
        if (updateBatchById(clipsList)) {
            return clipsList.size();
        }
        return 0;
    }
}
