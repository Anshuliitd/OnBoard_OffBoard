package com.example.adityashekhar.onboardbeacon;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NextStopActivity extends AppCompatActivity implements
        ConnectionCallbacks, OnConnectionFailedListener, LocationListener
{

    private static final String EXTRA_MESSAGE1 = "com";
    ProgressBar _progressBar;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    Bus q =null;
    TextView textView;
    TextView _latitude, _longitude;
    TextView nextStop;
    ListView distanc;
    ListView distanc2;
    ListView distanc3;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_stop);
        Intent intent = getIntent();
        // q = (Bus) intent.getStringArrayExtra(DisplayMessageActivity.EXTRA_MESSAGE1);
        String message = intent.getStringExtra(DisplayMessageActivity.EXTRA_MESSAGE1);
        textView = (TextView) findViewById(R.id.textView2);
        ArrayList<Bus> tt = DisplayMessageActivity.b.getList();
        int check=0;
        for(int i=0;i<tt.size();i++)
        {
            q = tt.get(i);
            if(q.getName().equals(message)){
                check = 1;
                break;
            }
        }
        if(check==1)
        {
          //  textView.setText("Got the bus number");
        }
        else
        {
         //   textView.setText("Error");
        }
        _latitude = (TextView) findViewById(R.id.textView3);
        _longitude = (TextView) findViewById(R.id.textView4);
        nextStop = (TextView) findViewById(R.id.textView5);
        distanc = (ListView) findViewById(R.id.listView2);
        distanc2 = (ListView) findViewById(R.id.listView3);
        distanc3 = (ListView) findViewById(R.id.listView4);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        if(mGoogleApiClient!=null)
        {
            Toast.makeText(this, "Connected!", Toast.LENGTH_SHORT).show();
            mGoogleApiClient.connect();
        } else
        {
            Toast.makeText(this, "Not Connected!", Toast.LENGTH_SHORT).show();
        }
    }

    public String getLastStop(Bus b, double longi, double lat)
    {
        String s = "Wait";
        String s1, s2;
        double d1, d2, d3, d4;
        int i1, i2, i3, i4;
        ArrayList<stop> t = b.getStopList();
        ArrayList<Double> distances = new ArrayList<Double>();
        ArrayList<Double> latis = new ArrayList<Double>();
        ArrayList<Double> longis = new ArrayList<Double>();
        for(int i=0;i<t.size();i++)
        {
            stop ee = t.get(i);
            double dd1, dd2;
            dd1 = ee.getLatitude();
            dd2 = ee.getLongitude();
            //    LatLng  latLngA = new LatLng(dd1, dd2);
            //    LatLng  latLngB = new LatLng(lat, longi);
            Location locationA = new Location("A");
            Location locationB = new Location("B");
            locationA.setLatitude(dd1);
            locationA.setLongitude(dd2);
            latis.add(i, dd1);
            longis.add(i, dd2);
            locationB.setLatitude(lat);
            locationB.setLongitude(longi);
            double dd = locationA.distanceTo(locationB);
            //   double a = (double) 0;
            distances.add(i,dd);

        }
        ArrayAdapter adapter = new ArrayAdapter<Double>(this, android.R.layout.simple_list_item_1,distances);
        //    ListView listView = (ListView) findViewById(R.id.listView);
        distanc.setAdapter(adapter);
        //    ArrayAdapter adapter1 = new ArrayAdapter<Double>(this, android.R.layout.simple_list_item_1,longis);
        //    ListView listView = (ListView) findViewById(R.id.listView);
        //   distanc2.setAdapter(adapter1);
        //    return s;
        d1 = distances.get(0);
        i1 =0;
        //  assume first element of array is the second largest
        d2 = distances.get(1);
        i2=1;
        double temp;
        if (d2 < d1)
        {
            int y;
            y=i1;
            i1= i2;
            i2 = y;
            temp = d1;
            d1 = d2;
            d2 = temp;
        }
        for (int i = 2; i < distances.size();	i++)
        {
            if (distances.get(i) < d1)
            {
                d2 = d1;
                i2 = i1;
                d1 = distances.get(i);
                i1 = i;
            }
            else if (distances.get(i) < d2)
            {
                d2 = distances.get(i);
                i2 = i;
            }
        }
        return (t.get(i1).getName());
       // Calendar c = Calendar.getInstance();
       // int second1 = c.get(Calendar.SECOND);
        //    int second2;
       /* while (c.get(Calendar.SECOND) - second1 < 2)
        {

        }*/
        //return s;
        /*lat = mLastLocation.getLatitude();
        longi = mLastLocation.getLongitude();
        LatLng latLngA = new LatLng(lat,longi);
        LatLng latLngB = new LatLng(t.get(i1).getLatitude(), t.get(i1).getLongitude());
        LatLng latLngC = new LatLng(t.get(i2).getLatitude(), t.get(i2).getLongitude());
        Location A = new Location("A");
        Location B = new Location("B");
        Location C = new Location("C");
        A.setLatitude(latLngA.latitude);
        A.setLatitude(latLngA.longitude);
        B.setLatitude(latLngB.latitude);
        B.setLatitude(latLngB.longitude);
        C.setLatitude(latLngC.latitude);
        C.setLatitude(latLngC.longitude);
        d3 = A.distanceTo(B);
        d4 = A.distanceTo(C);
        if(d3<d1)
        {
            s = t.get(i1).getName();
        }
        else
        {
            s = t.get(i2).getName();
        }*/

    }
    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(this, "Settings requests to be called!", Toast.LENGTH_SHORT).show();
        settingRequest();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Connection Suspended!", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection Failed!", Toast.LENGTH_SHORT).show();
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, 90000);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("Current Location", "Location services connection failed with code " + connectionResult.getErrorCode());
        }

    }

    public void settingRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);    // 10 seconds, in milliseconds
        mLocationRequest.setFastestInterval(1000);   // 1 second, in milliseconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.
                        getLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(NextStopActivity.this, 1000);
                        } catch (SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case 1000:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        getLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(this, "Location Service not Enabled", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    public void getLocation() {
        Toast.makeText(this, "Inside getLocation", Toast.LENGTH_SHORT).show();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        } else {
            /*Getting the location after aquiring location service*/
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

            if (mLastLocation != null) {
                Toast.makeText(this, "Just showing location", Toast.LENGTH_SHORT).show();
                //    _progressBar.setVisibility(View.INVISIBLE);
                double longi = mLastLocation.getLongitude();
                double lat = mLastLocation.getLatitude();
                _latitude.setText("Latitude: " + String.valueOf(lat));
                _longitude.setText("Longitude: " + String.valueOf(longi));
                String stopa = getLastStop(q,longi, lat);
                nextStop.setText(stopa);
                //    finish();
            } else {
                /*if there is no last known location. Which means the device has no data for the loction currently.
                * So we will get the current location.
                * For this we'll implement Location Listener and override onLocationChanged*/
                Log.i("Current Location", "No data for location found");

                if (!mGoogleApiClient.isConnected())
                    mGoogleApiClient.connect();

                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, NextStopActivity.this);
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        //    _progressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(this, "Just showing location", Toast.LENGTH_SHORT).show();
        double longi = mLastLocation.getLongitude();
        double lat = mLastLocation.getLatitude();
        _latitude.setText("Latitude: " + String.valueOf(lat));
        _longitude.setText("Longitude: " + String.valueOf(longi));
        //    double longi = mLastLocation.getLongitude();
        //    double lat = mLastLocation.getLatitude();
        String stopa = getLastStop(q, longi, lat);
        nextStop.setText(stopa);
        //    finish();
    }
}