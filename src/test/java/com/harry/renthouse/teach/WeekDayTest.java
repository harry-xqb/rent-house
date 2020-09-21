package com.harry.renthouse.teach;



import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;


/**
 * @author Harry Xu
 * @date 2020/9/11 16:34
 */
public class WeekDayTest {

    public int dateWeekDiff(String startStr, String endStr){
        LocalDate startDate = LocalDate.parse(startStr);
        LocalDate endDate = LocalDate.parse(endStr);
        // 如果结束日期大于开始日期则交换
        if(startDate.isAfter(endDate)){
            LocalDate tmp = startDate;
            startDate = endDate;
            endDate = tmp;
        }
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        int weeks = (int) (days / 7);
        int startDayOfWeek = startDate.getDayOfWeek().getValue();
        int endDayOfWeek = endDate.getDayOfWeek().getValue();
        int addDay = endDayOfWeek < startDayOfWeek ? 1 : 0;
        return weeks + addDay;
    }

    @Test
    public void test (){
        System.out.println(dateWeekDiff("2020-09-11", "2020-09-11"));
        System.out.println(dateWeekDiff("2020-09-11", "2020-09-14"));
        System.out.println(dateWeekDiff("2020-09-11", "2020-09-20"));
        System.out.println(dateWeekDiff("2020-09-11", "2020-09-30"));
        System.out.println(dateWeekDiff("2020-12-01", "2021-01-01"));
        System.out.println(dateWeekDiff("2020-09-11", "2020-09-01"));
    }
}
