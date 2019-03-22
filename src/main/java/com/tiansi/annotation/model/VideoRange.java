package com.tiansi.annotation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoRange {
    private double x1;
    private double x2;
    private double y1;
    private double y2;

    public double getXRange() {
        return x2 - x1;
    }

    public double getYRange() {
        return y2 - y1;
    }
}
