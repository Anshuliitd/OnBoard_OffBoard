package com.example.adityashekhar.onboardbeacon;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.RemoteException;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements BeaconConsumer {

    public static final String EXTRA_MESSAGE2 = "com.example.myfirstapp.MESSAGE2";
    private BeaconManager mBeaconManager;
    private BluetoothAdapter mBluetoothAdapter;
    private TextToSpeech tts;
    private int REQUEST_ENABLE_BT = 1;
    private Region mBeaconRegion = new Region("MyBeacons", Identifier.parse("74278bda-b644-4520-8f0c-720eaf059935"),Identifier.parse("65504"),Identifier.parse("65505"));
    private ListView listView;
    private Button scanButton;
    private Button offboard;
    private Button clear;
    private Button Auto_mode;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> BeaconDevices;
    private HashMap<String,Beacon> map;
    private Handler mhandler;
    private BluetoothGatt mGatt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.UK);
                }
            }
        });

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE Not Supported",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.w("BleActivity", "Location access not granted!");
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 42);
        }


        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        mBeaconManager = BeaconManager.getInstanceForApplication(this);
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        mBeaconManager.bind(this);
        map = new HashMap<String,Beacon>();

        listView = (ListView) findViewById(R.id.list);
        scanButton = (Button) findViewById(R.id.button20);

        BeaconDevices = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, BeaconDevices);
        listView.setAdapter(adapter);
        mhandler = new Handler();
        clear = (Button) findViewById(R.id.button11);
        offboard = (Button) findViewById(R.id.button21);
        Auto_mode =(Button) findViewById(R.id.button30);

        Auto_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(MainActivity.this,RestrictedActivity.class);
                startActivity(intent);
            }
        });


        offboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 =new Intent(MainActivity.this,MainActivity2.class);
                startActivity(intent1);
            }
        });
        clear.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                BeaconDevices.clear();
                map.clear();
                adapter.clear();
                adapter.notifyDataSetChanged();
                stopBeaconMonitoring();
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(map.get(listView.getItemAtPosition(position)).getBluetoothAddress());
                if(device!=null)
                {
                    connectToDevice(device);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            disconnectDevice();
                            Toast.makeText(MainActivity.this, device.getName()+" selected", Toast.LENGTH_LONG).show();
                        }
                    }, Constants.connectionTime);
                    Intent intent2 =new Intent(MainActivity.this,DisplayMessageActivity.class);
                    intent2.putExtra(EXTRA_MESSAGE2,String.valueOf(listView.getItemAtPosition(position)));
                    startActivity(intent2);
                }
            }
        });





        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                BeaconDevices.clear();
                map.clear();
                adapter.clear();
                adapter.notifyDataSetChanged();
                startBeaconMonitoring();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(BeaconDevices.size()==0){
                            Toast.makeText(MainActivity.this,Constants.noBusFoundMsg, Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(MainActivity.this, BeaconDevices.size() +" devices found", Toast.LENGTH_LONG).show();
                        }
                    }
                },Constants.SCAN_PERIOD);
            }
        });
    }

    private void startBeaconMonitoring() {
        try{
            mhandler.postDelayed( new Runnable() {
                @Override
                public void run() {
                    stopBeaconMonitoring();
                }
            }, Constants.SCAN_PERIOD);
            mBeaconManager.startMonitoringBeaconsInRegion(mBeaconRegion);
            mBeaconManager.startRangingBeaconsInRegion(mBeaconRegion);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(final String title){
        runOnUiThread(new Runnable() {
            @Override
            public void run(){
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle(title);
                alertDialog.setNeutralButton("ON",new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog,int which)
                    {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });
    }
    @Override
    public void onBeaconServiceConnect() {

        mBeaconManager.setMonitorNotifier(new MonitorNotifier() {

            @Override
            public void didEnterRegion(Region region){

            }

            @Override
            public void didExitRegion(Region region)
            {

            }

            @Override
            public void didDetermineStateForRegion(int state, Region region)
            {

            }
        });
        mBeaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons != null && !beacons.isEmpty()) {
                    for (final Beacon beacon : beacons) {

                        mhandler.post(new Runnable() {
                            @Override
                            public void run() {
                                boolean push = true;
                                for (int i = 0; i < BeaconDevices.size(); i++) {
                                    if (beacon != null) {
                                        if (BeaconDevices.get(i).equals(String.valueOf(beacon.getBluetoothName()))) {
                                            push = false;
                                            break;
                                        }
                                    }
                                }
                                if (push) {
                                    if (beacon != null && beacon.getId1() != null) {
                                        BeaconDevices.add(String.valueOf(beacon.getBluetoothName()));
                                        map.put(beacon.getBluetoothName(),beacon);
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public void connectToDevice(BluetoothDevice device)
    {
        if(mGatt == null)
        {

            mGatt = device.connectGatt(this,false,gattCallback);
        }
    }

    public void disconnectDevice(){
        if(mBluetoothAdapter == null)
        {
            return;
        }
        if(mGatt != null)
        {
            mGatt.disconnect();
            mGatt = null;
        }
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status , int newState){
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    gatt.discoverServices();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                {
                    Log.i("gattCallback", "STATE_DISCONNECTED");
                    break;
                }
                default:
                    Log.e("gattCallback", "STATE_OTHER");

            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt,int status)
        {
            List<BluetoothGattService> services = gatt.getServices();
            gatt.readCharacteristic(services.get(1).getCharacteristics().get(0));
            writeCharacteristic(Constants.sendData);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            //gatt.disconnect();
        }
    };

    public boolean writeCharacteristic(String str) {

        //check mBluetoothGatt is available
        if (mGatt == null) {
            return false;
        }

        // System.out.println("############################### SERVICE:  "+ mGatt.getServices().get(mGatt.getServices().size()-1).getUuid());
        BluetoothGattService Service = mGatt.getService(mGatt.getServices().get(mGatt.getServices().size()-1).getUuid());

        if (Service == null) {
            return false;
        }

        //System.out.println("############################### CHAR:  "+ mGatt.getServices().get(mGatt.getServices().size()-1).getCharacteristics().get(Constants.writeToCharacterisiticIndex).getUuid());
        BluetoothGattCharacteristic charac = Service.getCharacteristic(mGatt.getServices().get(mGatt.getServices().size()-1).getCharacteristics().get(Constants.writeToCharacterisiticIndex).getUuid());
        if (charac == null) {
            return false;
        }

        byte[] value = new byte[20];
        value = str.getBytes();
        charac.setValue(value);
        boolean status = mGatt.writeCharacteristic(charac);
        return status;
    }

    private void stopBeaconMonitoring() {
        try{
            mBeaconManager.stopMonitoringBeaconsInRegion(mBeaconRegion);
            mBeaconManager.stopRangingBeaconsInRegion(mBeaconRegion);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
