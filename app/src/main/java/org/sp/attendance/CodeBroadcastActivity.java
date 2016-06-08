package org.sp.attendance;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import org.sp.attendance.utils.CodeManager;

/**
 * Created by Daniel Quah on 21/5/2016
 */
public class CodeBroadcastActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast);
    }

    public void startBroadcast(View view) {
        CodeManager.setupLecturerEnvironment(this, "Lecturer", "000000");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        (findViewById(R.id.layout_code_input)).setVisibility(View.GONE);
        (findViewById(R.id.layout_code_broadcasting)).setVisibility(View.VISIBLE);
    }

    public void stopBroadcast(View view) {
        CodeManager.destroy();
        (findViewById(R.id.layout_code_input)).setVisibility(View.VISIBLE);
        (findViewById(R.id.layout_code_broadcasting)).setVisibility(View.GONE);
    }

}