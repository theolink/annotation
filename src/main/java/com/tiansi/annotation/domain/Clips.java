package com.tiansi.annotation.domain;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.tiansi.annotation.domain.body.ClipsRequestBody;
import lombok.Data;

import java.util.Date;

@Data
public class Clips {
    private Long id;
    private String name;
    private Long videoId;
    private Integer frameNum;
    private String address;
    private String xmlAddress;
    private String tag;
    private Integer tagged;
    private Long tagger;
    private Date tagDate;

    @TableLogic
    private Integer isDeleted;

    public ClipsRequestBody requestBody() {
        return new ClipsRequestBody(this);
    }
}
