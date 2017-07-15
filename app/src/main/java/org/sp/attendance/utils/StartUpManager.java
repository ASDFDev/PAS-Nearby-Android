package org.sp.attendance.utils;

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

import android.app.Activity;
import android.content.Context;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.sp.attendance.R;

import java.util.Arrays;

public class StartUpManager{

    private final Context context;

    public StartUpManager(Context context){
        this.context = context;
    }
    private final String[] spNetwork = {"SPStudent", "SPStaff", "SPGuest"};

    public void checkNetwork() {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo;
        String ssid;
        wifiInfo = wifiManager.getConnectionInfo();
                if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
                    if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
                        ssid = wifiInfo.getSSID();
                        if (Arrays.asList(spNetwork).contains(ssid)) {
                            // Valid network, should implement real account auth soon....
                        } else {
                            showWifiDialog(context.getResources().getString(R.string.title_warning),
                                    context.getResources().getString(R.string.error_network_invalid));
                        }
                    } else {
                        showWifiDialog(context.getResources().getString(R.string.title_warning),
                                context.getResources().getString(R.string.error_network_disappeared));
                    }
                }
    }

    public boolean checkPlayServices(Activity activity) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activity, resultCode, 9000)
                        .show();
            } else {
                ((Activity)context).finish();
            }
            return false;
        }
        return true;
    }

    private void showWifiDialog(String title, String message){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setIcon(R.drawable.ic_wifi_24dp)
                .setCancelable(false)
                .setPositiveButton(R.string.dismiss, (dialog, which) -> {
                })
                .create()
                .show();
    }

}

