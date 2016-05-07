package org.sp.attendance;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by HexGate on 7/5/16.
 */
public class ATSLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atslogin);
    }

    public void signIn(View view) {
        //TO-DO: Check credentials
        //Intent codeBroadcastIntent = new Intent(this, CodeBroadcastActivity.class);
        //startActivity(codeBroadcastIntent);
        Intent codeReceiveIntent = new Intent(this, CodeReceiveActivity.class);
        startActivity(codeReceiveIntent);
    }

}
