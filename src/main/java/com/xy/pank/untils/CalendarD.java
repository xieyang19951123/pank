package com.xy.pank.untils;

import java.util.Calendar;
import java.util.Date;

public class CalendarD {
    public static int getsdays(Calendar a, Calendar b) {
        if(b.after(a)) {
            Calendar temp;
            temp=a;
            a=b;
            b=temp;
        }
        int days=a.get(Calendar.DAY_OF_YEAR)-b.get(Calendar.DAY_OF_YEAR);
        if(a.get(Calendar.YEAR)!=b.get(Calendar.YEAR)) {
            do {
                days+=a.getActualMaximum(Calendar.DAY_OF_YEAR);
                a.add(Calendar.YEAR, 1);
            }
            while(a.get(Calendar.YEAR)!=b.get(Calendar.YEAR));
        }
        return days;
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Calendar c=Calendar.getInstance();
        Calendar d=Calendar.getInstance();
        c.set(2020,10,15,12,23);
        d.set(2020,10,15,13,24);
       // System.out.println(getsdays(c,d));
        System.out.println(getDatePoor(d.getTime(), c.getTime()));
    }
    public static String getDatePoor(Date endDate, Date nowDate) {

        long nd = 1000 * 24 * 60 * 60;

        long nh = 1000 * 60 * 60;

        long nm = 1000 * 60;

         long ns = 1000;

        // 获得两个时间的毫秒bai时间差异

        long diff = endDate.getTime() - nowDate.getTime();

        // 计算   差多du少天

        long day = diff / nd;

        // 计算差多少小时

        long hour = diff % nd / nh;

        // 计算差多少分钟

        long min = diff % nd % nh / nm;

        // 计算差多少秒//输出结果

        // long sec = diff % nd % nh % nm / ns;

        return day + "天" + hour + "小时" + min + "分钟";

    }
}
