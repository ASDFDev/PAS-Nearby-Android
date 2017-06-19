package org.sp.attendance.models;

/*
 * Copyright 2017 Daniel Quah and Justin Xin
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

import org.sp.attendance.utils.Ntp.NtpFactory;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class NtpModel {

    private Context context;
    private static Date currentTime;

    public NtpModel(Context context){
        this.context = context;
    }

    private Date ntpInit(){
        NtpFactory ntpFactory = new NtpFactory(context);
        try {
            currentTime = ntpFactory.execute().get();
        } catch (ExecutionException | InterruptedException executionExecption){
            System.out.println(executionExecption);
        }
        return currentTime;
    }

    public String getTrueCalendar(){
        ntpInit();
        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(currentTime);
    }

    public String getTrueDate(){
        ntpInit();
        Format formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(currentTime);
    }

    public String getTrueTime(){
        ntpInit();
        Format formatter = new SimpleDateFormat("HH:mm:ss");
        return formatter.format(currentTime);
    }

    public String getTrueYear(){
        ntpInit();
        Format formatter = new SimpleDateFormat("yyyy");
        return formatter.format(currentTime);
    }

    public String getTrueMonth(){
        ntpInit();
        Format formatter = new SimpleDateFormat("LLLL", Locale.getDefault());
        return formatter.format(currentTime);
    }

    public String getTrueDay(){
        ntpInit();
        Format formatter = new SimpleDateFormat("dd");
        return formatter.format(currentTime);
    }

}