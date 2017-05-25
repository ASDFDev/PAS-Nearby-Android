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


import android.app.ProgressDialog;
import android.content.Context;

import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.instacart.library.truetime.TrueTimeRx;

import org.sp.attendance.R;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.schedulers.Schedulers;

public class NtpManager {

    private static Context context;
    private static ProgressDialog progressDialog;

    private static void showProgressDialog() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        ThreeBounce threeBounce = new ThreeBounce();
        progressDialog.setIndeterminateDrawable(threeBounce);
        progressDialog.setMessage(context.getResources().getString(R.string.ntp_init_message));
        progressDialog.show();
    }

    public NtpManager(Context context){
        this.context = context;
    }

     public static void queryNtpServer(){
         showProgressDialog();
            TrueTimeRx.build()
                    .withRetryCount(100)
                    .withSharedPreferences(context)
                    .withLoggingEnabled(false)
                    .initializeRx("time.apple.com")
                    .subscribeOn(Schedulers.io())
                    .subscribe(date ->
                                    System.out.println("Ntp initialization successful ( " + date  + " )"),
                            Throwable ->
                                    System.out.print("Ntp initialization failed!"));
        progressDialog.dismiss();
    }

    public static String getNtp(){
        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date trueTime = TrueTimeRx.now();
        return formatter.format(trueTime);
    }

}
