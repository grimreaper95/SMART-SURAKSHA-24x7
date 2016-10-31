package shashank.grimreaper.smartsuraksha24x7;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.style.LocaleSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LocationListener{

    DrawerLayout dlayout;
    ActionBarDrawerToggle toggle;
    ImageView policeStation,hospital ;
    ImageView panicButton ,scream;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    Double latitude,longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.mytoolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }


        policeStation = (ImageView)findViewById(R.id.policeStation);
        hospital = (ImageView)findViewById(R.id.hospital);
        scream = (ImageView)findViewById(R.id.scream);


        policeStation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

             ;//   startActivity(new Intent(MainActivity.this,NearestPoliceStation.class));
            }
        });

        panicButton = (ImageView)findViewById(R.id.panicButton);
        panicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String uri = "http://maps.google.com/maps?saddr=" + currentLocation.getLatitude()+","+currentLocation.getLongitude();
                SmsManager smsManager = SmsManager.getDefault();
                StringBuffer smsBody = new StringBuffer();

                String tempUrl = "http://maps.google.com/?q=23.412894,85.441745";
                smsBody.append("Please help me. I am at this location : " + tempUrl);
                smsManager.sendTextMessage("+919199095326", null, smsBody.toString(), null, null);
                smsManager.sendTextMessage("+919771661256", null, smsBody.toString(), null, null);
            }
        });

        scream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Screams.class);
                startActivity(intent);
            }
        });



        dlayout = (DrawerLayout)findViewById(R.id.mydrawerlayout);
        toggle = new ActionBarDrawerToggle(this,dlayout,0,0);
        dlayout.addDrawerListener(toggle);
        dlayout.setScrimColor(getResources().getColor(android.R.color.transparent));
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                final int id = item.getItemId();
                if(id == R.id.medicalInformation){
                    //      backgrnd_frag_is_home = true;
                    /*FragmentTransaction ft = manager.beginTransaction();
                    ft.replace(R.id.dummy,req_frag);
                    ft.commit();
                    */
                }
                else if(id == R.id.settings){
                    //    backgrnd_frag_is_home = false;
                    /*
                    FragmentTransaction ft = manager.beginTransaction();
                    ft.replace(R.id.dummy,sett_frag);
                    ft.commit();
                    */
                }
                dlayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                dlayout.openDrawer(GravityCompat.START);  // OPEN DRAWER
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        //Toast.makeText(getApplicationContext(),latitude +" " + longitude,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
