package com.sfu.cmpt276groupproject.Model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.sfu.cmpt276groupproject.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.DateFormatSymbols;
import java.util.TimeZone;

/**
 * This class is used for everything related to calculating dates and date Strings
 */
public class DateCalculator {

    public static int getDayInterval(Inspection inspection)throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");

        newFormat.setTimeZone(TimeZone.getTimeZone("US/Pacific"));
        String today = newFormat.format(new Date());

        Log.d("TAG", inspection.getDate());
        StringBuilder temp = new StringBuilder(inspection.getDate());
        temp.insert(4, "-");
        temp.insert(7, "-");
        String target = temp.toString();

        Date from = newFormat.parse(target);
        Date to = newFormat.parse(today);
        assert to != null;
        assert from != null;
        return (int) ((to.getTime() - from.getTime()) / (1000 * 60 * 60 * 24));
    }

    public static String changeDateToString(Context context, Inspection inspection) throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");

        newFormat.setTimeZone(TimeZone.getTimeZone("US/Pacific"));
        String today = newFormat.format(new Date());

        Log.d("TAG", inspection.getDate());
        StringBuilder temp = new StringBuilder(inspection.getDate());
        temp.insert(4, "-");
        temp.insert(7, "-");
        String target = temp.toString();

        Date from = newFormat.parse(target);
        Date to = newFormat.parse(today);
        assert to != null;
        assert from != null;
        int days = (int) ((to.getTime() - from.getTime()) / (1000 * 60 * 60 * 24));
        String res;
        if (days <= 30) {
            res = days + context.getString(R.string.days_ago);
        } else if (days < 365) {
            res = getMonth(from.getMonth());
            res = res + " " + from.getDate();
        } else {
            res = String.valueOf(from.getYear() + 1900);
            Log.d("TAG", "res = " + res);
            res = getMonth(from.getMonth()) + " " + res;
            Log.d("TAG", "res = " + res);
        }
        return res;
    }

    private static String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month];
    }

    public static String getFullDate(String date) throws ParseException {
        StringBuilder temp = new StringBuilder(date);
        temp.insert(4, "-");
        temp.insert(7, "-");
        @SuppressLint("SimpleDateFormat") Date fullDate = new SimpleDateFormat("yyyy-MM-dd").parse(temp.toString());
        assert fullDate != null;
        String year = String.valueOf(fullDate.getYear() + 1900);
        String month = new DateFormatSymbols().getMonths()[fullDate.getMonth()];
        String day = String.valueOf(fullDate.getDate());
        String res = month + " " + day + ", " + year;
        return res;
    }
}
