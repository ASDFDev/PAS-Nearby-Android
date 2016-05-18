package org.sp.attendance;

import android.app.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AppIdentifier;
import com.google.android.gms.nearby.connection.AppMetadata;
import com.google.android.gms.nearby.connection.Connections;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by HexGate on 7/5/16.
 */
public class CodeBroadcastActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        Connections.ConnectionRequestListener,
        Connections.EndpointDiscoveryListener,
        Connections.MessageListener{

    private static final long TIMEOUT_ADVERTISE = 1000L * 30L;

    @Retention(RetentionPolicy.CLASS)
    @IntDef({STATE_IDLE, STATE_READY, STATE_ADVERTISING,STATE_CONNECTED})
    public @interface NearbyConnectionState {}
    private static final int STATE_IDLE = 1023;
    private static final int STATE_READY = 1024;
    private static final int STATE_ADVERTISING = 1025;
    private static final int STATE_CONNECTED = 1027;

    /** GoogleApiClient for connecting to the Nearby Connections API **/
    private GoogleApiClient mGoogleApiClient;

    /** The current state of the application **/
    @NearbyConnectionState
    private int mState = STATE_IDLE;

    private boolean mIsHost = false;

    private AlertDialog mConnectionRequestDialog;

    private String mOtherEndpointId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Nearby.CONNECTIONS_API)
                .build();
        startAdvertising();
    }


    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onEndpointLost(String endpointId) {
        // FOR DEBUGGING, REMOVE BEFORE RELEASE!
        Toast.makeText(getApplicationContext(), "END POINT LOST!", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onConnectionSuspended(int i) {
        // FOR DEBUGGING, REMOVE BEFORE RELEASE!
        Toast.makeText(getApplicationContext(), "CONNECTION SUSPENDED!", Toast.LENGTH_LONG).show();
        mGoogleApiClient.reconnect();
    }


    @Override
    public void onConnected(Bundle bundle) {
        // FOR DEBUGGING, REMOVE BEFORE RELEASE!
        Toast.makeText(getApplicationContext(), "CONNECTED!", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onDisconnected(String endpointId) {
        // FOR DEBUGGING, REMOVE BEFORE RELEASE!
        Toast.makeText(getApplicationContext(), "DISCONNECTED!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onEndpointFound(final String endpointId, String deviceId, String serviceId,
                                final String endpointName) {
        // FOR DEBUGGING, REMOVE BEFORE RELEASE!
        Toast.makeText(getApplicationContext(), "END POINT FOUND!", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // FOR DEBUGGING, REMOVE BEFORE RELEASE!
        Toast.makeText(getApplicationContext(), "CONNECTION FAILED!", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onConnectionRequest(final String endpointId, String deviceId, String endpointName,
                                    byte[] payload) {
        mConnectionRequestDialog = new AlertDialog.Builder(this)
                // FOR DEBUGGING, IN THE FUTURE, THIS SHOULD BE INVISIBLE TO USER
                // TODO Accept request automatically without user consent
                .setTitle("Connection Request")
                .setMessage("Do you want to connect to " + endpointName + "?")
                .setCancelable(false)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        byte[] payload = null;
                        Nearby.Connections.acceptConnectionRequest(mGoogleApiClient, endpointId,
                                payload, CodeBroadcastActivity.this)
                                .setResultCallback(new ResultCallback<Status>() {
                                    @Override
                                    public void onResult(Status status) {
                                        if (status.isSuccess()) {
                                            mOtherEndpointId = endpointId;
                                        } else {

                                        }
                                    }
                                });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Nearby.Connections.rejectConnectionRequest(mGoogleApiClient, endpointId);
                    }
                }).create();

        mConnectionRequestDialog.show();
    }

        private void startAdvertising() {
        mIsHost = true;
        List<AppIdentifier> appIdentifierList = new ArrayList<>();
        appIdentifierList.add(new AppIdentifier(getPackageName()));
        AppMetadata appMetadata = new AppMetadata(appIdentifierList);
        long NO_TIMEOUT = 0L;

        String name = null;
        Nearby.Connections.startAdvertising(mGoogleApiClient, name, appMetadata, NO_TIMEOUT,
                this).setResultCallback(new ResultCallback<Connections.StartAdvertisingResult>() {
            @Override
            public void onResult(Connections.StartAdvertisingResult result) {
                if (result.getStatus().isSuccess()) {
                    // FOR DEBUGGING, REMOVE BEFORE RELEASE!
                    Toast.makeText(getApplicationContext(), "DEVICE ADVERTISING!", Toast.LENGTH_LONG).show();
                } else {
                    int statusCode = result.getStatus().getStatusCode();
                    if (statusCode == ConnectionsStatusCodes.STATUS_ALREADY_ADVERTISING){
                        // FOR DEBUGGING, REMOVE BEFORE RELEASE!
                        Toast.makeText(getApplicationContext(), "DEVICE ALREADY ADVERTISING!", Toast.LENGTH_LONG).show();
                    } else {
                        // FOR DEBUGGING, REMOVE BEFORE RELEASE!
                        Toast.makeText(getApplicationContext(), "DEVICE READY FOR ADVERTISING!", Toast.LENGTH_LONG).show();

                    }
                }
            }
        });
    }

    @Override
    public void onMessageReceived(String endpointId, byte[] payload, boolean isReliable) {
        // A message has been received from a remote endpoint.

    }



    public void cancelBroadcast(View view) {
       finish();
    }

}
