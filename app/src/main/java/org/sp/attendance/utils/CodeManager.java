package org.sp.attendance.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.PublishCallback;
import com.google.android.gms.nearby.messages.PublishOptions;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeCallback;
import com.google.android.gms.nearby.messages.SubscribeOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.sp.attendance.ListDialog;
import org.sp.attendance.R;

import java.nio.charset.Charset;


import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by HexGate on 7/5/16.
 */

public class CodeManager {

    // 30 minutes time out for students to submit code
    private static final Strategy PUB_SUB_STRATEGY = new Strategy.Builder()
            .setTtlSeconds(1800).build();
    public static boolean isDestroyed = true;
    private static GoogleApiClient googleApiClient;
    private static Context ctx;
    private static Message attendanceCode;
    private static MessageListener messageListener;
    private static ListDialog endpointListDialog = null;
    private static ManagerType globalManagerType;
    private static Message globalCode;
    private static String globalLecturerName;
    private static String globalStudentID;
    private String android_id = Secure.getString(ctx.getContentResolver(), Secure.ANDROID_ID);

    public static void setupLecturerEnvironment(Context context, String lecturerName, String code) {
        globalCode = new Message((code).getBytes(Charset.forName("UTF-8")));
        globalLecturerName = lecturerName;
        globalStudentID = null;
        initialize(context, ManagerType.Send);
    }

    public static void setupStudentEnvironment(Context context, String studentID) {
        globalCode = null;
        globalLecturerName = null;
        globalStudentID = studentID;
        initialize(context, ManagerType.Receive);
    }

    private static void initialize(Context context, ManagerType managerType) {
        ctx = context;
        globalManagerType = managerType;
        messageListener = new MessageListener() {
            @Override
            public void onFound(Message message) {
                // TODO: Actual code submission
                new AlertDialog.Builder(ctx)
                        .setTitle("Lecturers found")
                        .setMessage("Mr. Tan Ah Beng")
                        .setCancelable(false)
                        .setPositiveButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((Activity) ctx).finish();
                            }
                        })
                        .create()
                        .show();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference();
                myRef.setValue(message.getContent());
            }
            @Override
            public void onLost(final Message message) {
            }
        };
        googleApiClient = new GoogleApiClient.Builder(ctx)
                .addApi(Nearby.MESSAGES_API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(@Nullable Bundle bundle) {
                    if (globalManagerType == ManagerType.Receive) {
                        // TODO: Implement student ID for sending/receiving
                        receiveCode();
                    } else if (globalManagerType == ManagerType.Send) {
                        broadcastCode();
                    } else {
                        destroy();
                    }
                }

                    @Override
                    public void onConnectionSuspended(int i) {
                        googleApiClient.reconnect();
                    }
                })
                .enableAutoManage(((FragmentActivity) ctx), new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        new AlertDialog.Builder(ctx)
                                .setTitle(R.string.title_nearby_error)
                                .setMessage(R.string.error_nearby_oops)
                                .setCancelable(false)
                                .setPositiveButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ((Activity) ctx).finish();
                                    }
                                })
                                .create()
                                .show();
                    }
                })
                .build();
        isDestroyed = false;
    }

    public static void destroy() {
        ctx = null;
        if (globalManagerType == ManagerType.Receive) {
            stopReceiveCode();
        } else if (globalManagerType == ManagerType.Send) {
            stopBroadcastCode();
        }
        globalCode = null;
        globalStudentID = null;
        globalLecturerName = null;
        globalManagerType = null;
        isDestroyed = true;
    }

    private static boolean checkNetwork() {
        ConnectivityManager connManager = (ConnectivityManager)
                ctx.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getActiveNetworkInfo();
        return (info != null && info.isConnectedOrConnecting());
    }

    /*
        RECEIVE CODE FROM LECTURER
     */

    private static void receiveCode() {
        if (!checkNetwork()) {
            return;
        }
        SubscribeOptions options = new SubscribeOptions.Builder()
                .setStrategy(PUB_SUB_STRATEGY)
                .setCallback(new SubscribeCallback() {
                    @Override
                    public void onExpired() {
                        super.onExpired();
                        new AlertDialog.Builder(ctx)
                                .setTitle(R.string.title_nearby_error)
                                .setMessage(R.string.error_nearby_timed_out)
                                .setCancelable(false)
                                .setPositiveButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ((Activity) ctx).finish();
                                    }
                                })
                                .create()
                                .show();
                    }
                }).build();

        Nearby.Messages.subscribe(googleApiClient, messageListener, options)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                        } else {
                            new AlertDialog.Builder(ctx)
                                    .setTitle(R.string.title_nearby_error)
                                    .setMessage(R.string.error_nearby_kekd)
                                    .setCancelable(false)
                                    .setPositiveButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            ((Activity) ctx).finish();
                                        }
                                    })
                                    .create()
                                    .show();
                        }
                    }
                });
    }

    public static void stopReceiveCode() {
        Nearby.Messages.unsubscribe(googleApiClient, messageListener);
    }

    /*
        LECTURER CODE BROADCAST
     */

    private static void broadcastCode() {
        if (!checkNetwork()) {
            return;
        }
        PublishOptions options = new PublishOptions.Builder()
                .setStrategy(PUB_SUB_STRATEGY)
                .setCallback(new PublishCallback() {
                    @Override
                    public void onExpired() {
                        super.onExpired();
                        new AlertDialog.Builder(ctx)
                                .setTitle(R.string.title_nearby_error)
                                .setMessage(R.string.error_nearby_timed_out)
                                .setCancelable(false)
                                .setPositiveButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ((Activity) ctx).finish();
                                    }
                                })
                                .create()
                                .show();
                    }
                }).build();
        Nearby.Messages.publish(googleApiClient, globalCode, options)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                        } else {
                            new AlertDialog.Builder(ctx)
                                    .setTitle(R.string.title_nearby_error)
                                    .setMessage(R.string.error_nearby_pwn)
                                    .setCancelable(false)
                                    .setPositiveButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            ((Activity) ctx).finish();
                                        }
                                    })
                                    .create()
                                    .show();
                        }
                    }
                });
    }

    public static void stopBroadcastCode() {
        Nearby.Messages.unpublish(googleApiClient, globalCode);
    }

    private enum ManagerType {
        Receive, Send, Unknown
    }

}
