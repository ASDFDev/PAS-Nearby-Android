package org.sp.attendance.models;


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
