package shashank.grimreaper.smartsuraksha24x7;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Shashank on 23-10-2016.
 */

public class NearestPoliceStation extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mGoogleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.police_station);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

    }


     /* function to load map. If map is not created it will create it for you*/


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mGoogleMap.setMyLocationEnabled(true);
        } else {
            Toast.makeText(NearestPoliceStation.this, "You have to accept to enjoy all app's services!", Toast.LENGTH_LONG).show();
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mGoogleMap.setMyLocationEnabled(true);
            }
            mGoogleMap.setTrafficEnabled(true);
            mGoogleMap.setIndoorEnabled(true);
            mGoogleMap.setBuildingsEnabled(true);
            mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
            if (mGoogleMap != null) {
                mGoogleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location arg0) {
                        // TODO Auto-generated method stub
                        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(arg0.getLatitude(), arg0.getLongitude()));
                        CameraUpdate zoom = CameraUpdateFactory.zoomTo(12);
                        mGoogleMap.moveCamera(center);
                        mGoogleMap.animateCamera(zoom);
                    }
                });
            }
        }
    }
}
