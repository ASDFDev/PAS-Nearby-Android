package org.sp.attendance;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import org.sp.attendance.utils.CodeManager;
import org.sp.attendance.utils.DatabaseManager;

/**
 * Created by Daniel Quah on 21/5/2016
 */
public class CodeBroadcastActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast);
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

    public void startBroadcast(View view) {
        final String code = ((EditText) findViewById((R.id.textCode))).getText().toString();
        if (code.matches("")){
            new AlertDialog.Builder(CodeBroadcastActivity.this)
                    .setTitle(R.string.title_warning)
                    .setMessage(R.string.error_code_disappeared)
                    .setCancelable(false)
                    .setPositiveButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .create()
                    .show();
            return;
        }
        new AlertDialog.Builder(CodeBroadcastActivity.this)
                .setTitle(R.string.title_warning)
                .setMessage(getResources().getString(R.string.continue_confirmation) + code + getResources().getString(R.string.continue_confirmation2))
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        (findViewById(R.id.layout_code_input)).setVisibility(ScrollView.GONE);
                        (findViewById(R.id.layout_code_broadcasting)).setVisibility(ScrollView.VISIBLE);
                        CodeManager.setupLecturerEnvironment(CodeBroadcastActivity.this, code);
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .create()
                .show();
    }

        public void stopBroadcast(View view) {
        CodeManager.destroy();
        DatabaseManager.closeDatabaseForLecturer();
        (findViewById(R.id.layout_code_input)).setVisibility(ScrollView.VISIBLE);
        (findViewById(R.id.layout_code_broadcasting)).setVisibility(ScrollView.GONE);
    }

    // Reserved space for future database management for staff

}