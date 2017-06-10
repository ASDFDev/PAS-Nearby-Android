package org.sp.attendance.utils;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.sp.attendance.R;

public class StartUpManager extends AsyncTask<String,Void,String>{

    private Context context;
    private ProgressDialog progressDialog;

    public StartUpManager(Context context){
        this.context = context;
    }

    @Override
    protected void onPreExecute(){
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getResources().getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        Wave mWaveDrawable = new Wave();
        mWaveDrawable.setBounds(0, 0, 100, 100);
        mWaveDrawable.setColor(ContextCompat.getColor(context,R.color.colorAccent));
        progressDialog.setIndeterminateDrawable(mWaveDrawable);
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... params){
        checkPlayServices((Activity)context);
        checkNetwork();
        return "Init success!";
    }


    @Override
    protected void onPostExecute(String result){
        progressDialog.dismiss();
    }

    private void checkNetwork() {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo;
        String ssid;
        wifiInfo = wifiManager.getConnectionInfo();
                if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
                    if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
                        ssid = wifiInfo.getSSID();
                        if (ssid.equals("\"SPStudent\"") || ssid.equals("\"SPStaff\"") || ssid.equals("\"SPGuest\"")) {
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

    private void showWifiDialog(String title, String message){
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
