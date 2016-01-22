package com.jit.connector_service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;

public class ConnectService extends Service {
    // Variable declarations
    public static final String TAG = "disarmconnect";
    ListView lv;
    TextView my_wifi_name;
    WifiManager wifi;
    String wifis[];
    WifiScanReceiver wifiReciever;
    boolean b,c,wifiState;
    Context context;
    Timer myTimer,myTimer1;
    CoordinatorLayout coordinatorLayout;







    // searchHostpot - for searching other AP
    // isHotspot - acting as AP
    boolean searchHotspot = true, isHotspot = false;


    public ConnectService() {
    }
    @Override
    public void onCreate(){
        super.onCreate();
        Toast.makeText(this,"Service Created",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart(Intent intent,int startId){
        Toast.makeText(this,"Service Started",Toast.LENGTH_LONG).show();
    }
    @Override
    public void onDestroy(){
        Toast.makeText(this,"Service Destroyed",Toast.LENGTH_LONG).show();

    }
    @Override
    public IBinder onBind(Intent intent) {
        super.onCreate();

        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
