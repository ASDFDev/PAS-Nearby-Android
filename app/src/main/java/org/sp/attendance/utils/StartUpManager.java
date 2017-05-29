package org.sp.attendance.utils;


import android.app.Activity;
import android.content.Context;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.sp.attendance.R;

public class StartUpManager{

    private Context context;


    public StartUpManager(Context context){
        this.context = context;
    }

    public void initStartUp(){
        checkPlayServices((Activity) context);
        NtpManager ntpManager = new NtpManager(context);
        ntpManager.queryNtpServer();
        checkSsid();
    }



    private void checkSsid() {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo;
        String ssid;
        wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
            ssid = wifiInfo.getSSID();
            if (!ssid.equals("\"SPStudent\"") || !ssid.equals("\"SPStaff\"") || !ssid.equals("\"SPGuest\"")) {
                showDialog(context.getResources().getString(R.string.title_warning),
                        context.getResources().getString(R.string.error_network_invalid));
            } else {
                showDialog(context.getResources().getString(R.string.title_warning),
                        context.getResources().getString(R.string.error_network_disappeared));
            }
        }
    }

    private boolean checkPlayServices(Activity activity) {
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

    private void showDialog(String title, String message){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setIcon(R.drawable.ic_warning_black_24dp)
                .setCancelable(false)
                .setPositiveButton(R.string.dismiss, (dialog, which) -> {
                })
                .create()
                .show();
    }



}
