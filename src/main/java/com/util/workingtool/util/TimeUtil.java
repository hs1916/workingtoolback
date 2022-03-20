package com.util.workingtool.util;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class TimeUtil {

    final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    public static Long checkTime(Long time){
        Date date = new Date(time);
        log.info(" Check this time : {} ", sdf.format(date));
        return time;
    }
}
