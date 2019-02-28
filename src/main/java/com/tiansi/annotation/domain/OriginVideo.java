package com.tiansi.annotation.domain;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class OriginVideo {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long trialId;
    private String name;
    private String address;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long uploader;
    private Date uploadDate;
    //视频预处理状态,0：未处理，1：处理中，2：已处理
    private Integer preDeal;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long preDealer;
    private Date preDealDate;

    @TableLogic
    private Integer isDeleted;
}
