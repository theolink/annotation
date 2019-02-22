package com.tiansi.annotation.domain;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.tiansi.annotation.util.VideoClips;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Video {
    private int id;
    private int trialId;
    private String name;
    private String address;
    private int length;
    private int tagged;
    private int tagger;
    private String clipsInfo;

    @TableLogic
    private int isDeleted;

    public VideoClips toVideoClips() {
        return new VideoClips(this);
    }
}
