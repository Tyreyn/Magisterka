package org.PolandSignsDetection;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class GPSTracker extends AppCompatActivity {

    private static final int PERMISSIONS_FINE_LOCATION = 99;
    private Context mContext;

    private Activity mActivity;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private Location location;

    private LocationRequest locationRequest;

    private LocationCallback locationCallback;

    protected double longitude;

    protected double latitude;

    protected double speed;

    public GPSTracker(Context context) {
        this.mContext = context;
        mActivity = (Activity) context;
        locationRequest = new LocationRequest().create();
        // In ms
        locationRequest.setInterval(1000 * 5);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                updateLocation(locationResult.getLastLocation());
            }
        };
        updateGPS();
    }

    @SuppressLint("MissingPermission")
    protected void performLocationUpdate() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        updateGPS();
    }
    private void updateGPS(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mActivity);

        if(ActivityCompat.checkSelfPermission(mContext,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    updateLocation(location);
                }
            });
        }
        else
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                mActivity.requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }
    }


    private void updateLocation(Location location){
        try {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            speed = location.getSpeed();
        }catch(Exception ex)
        {
            longitude = 0;
            latitude = 0;
            speed = 0;
            Toast.makeText(mContext, "GPS niedostępny", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case PERMISSIONS_FINE_LOCATION:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    updateGPS();
                }else{
                    Toast.makeText(mContext, "Aplikacja potrzebuje dostępu do GPS", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }
}