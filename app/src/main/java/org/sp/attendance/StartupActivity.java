package org.sp.attendance;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by HexGate on 7/5/16.
 */
public class StartupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        //TO-DO: Check saved credentials
        Intent loginIntent = new Intent(this, ATSLoginActivity.class);
        startActivity(loginIntent);
    }

}