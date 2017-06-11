package org.sp.attendance.utils.Ntp;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;

import com.github.ybq.android.spinkit.style.Wave;

import org.sp.attendance.R;

import java.util.Date;

public class NtpFactory extends AsyncTask<Void, Void, Date> {

    private Context context;
    private ProgressDialog progressDialog;
    private static Date date;

    public NtpFactory(Context context){
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(context.getResources().getString(R.string.ntp_init_message));
        Wave mWaveDrawable = new Wave();
        mWaveDrawable.setBounds(0, 0, 100, 100);
        mWaveDrawable.setColor(ContextCompat.getColor(context, R.color.colorAccent));
        progressDialog.setIndeterminateDrawable(mWaveDrawable);
        progressDialog.show();
    }


    @Override
    protected Date doInBackground(Void... params) {
        SntpClient client = new SntpClient();
        if (client.requestTime("time.google.com", 20000 /* 20 seconds timeout */)) {
            long now =
                    client.getNtpTime() + SystemClock.elapsedRealtime() - client.getNtpTimeReference();
            date = new Date(now);
            return date;
        }
        return date;
    }

    @Override
    protected void onPostExecute(Date currentTime){
        super.onPostExecute(date);
        progressDialog.dismiss();
    }
}
