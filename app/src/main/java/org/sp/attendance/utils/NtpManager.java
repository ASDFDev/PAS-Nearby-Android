package org.sp.attendance.utils;

/*
 * Copyright 2016-2017 Daniel Quah and Justin Xin
 *
 * This file is part of org.sp.attendance
 *
 * ATS_Nearby is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ATS_Nearby is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */


import android.content.Context;

import com.instacart.library.truetime.TrueTimeRx;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class NtpManager {

    private Context context;

    NtpManager(Context context){
        this.context = context;
    }

    void queryNtpServer(){
        TrueTimeRx.build()
                .withRetryCount(100)
                .withSharedPreferences(context)
                .withLoggingEnabled(false)
                .initializeRx("time.apple.com")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(date ->
                                System.out.println("Ntp initialization successful ( " + date  + " )"),
                        Throwable ->
                                System.out.print("Ntp initialization failed!"));
    }

    static String getNtp(){
        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date trueTime = TrueTimeRx.now();
        return formatter.format(trueTime);
    }

    public static String getDate(){
        Format formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date trueTime = TrueTimeRx.now();
        return formatter.format(trueTime);
    }

    static String getTime(){
        Format formatter = new SimpleDateFormat("HH:mm:ss");
        Date trueTime = TrueTimeRx.now();
        return formatter.format(trueTime);
    }

    static String getYear(){
        Format formatter = new SimpleDateFormat("yyyy");
        Date trueTime = TrueTimeRx.now();
        return formatter.format(trueTime);
    }

    static String getMonth(){
        Format formatter = new SimpleDateFormat("LLLL", Locale.getDefault());
        Date trueTime = TrueTimeRx.now();
        return formatter.format(trueTime);
    }

    static String getDay(){
        Format formatter = new SimpleDateFormat("dd");
        Date trueTime = TrueTimeRx.now();
        return formatter.format(trueTime);
    }

}
