package com.tiansi.annotation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tiansi.annotation.domain.Clips;
import com.tiansi.annotation.mapper.ClipsMapper;
import com.tiansi.annotation.service.ClipsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClipsServiceImpl extends ServiceImpl<ClipsMapper, Clips> implements ClipsService {

    @Override
    public List<Clips> findByVideoId(int videoId) {
        return list(new QueryWrapper<Clips>().eq("video_id", videoId));
    }

    @Override
    public List<Clips> findByVideoIdUntagged(int videoId) {
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
    public boolean deleteByVideoId(int videoId) {
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
        return save(originClips);
    }

    @Override
    public boolean clear(int id) {
        Clips clips = getById(id);
        clips.setTag("");
        return updateById(clips);
    }

    @Override
    public int clearBatch(List<Integer> ids) {
        int flag = 0;
        for (int id : ids) {
            if (clear(id)) {
                flag++;
            }
        }
        return flag;
    }

}
