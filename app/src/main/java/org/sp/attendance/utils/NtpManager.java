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


import com.instacart.library.truetime.TrueTimeRx;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.schedulers.Schedulers;

public class NtpManager {

     public static void queryNtpServer(){
            TrueTimeRx.build()
                    .initializeRx("time.apple.com")
                    .subscribeOn(Schedulers.io())
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

}
