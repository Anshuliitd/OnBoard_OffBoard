package com.example.adityashekhar.onboardbeacon;

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
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.RemoteException;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.altbeacon.beacon.BeaconManager.getInstanceForApplication;

public class RestrictedActivity extends AppCompatActivity implements BeaconConsumer {

    private Set<String> restrictedset;
    private ArrayList<String> restrictedList;

    private BluetoothAdapter mBluetoothAdapter;
    private BeaconManager mBeaconManager;
    private BluetoothGatt mGatt;
    private int REQUEST_ENABLE_BT=1;
    private Boolean mScanning= false;

    private Region mBeaconRegion = new Region("MyBeacons", Identifier.parse("74278bda-b644-4520-8f0c-720eaf059935"),Identifier.parse("65504"),Identifier.parse("65505"));

    private Button addBus;
    private Button clearList ;
    private ListView BusList ;
    private ArrayAdapter<String> adapter;
    private Handler mHandler;
    private Boolean isBusFound= false;
    private TextToSpeech tts;
    private SharedPreferences setPref;
    private Beacon BeaconFound;
    private String BusFound= new String();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restricted);

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.UK);
                }
            }
        });

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mHandler = new Handler();
        mBeaconManager = getInstanceForApplication(this);
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        mBeaconManager.bind(this);
        setPref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        Set<String> test = setPref.getStringSet("set", null);
        if (test != null) {
            restrictedset = test;
            restrictedList = convertSetToArray(test);
        } else {
            restrictedset = new HashSet<String>();
            restrictedList = new ArrayList<String>();
            SharedPreferences.Editor edit = setPref.edit();
            edit.putStringSet("", restrictedset);
            edit.commit();
        }

        addBus = (Button) findViewById(R.id.button5);
        clearList = (Button) findViewById(R.id.button6);
        BusList = (ListView) findViewById(R.id.list2);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, restrictedList);
        BusList.setAdapter(adapter);

        BusList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                BusFound = String.valueOf(BusList.getItemAtPosition(position));
                final Handler hand = new Handler();
                hand.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startBeaconMonitoring(true);
                    }
                }, Constants.connectionTime);

                if(isBusFound==true)
                {
                    final BluetoothDevice  device = mBluetoothAdapter.getRemoteDevice(BeaconFound.getBluetoothAddress());
                    connectToDevice(device);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            disconnectDevice();
                            Toast.makeText(RestrictedActivity.this, device.getName()+" selected", Toast.LENGTH_LONG).show();
                        }
                    }, Constants.connectionTime);
                }
                else
                {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            disconnectDevice();
                            Toast.makeText(RestrictedActivity.this,"Selected not in range", Toast.LENGTH_LONG).show();
                        }
                    }, Constants.connectionTime);
                }
            }
        });

        addBus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alert = new AlertDialog.Builder(RestrictedActivity.this);
                final EditText edittext = new EditText(RestrictedActivity.this);
                alert.setMessage("Enter Bus");
                alert.setTitle("Add Bus to Restricted Set");
                alert.setView(edittext);
                alert.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String busName = edittext.getText().toString();
                        if (restrictedset != null) {
                            restrictedset.add(busName);
                        } else {
                            restrictedset = new HashSet<String>();
                            restrictedset.add(busName);
                        }
                        SharedPreferences.Editor prefEditor = setPref.edit();
                        prefEditor.putStringSet("set", restrictedset);
                        prefEditor.commit();
                        restrictedList.add(busName);
                        adapter.notifyDataSetChanged();
                        BusList.setAdapter(adapter);
                        System.out.println(setPref.getStringSet("set", null).toString());
                        //repeatTask();
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
                alert.show();
            }
        });

        clearList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder alert = new AlertDialog.Builder(RestrictedActivity.this);
                alert.setMessage("Do you want to continue?");
                alert.setTitle("Clear List");
                alert.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        restrictedset = new HashSet<String>();
                        SharedPreferences.Editor prefEditor = setPref.edit();
                        prefEditor.putStringSet("set", restrictedset);
                        prefEditor.commit();
                        restrictedList.clear();
                        adapter.notifyDataSetChanged();
                        BusList.setAdapter(adapter);
                        // repeatTask();
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
                alert.show();
            }
        });
    }

    public void connectToDevice(BluetoothDevice device) {
        if (mGatt == null) {
            mGatt = device.connectGatt(this, false, gattCallback);
        }
    }

    public void disconnectDevice(){
        if (mBluetoothAdapter == null) {
            return;
        }
        if (mGatt != null) {
            mGatt.disconnect();
            mGatt = null;
        }
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                switch (newState) {
                    case BluetoothProfile.STATE_CONNECTED:
                        Log.i("gattCallback", "STATE_CONNECTED");
                        gatt.discoverServices();
                        break;
                    case BluetoothProfile.STATE_DISCONNECTED:
                        Log.e("gattCallback", "STATE_DISCONNECTED");
                        break;
                    default:
                        Log.e("gattCallback", "STATE_OTHER");
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                List<BluetoothGattService> services = gatt.getServices();
                Log.i("onServicesDiscovered", services.toString());
                gatt.readCharacteristic(services.get(1).getCharacteristics().get(0));
                writeCharacteristic(Constants.sendData);
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                Log.i("onCharacteristicRead", characteristic.toString());
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

    private ArrayList<String> convertSetToArray(Set<String> test) {
        ArrayList<String > l  = new ArrayList<String>();
        for (Iterator<String> it = test.iterator(); it.hasNext(); ) {
            String busName  = it.next();
            l.add(busName);
        }
        return l;
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
                        if (isBusFound == true) {
                            break;
                        }
                        else
                        {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if(beacon.getBluetoothName()==BusFound)
                                    {
                                        isBusFound=true;
                                        BeaconFound=beacon;
                                    }
                                    else
                                    {
                                        isBusFound=false;
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }
    private void startBeaconMonitoring(final boolean enable) {
        try{
            if(enable  && !mScanning) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        stopBeaconMonitoring();
                        mScanning = false;
                    }
                }, 1000);
                mScanning = true;
                mBeaconManager.startMonitoringBeaconsInRegion(mBeaconRegion);
                mBeaconManager.startRangingBeaconsInRegion(mBeaconRegion);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
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
