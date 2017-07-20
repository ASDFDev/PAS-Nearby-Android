package org.sp.attendance.utils;

/*
 * Copyright 2016-2017 Daniel Quah and Justin Xin
 * 	
 * This file is part of org.sp.attendance
 *
 * ATS_Nearby is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ATS_Nearby is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.NearbyMessagesStatusCodes;
import com.google.android.gms.nearby.messages.PublishCallback;
import com.google.android.gms.nearby.messages.PublishOptions;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeCallback;
import com.google.android.gms.nearby.messages.SubscribeOptions;

import org.sp.attendance.models.DateTime;
import org.sp.attendance.R;
import org.sp.attendance.service.sntp.SntpConsumer;

import java.nio.charset.Charset;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static com.google.android.gms.nearby.messages.Strategy.TTL_SECONDS_INFINITE;

public class CodeManager {

    public boolean isDestroyed = true;
    private static boolean isGoogleApiClientInitialized = false;
    public static boolean resolvingPermissionError = false;
    private static GoogleApiClient googleApiClient;
    private Context ctx;
    private static MessageListener messageListener;
    private static ManagerType globalManagerType;
    private static Message globalCode;
    private static String deviceID, timeStamp, globalStudentID;
    private static int duration;

    public CodeManager(Context context){
        this.ctx = context;
    }

    public void setupLecturerEnvironment(Context context, String code, int duration) {
        DatabaseManager databaseManager = new DatabaseManager(context);
        globalCode = new Message((databaseManager.generateMessage(code)).getBytes(Charset.forName("UTF-8")));
        globalStudentID = null;
        initialize(context, ManagerType.Send, duration);
    }

    public void setupStudentEnvironment(Context context, String studentID) {
        globalCode = null;
        globalStudentID = studentID;
        initialize(context, ManagerType.Receive, duration);
    }



    private void initialize(Context context, ManagerType managerType, int duration) {
        DatabaseManager databaseManager = new DatabaseManager(context);
        ctx = context;
        if (!databaseManager.isDestroyed) {
            databaseManager.initialize(ctx);
        }
        SntpConsumer sntpConsumer = new SntpConsumer(context);
        timeStamp = DateTime.INSTANCE.getTrueTimeToString(sntpConsumer.getNtpTime());
        deviceID = Secure.getString(ctx.getContentResolver(), Secure.ANDROID_ID);
        globalManagerType = managerType;
        messageListener = new MessageListener() {
            @Override
            public void onFound(Message message) {
                databaseManager.submitStudentDevice(new String(message.getContent(), Charset.forName("UTF-8")), deviceID, timeStamp);
            }

            @Override
            public void onLost(final Message message) {

            }
        };
        if (!isGoogleApiClientInitialized) {
            googleApiClient = new GoogleApiClient.Builder(ctx)
                    .addApi(Nearby.MESSAGES_API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {
                            if (globalManagerType == ManagerType.Receive) {
                                receiveCode();
                            } else if (globalManagerType == ManagerType.Send) {
                                broadcastCode(duration);
                            } else {
                                destroy();
                            }
                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            googleApiClient.reconnect();
                        }
                    })
                    .enableAutoManage(((FragmentActivity) ctx), connectionResult ->
                            showNearbyErrorDialog(ctx.getResources().getString(R.string.title_nearby_error),
                            ctx.getResources().getString(R.string.error_nearby_api)))
                    .build();
            isGoogleApiClientInitialized = true;
        } else {
            googleApiClient.reconnect();
        }
        isDestroyed = false;
    }

    public void destroy() {
        if (resolvingPermissionError) {
            if (globalManagerType == ManagerType.Receive) {
                stopReceiveCode();
            } else if (globalManagerType == ManagerType.Send) {
                stopBroadcastCode();
            }
            ctx = null;
            globalCode = null;
            globalStudentID = null;
            globalManagerType = null;
            googleApiClient.disconnect();
            isDestroyed = true;
        }
    }

    private boolean checkNetwork() {
        ConnectivityManager connManager = (ConnectivityManager)
                ctx.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getActiveNetworkInfo();
        return (info != null && info.isConnectedOrConnecting());
    }

    /*
        RECEIVE CODE FROM LECTURER
     */

    private void receiveCode() {
        if (!checkNetwork()) {
            return;
        }
        Strategy PUB_SUB_STRATEGY = new Strategy.Builder().setTtlSeconds(TTL_SECONDS_INFINITE).build();
        SubscribeOptions options = new SubscribeOptions.Builder()
                .setStrategy(PUB_SUB_STRATEGY)
                .setCallback(new SubscribeCallback() {
                    @Override
                    public void onExpired() {
                        super.onExpired();
                        showNearbyErrorDialog(ctx.getResources().getString(R.string.title_nearby_error),
                                ctx.getResources().getString(R.string.error_nearby_timed_out));
                    }
                }).build();
        Nearby.Messages.subscribe(googleApiClient, messageListener, options)
                .setResultCallback(status -> {
                    if (!status.isSuccess()) {
                        handleUnsuccessfulNearbyResult(status);
                    }
                });
    }

    private void stopReceiveCode() {
        Nearby.Messages.unsubscribe(googleApiClient, messageListener);
        googleApiClient.disconnect();
    }

    /*
        LECTURER CODE BROADCAST
     */

    private void broadcastCode(int duration) {
        if (!checkNetwork()) {
            return;
        }
        Strategy PUB_SUB_STRATEGY = new Strategy.Builder().setTtlSeconds(duration).build();
        PublishOptions options = new PublishOptions.Builder()
                .setStrategy(PUB_SUB_STRATEGY)
                .setCallback(new PublishCallback() {
                    @Override
                    public void onExpired() {
                        super.onExpired();
                    }
                }).build();
        Nearby.Messages.publish(googleApiClient, globalCode, options)
                .setResultCallback(status -> {
                    if (!status.isSuccess()) {
                        handleUnsuccessfulNearbyResult(status);
                    }
                });
    }

    private void stopBroadcastCode() {
        Nearby.Messages.unpublish(googleApiClient, globalCode);
        googleApiClient.disconnect();
    }

    private void handleUnsuccessfulNearbyResult(Status status) {
        if (status.getStatusCode() == NearbyMessagesStatusCodes.APP_NOT_OPTED_IN) {
            if (!resolvingPermissionError) {
                try {
                    resolvingPermissionError = true;
                    status.startResolutionForResult((Activity) ctx, 101);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (status.getStatusCode() == ConnectionResult.NETWORK_ERROR) {
                showNearbyErrorDialog(ctx.getResources().getString(R.string.title_nearby_error),
                        ctx.getResources().getString(R.string.error_network_disappeared_generic));
            }
        }
    }

    private void showNearbyErrorDialog(String title, String message){
        new AlertDialog.Builder(ctx)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.dismiss, (dialog, which) -> ((Activity) ctx).finish())
                .create()
                .show();
    }

    private enum ManagerType {
        Receive, Send, Unknown
    }

}
