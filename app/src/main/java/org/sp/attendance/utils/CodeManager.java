package org.sp.attendance.utils;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.PublishCallback;
import com.google.android.gms.nearby.messages.PublishOptions;
import com.google.android.gms.nearby.messages.SubscribeCallback;
import com.google.android.gms.nearby.messages.SubscribeOptions;

/**
 * Created by HexGate on 7/5/16.
 */
public class CodeManager {

    GoogleApiClient googleApiClient;
    Context ctx;
    Message attendanceCode;
    MessageListener messageListener;

    private Boolean isResolvingError = false;

    public void initialize(Context context) {
        ctx = context;
        googleApiClient = new GoogleApiClient.Builder(ctx)
                .addApi(Nearby.MESSAGES_API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {

                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .build();
    }

    public void initializeMessageListener(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        messageListener = new MessageListener() {
            @Override
            public void onFound(final Message message) {
                final String nearbyMessageString = new String(message.getContent());
            }
            public void onLost(final Message message) {
                final String nearbyMessageString = new String(message.getContent());
            }
        };
    }

    public void broadcastCode(String code) {
        attendanceCode =  new Message(code.getBytes());
        if (!googleApiClient.isConnected()) {
            if (!googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        } else {
            PublishOptions options = new PublishOptions.Builder()
                    .setCallback(new PublishCallback() {
                        @Override
                        public void onExpired() {
                        }
                    }).build();

            Nearby.Messages.publish(googleApiClient, attendanceCode, options)
                    .setResultCallback(new ResultCallback<Status>() {

                        @Override
                        public void onResult(Status status) {
                            if (status.isSuccess()) {
                            } else {
                            }
                        }
                    });
        }
    }

    public void cancelBroadcast() {
        Nearby.Messages.unpublish(googleApiClient, attendanceCode);
    }

    private void receiveCode() {
        if (!googleApiClient.isConnected()) {
            if (!googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        } else {
            SubscribeOptions options = new SubscribeOptions.Builder()
                    .setCallback(new SubscribeCallback() {
                        @Override
                        public void onExpired() {
                        }
                    }).build();

            Nearby.Messages.subscribe(googleApiClient, messageListener, options)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            if (status.isSuccess()) {
                            } else {
                            }
                        }
                    });
        }
    }

    public void cancelReceive() {
        Nearby.Messages.unsubscribe(googleApiClient, messageListener);
    }

    private void handleUnsuccessfulNearbyResult(Status status) {
        if (isResolvingError) {
            return;
        } else if (status.hasResolution()) {
            try {
                isResolvingError = true;
                status.startResolutionForResult(((Activity)ctx), 1001);
            } catch (IntentSender.SendIntentException e) {
                isResolvingError = false;
            }
        } else {
            if (status.getStatusCode() == CommonStatusCodes.NETWORK_ERROR) {
                Toast.makeText(ctx.getApplicationContext(),
                        "No connectivity, cannot proceed. Fix in 'Settings' and try again.",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(ctx.getApplicationContext(), "Unsuccessful: " +
                        status.getStatusMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

}
