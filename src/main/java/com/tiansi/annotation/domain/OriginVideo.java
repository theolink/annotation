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
    //视频预处理状态,0：未分配，1：已分配，未设置类型，2：已设置类型，未分割，3：分割中，4：已分割
    private Integer preDeal;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long preDealer;
    private Date preDealDate;
    private Long divideType;
    private String imgPath;

    @TableLogic
    private Integer isDeleted;
}
