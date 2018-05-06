package com.dev_guy_jay.simpletodo.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utils {

    public static String getDateTime(){
        Calendar c = Calendar.getInstance();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(c.getTime());
    }

    public static boolean isDateValid(int year, int month, int day, int hourOfDay, int minute){
        Calendar c = Calendar.getInstance();
        Date date1;
        Date date2;

        String currentYears = String.valueOf(c.get(Calendar.YEAR));
        String currentMonth =  String.valueOf(c.get(Calendar.MONTH));
        String currentDay =  String.valueOf(c.get(Calendar.DAY_OF_MONTH));

        String reminderYear = String.valueOf(year);
        String reminderMonth = String.valueOf(month);
        String reminderDay = String.valueOf(day);
        String reminderHourOfDay = String.valueOf(hourOfDay);
        String reminderMinute = String.valueOf(minute);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try{
            date1 = sdf.parse(currentYears + "-" + currentMonth + "-" + currentDay);
            date2 = sdf.parse(reminderYear + "-" + reminderMonth + "-" + reminderDay);

            if(date2.before(date1) ){
                return false;
            }

            if(date2.equals(date1) || date2.after(date1)){
                if(isDateAndTimeValid(reminderYear,reminderMonth,reminderDay,reminderHourOfDay,reminderMinute)){
                    return true;
                }
                return false;
            }
        }catch(ParseException e){
            return false;
        }

        return true;
    }

    private static boolean isDateAndTimeValid(String year, String month, String day, String hourOfDay, String minute) {
        Calendar c = Calendar.getInstance();
        Date date1;
        Date date2;

        String currentYear = String.valueOf(c.get(Calendar.YEAR));
        String currentMonth =  String.valueOf(c.get(Calendar.MONTH));
        String currentDay =  String.valueOf(c.get(Calendar.DAY_OF_MONTH));
        String currentHour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
        String currentMinute =  String.valueOf(c.get(Calendar.MINUTE));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                try{
                    date1 = sdf.parse(currentYear + "-" + currentMonth + "-" + currentDay + " " + currentHour + ":" + currentMinute + ":" + 00);
                    date2 = sdf.parse(year + "-" + month + "-" + day + " " + hourOfDay + ":" + minute +":" + 00);

                    if(date2.before(date1)){
                        return false;
                }
        }catch(ParseException e){
            return false;
        }

        return true;
    }
}