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
    private Long id;
    private Long trialId;
    private String name;
    private Long originVideoId;
    private String address;
    private Integer length;
    private Integer tagged;
    private Long tagger;
    private String clipsInfo;

    @TableLogic
    private Integer isDeleted;

    public VideoClips toVideoClips() {
        return new VideoClips(this);
    }
}
