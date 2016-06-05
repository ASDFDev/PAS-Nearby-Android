package org.sp.attendance;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

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
               //TODO: Auto sign in
            } else {
                new AlertDialog.Builder(ATSLoginActivity.this)
                        .setTitle(R.string.title_warning)
                        .setMessage(R.string.error_network_invalid)
                        .setCancelable(false)
                        .setPositiveButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .create()
                        .show();
            }
        } else {
            new AlertDialog.Builder(ATSLoginActivity.this)
                    .setTitle(R.string.title_warning)
                    .setMessage(R.string.error_network_disappeared)
                    .setCancelable(false)
                    .setPositiveButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .create()
                    .show();
        }
        boolean firstRun = getSharedPreferences("apppref", MODE_PRIVATE).getBoolean("firstrun", true);
        if (firstRun) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.disclaimer)
                    .setMessage(R.string.first_run_message)
                    .setCancelable(false)
                    .setPositiveButton(R.string.got_it, null)
                    .create()
                    .show();
        }
        getSharedPreferences("apppref", MODE_PRIVATE)
                .edit()
                .putBoolean("firstrun", false)
                .commit();
        checkGooglePlayServices();
    }

    private boolean checkGooglePlayServices(){
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, 9000)
                        .show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }



    public void signIn(View view) {
        if (((EditText)findViewById(R.id.textEdit_userID)).getText().toString().toLowerCase().startsWith("p")) {
            //Found student user ID
            //TODO: Login to student ATS
            Intent codeReceiveIntent = new Intent(this, CodeReceiveActivity.class);
            startActivity(codeReceiveIntent);
        } else if (((EditText)findViewById(R.id.textEdit_userID)).getText().toString().toLowerCase().startsWith("s")) {
            //Found staff user ID
            //TODO: Login to staff ATS
            Intent sendCodeIntent = new Intent(this, SendCodeActivity.class);
            startActivity(sendCodeIntent);
        } else {
            //Incorrect user ID format
            new AlertDialog.Builder(ATSLoginActivity.this)
                    .setTitle(R.string.title_error_login)
                    .setMessage(R.string.error_credentials_invalid)
                    .setCancelable(false)
                    .setPositiveButton(R.string.dismiss ,new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .create()
                    .show();
        }
    }

}
