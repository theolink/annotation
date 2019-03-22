package com.tiansi.annotation.domain;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DivideType {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    private String name;
    private String videoRanges;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long creator;
    private Date createDate;

    @TableLogic
    private Integer isDeleted;


}
