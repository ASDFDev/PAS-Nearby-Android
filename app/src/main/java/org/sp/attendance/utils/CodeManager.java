package org.sp.attendance.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AppIdentifier;
import com.google.android.gms.nearby.connection.AppMetadata;
import com.google.android.gms.nearby.connection.Connections;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import org.sp.attendance.ListDialog;
import org.sp.attendance.R;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by HexGate on 7/5/16.
 */
public class CodeManager {

    private static GoogleApiClient googleApiClient;
    private static Context ctx;
    private static Message attendanceCode;
    private static MessageListener messageListener;

    private static ListDialog endpointListDialog = null;

    private static ManagerType globalManagerType;
    private static String globalCode;
    private static String globalLecturerName;
    private static String globalStudentID;

    public static void setupLecturerEnvironment(Context context, String lecturerName, String code) {
        globalCode = code;
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
        googleApiClient = new GoogleApiClient.Builder(ctx)
                .addApi(Nearby.CONNECTIONS_API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        if (globalManagerType == ManagerType.Receive) {
                            // TODO: Implement student ID for sending/receiving
                            receiveCode();
                        } else if (globalManagerType == ManagerType.Send) {
                            broadcastCode();
                            deliverCode();
                        } else {
                            destroy();
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        googleApiClient.reconnect();
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .build();
        googleApiClient.connect();
    }

    public static void destroy() {
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
        ctx = null;
        googleApiClient = null;
    }

    private static boolean checkNetwork() {
        ConnectivityManager connManager = (ConnectivityManager)
                ctx.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getActiveNetworkInfo();
        return (info != null && info.isConnectedOrConnecting());
    }

    public static void receiveCode() {
        if (!checkNetwork()) {
            return;
        }
        if (!googleApiClient.isConnected()) {
            if (!googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        } else {
            String serviceId = ctx.getString(R.string.service_id);
            Nearby.Connections.startDiscovery(googleApiClient, serviceId, 0L, new Connections.EndpointDiscoveryListener() {
                @Override
                public void onEndpointFound(final String endpointId, String deviceId, String serviceId, final String endpointName) {
                    if (endpointListDialog == null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ctx)
                                .setTitle(R.string.lecturer_broadcasting);
                        endpointListDialog = new ListDialog(ctx, builder, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String selectedEndpointName = endpointListDialog.getItemKey(which);
                                String selectedEndpointId = endpointListDialog.getItemValue(which);
                                //TODO: Implement connection to endpoint
                                connectToEndpoint(selectedEndpointId, selectedEndpointName);
                                endpointListDialog.dismiss();
                            }
                        });
                        endpointListDialog.addItem(endpointName, endpointId);
                        endpointListDialog.show();
                    }
                }

                @Override
                public void onEndpointLost(String endpointId) {
                    if (endpointListDialog != null) {
                        endpointListDialog.removeItemByValue(endpointId);
                    }
                }
            })
                    .setResultCallback(new ResultCallback<Status>() {

                        @Override
                        public void onResult(Status status) {
                            int statusCode = status.getStatusCode();
                            if (status.isSuccess()) {
                                // Connection success
                            } else {
                                if (statusCode == 8000) {
                                    new android.app.AlertDialog.Builder(ctx)
                                            .setTitle(R.string.title_network)
                                            .setMessage(R.string.error_network_disappeared)
                                            .setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            }).create().show();
                                } else if (statusCode == 13) {
                                    new android.app.AlertDialog.Builder(ctx)
                                            .setTitle(R.string.title_nearby_error)
                                            .setMessage(R.string.error_generic)
                                            .setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            }).create().show();
                                } else if (statusCode == 8004) {
                                    new android.app.AlertDialog.Builder(ctx)
                                            .setTitle(R.string.title_nearby_error)
                                            .setMessage(R.string.error_nearby_rejected)
                                            .setNegativeButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            }).create().show();
                                }
                            }
                        }
                    });
        }
    }

    /*
        RECEIVE CODE FROM LECTURER
     */

    private static void connectToEndpoint(String endpointId, final String endpointName) {
        // TODO: Payload as student ID?
        byte[] payload = null;
        Nearby.Connections.sendConnectionRequest(googleApiClient, endpointName, endpointId, payload,
                new Connections.ConnectionResponseCallback() {
                    @Override
                    public void onConnectionResponse(String endpointId, Status status,
                                                     byte[] bytes) {
                        if (status.isSuccess()) {
                            Toast.makeText(ctx, "Connection to " + endpointName + " success!",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ctx, "Connection to " + endpointName + " failed! Retrying...",
                                    Toast.LENGTH_LONG).show();
                            receiveCode();
                        }
                    }
                }, new Connections.MessageListener() {
                    @Override
                    public void onMessageReceived(String endpointId, byte[] payload, boolean isReliable) {
                        if (payload != null && isReliable) {
                            new ConnectionManager(ctx).execute("CodeOnly", new String(payload));
                        }
                    }

                    @Override
                    public void onDisconnected(String s) {
                        // Disconnected
                    }
                });
    }

    private static void broadcastCode() {
        if (!checkNetwork()) {
            return;
        }
        if (!googleApiClient.isConnected()) {
            if (!googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        } else {
            List<AppIdentifier> appIdentifierList = new ArrayList<>();
            appIdentifierList.add(new AppIdentifier(ctx.getPackageName()));
            AppMetadata appMetadata = new AppMetadata(appIdentifierList);
            Nearby.Connections.startAdvertising(googleApiClient, globalLecturerName, appMetadata, 0L,
                    new Connections.ConnectionRequestListener() {
                        @Override
                        public void onConnectionRequest(final String endpointId, String deviceId, String endpointName, byte[] payload) {
                            Nearby.Connections.acceptConnectionRequest(googleApiClient, endpointId,
                                    payload, new Connections.MessageListener() {
                                        @Override
                                        public void onMessageReceived(String s, byte[] bytes, boolean b) {
                                        }

                                        @Override
                                        public void onDisconnected(String s) {
                                        }
                                    })
                                    .setResultCallback(new ResultCallback<Status>() {
                                        @Override
                                        public void onResult(Status status) {
                                        }
                                    });
                        }
                    }).setResultCallback(new ResultCallback<Connections.StartAdvertisingResult>() {
                @Override
                public void onResult(Connections.StartAdvertisingResult result) {
                    if (result.getStatus().isSuccess()) {

                    } else {

                    }
                }
            });
        }
    }

    /*
        LECTURER CODE BROADCAST
     */

    private static void deliverCode() {
        Nearby.Connections.sendReliableMessage(googleApiClient, globalLecturerName, globalCode.getBytes());
    }

    private enum ManagerType {
        Receive, Send, Unknown
    }

}
