package com.tiansi.annotation.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static String getDay() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date);
    }
}
