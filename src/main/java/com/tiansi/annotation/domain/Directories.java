package com.tiansi.annotation.domain;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Directories {
    private Long id;
    private String name;

    @TableLogic
    private Integer isDeleted;

    public Directories(String name) {
        this.name = name;
    }
}
