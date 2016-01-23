package com.jit.connector_service;


import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
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

    //CoordinatorLayout coordinatorLayout;
    Handler handler;





    // searchHostpot - for searching other AP
    // isHotspot - acting as AP
    boolean searchHotspot = true, isHotspot = false;


    public ConnectService() {
    }
    @Override
    public void onCreate(){
        super.onCreate();

        handler = new Handler();
        Toast.makeText(this,"Service Created",Toast.LENGTH_LONG).show();




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
        this.runOnUiThread(Timer_Tick);
    }


    private Runnable Timer_Tick = new Runnable() {
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
                wifi.setWifiEnabled(true);
                Toast.makeText(ConnectService.this, "Wifi Active", Toast.LENGTH_SHORT).show();
                Toast.makeText(ConnectService.this, "Searching for DisarmHotspot !!!!", Toast.LENGTH_SHORT).show();

                if (Arrays.asList(wifis).contains("DisarmHotspot")) {
                    // true
                    Toast.makeText(ConnectService.this, "DisarmHotspot Found !!!!", Toast.LENGTH_LONG).show();
                }
            }

        }
    };




    @Override
    public void onStart(Intent intent,int startId){
        Toast.makeText(this,"Wifi Service Started",Toast.LENGTH_LONG).show();

        wifi =(WifiManager)getSystemService(Context.WIFI_SERVICE);
        wifiReciever = new WifiScanReceiver();

        wifi.startScan();

        // Run Thread for Switching Mode
        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                TimerMethod();
            }

        }, 0, 20000);



    }

    private Runnable Timer_Tick = new Runnable() {
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
                wifi.setWifiEnabled(true);
                Toast.makeText(ConnectService.this, "Wifi Active", Toast.LENGTH_SHORT).show();
                Toast.makeText(ConnectService.this, "Searching for DisarmHotspot !!!!", Toast.LENGTH_SHORT).show();

                if (Arrays.asList(wifis).contains("DisarmHotspot")) {
                    // true
                    Toast.makeText(ConnectService.this, "DisarmHotspot Found !!!!", Toast.LENGTH_LONG).show();
                }
            }

        }
    };


    @Override
    public void onDestroy(){
        wifi.disconnect();
        Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_SHORT).show();
        Toast.makeText(this,"Service Created",Toast.LENGTH_LONG).show();
    }



    @Override
    public IBinder onBind(Intent intent) {
        super.onCreate();

        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }



    private class WifiScanReceiver extends BroadcastReceiver {

        public void onReceive(Context c, Intent intent) {
            final List<ScanResult> wifiScanList = wifi.getScanResults();
            wifis = new String[wifiScanList.size()];

            for(int i = 0; i < wifiScanList.size(); i++){
                wifis[i] = ((wifiScanList.get(i)).SSID);

            }

            lv.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, wifis));

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ConnectService.this);

                    // Setting Dialog Title
                    alertDialog.setTitle("Connect");
                    for (int i = 0; i < wifis.length; i++) {

                        // Setting Dialog Message
                        alertDialog
                                .setMessage("Connect to " + wifis[position]);
                    }

                    // Setting Positive "Yes" Button
                    alertDialog.setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            // Write your code here to invoke YES event
                            Toast.makeText(getApplicationContext(), "Connecting", Toast.LENGTH_SHORT).show();

                            //Connecting to specific network
                            WifiConfiguration conf = new WifiConfiguration();

                            conf.SSID = "\" " + wifis[position] + "\"";
                            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);



                            wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                            wifi.addNetwork(conf);

                            List<WifiConfiguration> list = wifi.getConfiguredNetworks();
                            for (WifiConfiguration i : list) {
                                if (i.SSID != null && i.SSID.equals("\"" + wifis[position] + "\"")) {
                                    wifi.disconnect();
                                    wifi.enableNetwork(i.networkId, true);
                                    wifi.reconnect();
                                    Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();

                                    break;
                                }
                            }

                        }
                    });

                    // Setting Negative "NO" Button
                    alertDialog.setNegativeButton("Disconnect", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to invoke NO event

                            dialog.cancel();
                        }

                    });
                    alertDialog.show();
                }
            });

        }


    }

}
