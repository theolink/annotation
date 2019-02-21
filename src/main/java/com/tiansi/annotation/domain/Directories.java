package com.tiansi.annotation.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Directories {
    private int id;
    private String name;

    public Directories(String name) {
        this.name = name;
    }
}
