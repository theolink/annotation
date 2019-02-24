package com.tiansi.annotation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tiansi.annotation.domain.Clips;
import com.tiansi.annotation.mapper.ClipsMapper;
import com.tiansi.annotation.service.ClipsService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ClipsServiceImpl extends ServiceImpl<ClipsMapper, Clips> implements ClipsService {

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
    public boolean deleteByVideoId(Long videoId) {
        return remove(new QueryWrapper<Clips>().eq("video_id", videoId));
    }

    @Override
    public boolean tag(Clips clips) {
        if (clips == null || clips.getId() <= 0) {
            return false;
        }
        Clips originClips = getById(clips.getId());
        if (originClips == null) {
            return false;
        }
        originClips.setTag(clips.getTag());
        originClips.setTagger(clips.getTagger());
        originClips.setTagged(1);
        originClips.setTagDate(new Date());
        return save(originClips);
    }

    @Override
    public boolean clear(Long id) {
        Clips clips = getById(id);
        clips.setTag("");
        return updateById(clips);
    }

    @Override
    public int clearBatch(List<Long> ids) {
        int flag = 0;
        for (Long id : ids) {
            if (clear(id)) {
                flag++;
            }
        }
        return flag;
    }

}
