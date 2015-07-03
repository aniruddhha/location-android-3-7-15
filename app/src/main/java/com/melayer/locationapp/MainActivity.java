package com.melayer.locationapp;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class MainActivity extends Activity {

    private TextView textInfo;
    private LocationManager manager;

    private LocationListener listener = new MyLocations();

    private BroadcastReceiver receiverProximity = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Boolean isEntering = intent.getBooleanExtra("KEY_PROXIMITY_ENTERING",false);

            if(isEntering){

                Toast.makeText(context,"Entering CDAC-ACTS",Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(context,"Exiting CDAC-ACTS",Toast.LENGTH_LONG).show();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textInfo = (TextView)findViewById(R.id.textInfo);

        manager = (LocationManager) getSystemService(LOCATION_SERVICE); //#1

        List<String> listProviders = manager.getAllProviders();

        for(String provider : listProviders){

            textInfo.append("\n "+provider);
        }

        Criteria locationCriteria = new Criteria();
        locationCriteria.setAccuracy(Criteria.ACCURACY_COARSE);
        locationCriteria.setAltitudeRequired(true);
        locationCriteria.setCostAllowed(true);
        locationCriteria.setPowerRequirement(Criteria.POWER_LOW);

        String bestProvider = manager.getBestProvider(locationCriteria,true);//#2

        textInfo.append("\n Best Provider -" + bestProvider);

        manager.requestLocationUpdates(bestProvider, 1000l, 0.5f, listener); // #3

        Intent intentRegion = new Intent("com.melayer.region");

        PendingIntent pendingIntentRegion = PendingIntent.getBroadcast(this,1234,intentRegion,0);
        manager.addProximityAlert(18.56d,73.816d,5,-1,pendingIntentRegion); //#4

        IntentFilter filterProximity = new IntentFilter("com.melayer.region");
        registerReceiver(receiverProximity,filterProximity);
    }

    private class MyLocations implements LocationListener{

        @Override
        public void onLocationChanged(Location location) {

            textInfo.append("\n Lat = "+location.getLatitude()+" Lng - "+location.getLongitude());
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }
}
