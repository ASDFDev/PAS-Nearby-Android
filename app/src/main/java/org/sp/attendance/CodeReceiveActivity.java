package org.sp.attendance;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import org.sp.attendance.utils.CodeManager;

/**
 * Created by Daniel Quah on 21/5/2016
 */
public class CodeReceiveActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);
        // TODO: Implement student ID for sending/receiving
        CodeManager.setupStudentEnvironment(this, "0000000");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onPause() {
        super.onPause();
        CodeManager.destroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        CodeManager.destroy();
    }

    public void stopReceive(View view) {
        CodeManager.destroy();
        finish();
    }

}