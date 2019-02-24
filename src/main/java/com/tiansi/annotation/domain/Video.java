package com.tiansi.annotation.domain;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.tiansi.annotation.domain.body.VideoRequestBody;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


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
    private Date tagDate;
    private String clipsInfo;

    @TableLogic
    private Integer isDeleted;

    public VideoRequestBody requestBody() {
        return new VideoRequestBody(this);
    }
}
