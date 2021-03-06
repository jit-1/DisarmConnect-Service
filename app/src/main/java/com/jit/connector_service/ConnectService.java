package com.jit.connector_service;


import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

public class ConnectService extends Service {
    // Variable declarations
    public static final String TAG = "disarmconnect";
    ListView lv;
    TextView my_wifi_name;
    WifiManager wifi;
    String wifis[];
    WifiScanReceiver wifiReceiver;
    boolean b,c,wifiState;
    Context context;
    Timer myTimer,myTimer1;
    String networkSSID = "DisarmHotspot"; // AP to search & connect

    //CoordinatorLayout coordinatorLayout;
    Handler handler=new Handler();
    BroadcastReceiver mReceiver;




    // searchHostpot - for searching other AP
    // isHotspot - acting as AP
    boolean searchHotspot = true, isHotspot = false;

    public class WifiScanReceiver extends BroadcastReceiver {

        public void onReceive(Context c, Intent intent) {
            final List<ScanResult> wifiScanList = wifi.getScanResults();

            wifis = new String[wifiScanList.size()];

            for(int i = 0; i < wifiScanList.size(); i++){
                wifis[i] = ((wifiScanList.get(i)).SSID);
                Log.d("WIFI_AP_LIST",wifis[i]);
                //Debug : Returning NULL for some reason
                Toast.makeText(getApplicationContext(),"Adapter",Toast.LENGTH_SHORT).show();
            }


        }


    }


    public ConnectService() {
    }
    @Override
    public void onCreate(){
        super.onCreate();
        Toast.makeText(this,"Service Created",Toast.LENGTH_LONG).show();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        mReceiver=new WifiScanReceiver();
        registerReceiver(mReceiver,filter );
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);


        wifi.startScan();

    }

    private void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }

    private void TimerMethod()
    {
        //This method is called directly by the timer
        //and runs in the same thread as the timer.

        //We call the method that will work with the UI
        //through the runOnUiThread method.
        this.runOnUiThread(TimerTick);
    }


    private Runnable TimerTick = new Runnable() {
        public void run() {

            //This method runs in the same thread as the UI.

            //Do something to the UI thread here
            wifiState = (Math.random() < 0.5);

            // WifiState - 1 (Is Hotspot) || 0 - (CheckHotspot)
            if (wifiState) {
                wifi.setWifiEnabled(false);

                // Check Hotspot on or not
                b = ApManager.isApOn(ConnectService.this);
                if (!b) {
                    ApManager.configApState(ConnectService.this);
                }
                Toast.makeText(ConnectService.this, "Hotspot Active", Toast.LENGTH_SHORT).show();
            } else {
                // Change Hotspot State and enable WIFI to true
                ApManager.configApState(ConnectService.this);
                //wifi.setWifiEnabled(true);
                Toast.makeText(ConnectService.this, "Wifi Active", Toast.LENGTH_SHORT).show();
                Toast.makeText(ConnectService.this, "Searching for DisarmHotspot !!!!", Toast.LENGTH_SHORT).show();
                try {

                    WifiConfiguration conf = new WifiConfiguration();
                    conf.SSID = "\"" + networkSSID + "\"";
                    conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE); // Open Network usd for now
                    //wifi.addNetwork(conf);
                    List<WifiConfiguration> list = wifi.getConfiguredNetworks();
                    for( WifiConfiguration i : list ) {
                        if(i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                            wifi.disconnect();
                            Toast.makeText(ConnectService.this, networkSSID+ " Found !!!!", Toast.LENGTH_LONG).show();
                            wifi.enableNetwork(i.networkId, true);
                            wifi.reconnect();

                            break;
                        }
                    }

                }catch (Exception e) {
                    Log.d("Blank wifis",e.toString());

                }
            }
        }
    };




    @Override
    public void onStart(Intent intent,int startId){
        Toast.makeText(this,"Wifi Service Started",Toast.LENGTH_LONG).show();
        //wifi =(WifiManager)getSystemService(Context.WIFI_SERVICE);
        //wifiReceiver = new WifiScanReceiver();

        //wifi.startScan();

        // Run Thread for Switching Mode
        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                TimerMethod();
            }

        }, 0, 20000);


    }



    @Override
    public void onDestroy(){

        myTimer.cancel();
        handler.removeCallbacks(TimerTick);
        Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_SHORT).show();
        this.unregisterReceiver(mReceiver);
    }



    @Override
    public IBinder onBind(Intent intent) {
        super.onCreate();

        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    public void testAp(MenuItem item) {

        b = ApManager.isApOn(ConnectService.this); // check Ap state :boolean
        c = ApManager.configApState(ConnectService.this); // change Ap state :boolean
        boolean isWifiEnable = wifi.isWifiEnabled();
        if(!isWifiEnable) {
            if (b) {
                Toast.makeText(this, "Hotspot off", Toast.LENGTH_SHORT).show();
              //  Logger.addRecordToLog("Hotspot Switched Off");
            } else {
                if (c) {
                    Toast.makeText(this, "Hotspot state changed (Switch On)", Toast.LENGTH_SHORT).show();
                  //  Logger.addRecordToLog("Hotspot Switched On");
                }
            }
        }
        else {
            Toast.makeText(this, "Disabling Wifi. Press Hotspot Button again !!", Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(false);
        }

        //Change Name of the Created Hotspot
        try {
            Method getConfigMethod = wifi.getClass().getMethod("getWifiApConfiguration");
            WifiConfiguration wifiConfig = (WifiConfiguration) getConfigMethod.invoke(wifi);

            String wifiName = "DisarmHotspot";
            wifiConfig.SSID = "\"" + wifiName + "\"";
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

            Method setConfigMethod = wifi.getClass().getMethod("setWifiApConfiguration", WifiConfiguration.class);
            setConfigMethod.invoke(wifi, wifiConfig);



        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }




}
