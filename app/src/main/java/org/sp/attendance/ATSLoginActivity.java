package org.sp.attendance;

import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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

import org.sp.attendance.utils.AccountsManager;
import org.sp.attendance.utils.CodeManager;

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
        if (!CodeManager.isDestroyed) {
            CodeManager.destroy();
        }
        checkGooglePlayServices();
        IntentFilter filter = new IntentFilter(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo;
        String ssid;
        wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
            ssid = wifiInfo.getSSID();
            if (ssid.equals("\"SPStudent\"") || ssid.equals("\"SPStaff\"") || ssid.equals("\"SPGuest\"")) {
              //  tryAutoSignIn();
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
    }

    private boolean checkGooglePlayServices() {
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

    public void tryAutoSignIn() {
        SharedPreferences sharedPref = ATSLoginActivity.this.getSharedPreferences("org.sp.ats.accounts", Context.MODE_PRIVATE);
        if (!sharedPref.getString("ats_userid", "").equals("") && !sharedPref.getString("ats_pwd", "").equals("")) {
            new AccountsManager(ATSLoginActivity.this).execute("SignInOnly", sharedPref.getString("ats_userid", ""), sharedPref.getString("ats_pwd", ""));

        }
    }

    public void signIn(View view) {
        if (!((EditText) findViewById(R.id.textEdit_userID)).getText().toString().equals("") && !((EditText) findViewById(R.id.textEdit_password)).getText().toString().equals("")) {
            new AccountsManager(ATSLoginActivity.this).execute("SignInOnly", ((EditText) findViewById(R.id.textEdit_userID)).getText().toString(),
                    ((EditText) findViewById(R.id.textEdit_password)).getText().toString());
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.title_sign_in_failed)
                    .setMessage(R.string.error_credentials_disappeared)
                    .setCancelable(false)
                    .setPositiveButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .create()
                    .show();
        }
    }

}
