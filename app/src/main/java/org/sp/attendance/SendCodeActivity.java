package org.sp.attendance;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Daniel Quah on 3/6/2016.
 */
public class SendCodeActivity extends Activity implements View.OnClickListener{

    private EditText mMessageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast);

        findViewById(R.id.button_send).setOnClickListener(this);
        mMessageText = (EditText) findViewById(R.id.edittext_message);
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMessageText.setFocusableInTouchMode(true);
        mMessageText.requestFocus();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.button_send:

                new AlertDialog.Builder(this)
                        .setTitle(R.string.please_confirm)
                        // TODO Fix "ATS Code is: " into string.
                        // If  mMessageText.getText().toString is set, weird numbers will be displayed
                        .setMessage("ATS Code is: " + mMessageText.getText() + ". Would you like to continue?")
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // Temporary hack to save ATS code across activities
                                SharedPreferences ats = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor editor = ats.edit();
                                editor.putString("ATS Code", mMessageText.getText().toString());
                                editor.apply();

                                Toast.makeText(getApplicationContext(),R.string.code_sent, Toast.LENGTH_LONG).show();
                                Intent codeBroadcastActivity = new Intent(getApplicationContext(), CodeBroadcastActivity.class);
                                startActivity(codeBroadcastActivity);
                            }
                        }).create().show();
                break;
        }
    }

}
