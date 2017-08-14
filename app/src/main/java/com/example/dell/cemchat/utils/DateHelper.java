package com.example.dell.cemchat.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by DELL on 6/28/2017.
 */
public class DateHelper {
    private static DateHelper ourInstance ;
  private   static Date now;
   private static String date;
   private static   Calendar messageDay;
    public static DateHelper getInstance() {

        if(ourInstance==null){

            ourInstance= new DateHelper();
             now = new Date(System.currentTimeMillis());


        }

        return ourInstance;
    }

    private DateHelper() {

    }

    public String compareDate(Date messageTime) {
     messageDay= Calendar.getInstance();
        messageDay.setTime(messageTime);
      now.getMonth();
      //int i=  c.DAY_OF_MONTH;

        String given_date = "15-08";

        String[] tokens = given_date.split("-");

//        int given_day = Integer.parseInt(tokens[0]);
//        int given_month = Integer.parseInt(tokens[1]);
        int given_day=messageDay.get(Calendar.DATE);
        int given_month=messageDay.get(Calendar.MONTH)+1;
        int given_time=messageDay.get(Calendar.HOUR);
        //date=String.valueOf(given_day)+"and"+String.valueOf(given_month)+"and"+String.valueOf(given_time);



        Calendar c = Calendar.getInstance();

        int now_month = c.get(Calendar.MONTH) + 1;
        int now_day = c.get(Calendar.DATE);

        System.out.println(given_day + " " + given_month + " " + now_day + " " + now_month);

       if (now_month== given_month&now_day==given_day) {
        date=("Today");
       } else if (now_month== given_month&now_day==(given_day+1)) {
            date=("Yesterday");
   } else {
           date=String.valueOf(given_day)+"/"+String.valueOf(given_month);
       }
//            if (now_day > given_day) {
//                date=("Before Today!");
//            } else if (now_day < given_day) {
//                date=("After Today!");
//            } else {
//                date=("Today!");
//            }
//        }

        return date;

    }
}
