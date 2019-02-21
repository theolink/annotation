package com.tiansi.annotation.util;

import lombok.Data;

@Data
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
