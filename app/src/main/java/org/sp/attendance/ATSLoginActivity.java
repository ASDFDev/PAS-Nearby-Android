package org.sp.attendance;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.sp.attendance.utils.ConnectionManager;

import java.net.CookieHandler;
import java.net.CookieManager;

/**
 * Created by HexGate on 7/5/16.
 */
public class ATSLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atslogin);
        CookieHandler.setDefault(new CookieManager());
        IntentFilter filter = new IntentFilter(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo;
        String ssid;
        wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
            ssid = wifiInfo.getSSID();
            if (ssid.equals("\"SPStudent\"") || ssid.equals("\"SPStaff\"") || ssid.equals("\"SPGuest\"")) {
            
            } else {
                new AlertDialog.Builder(ATSLoginActivity.this)
                        .setTitle(R.string.title_sp_wifi)
                        .setMessage(R.string.error_network_invalid)
                        .setCancelable(false)
                        .setPositiveButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .create()
                        .show();
            }
        } else {
            new AlertDialog.Builder(ATSLoginActivity.this)
                    .setTitle(R.string.title_sp_wifi)
                    .setMessage(R.string.error_network_disappeared)
                    .setCancelable(false)
                    .setPositiveButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .create()
                    .show();
        }
    }


    public void signIn(View view) {
        //TO-DO: Check credentials
        //Intent codeBroadcastIntent = new Intent(this, CodeBroadcastActivity.class);
        //startActivity(codeBroadcastIntent);
        Intent codeReceiveIntent = new Intent(this, CodeReceiveActivity.class);
        startActivity(codeReceiveIntent);
    }

}
