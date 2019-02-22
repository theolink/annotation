package com.tiansi.annotation.domain;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.util.Date;

@Data
public class OriginVideo {
    private int id;
    private int trialId;
    private String name;
    private String address;

    private int uploader;
    private Date uploadDate;
    //视频预处理状态,0：未处理，1：处理中，2：已处理
    private int preDeal;
    private int preDealer;
    private Date preDealDate;

    @TableLogic
    private int isDeleted;
}
