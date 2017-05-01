package shashank.grimreaper.smartsuraksha24x7;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
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
import android.telephony.TelephonyManager;
import android.text.style.LocaleSpan;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.plivo.endpoint.Endpoint;
import com.plivo.endpoint.EventListener;
import com.plivo.endpoint.Incoming;
import com.plivo.endpoint.Outgoing;


import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements LocationListener{//},EventListener{
    DrawerLayout dlayout;
    ActionBarDrawerToggle toggle;
    ImageView policeStation,hospital ;
    ImageView panicButton ,scream,camera,fakecall;
    protected LocationManager locationManager;
    Double latitude,longitude;
    SQLiteDatabase db;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String primaryContactName,primaryContactPhno;
    SmsManager smsManager;
    public final static String PLIVO_USERNAME = "shashank95";
    public final static String PLIVO_PASSWORD = "shashank95";
    //public final static String EXTRA_MESSAGE = "com.plivo.example.MESSAGE";
    //Endpoint endpoint = Endpoint.newInstance(true, this);
    //Outgoing outgoing = new Outgoing(endpoint);

    //public static String PHONE_NUMBER = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.mytoolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        //Log.v("PlivoOutbound", "Trying to log in");
        //endpoint.login(PLIVO_USERNAME,PLIVO_PASSWORD);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sp.edit();

        DBHelper helper = new DBHelper(this);
        db = helper.getWritableDatabase();

        primaryContactName = sp.getString("primaryContactName","dummy");
        primaryContactPhno = sp.getString("primaryContactPhno","123");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if ( ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED ) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
        else{
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},200);
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
                }
            }
        }

        policeStation = (ImageView)findViewById(R.id.policeStation);
        hospital = (ImageView)findViewById(R.id.hospital);
        scream = (ImageView)findViewById(R.id.scream);
        fakecall = (ImageView)findViewById(R.id.fakecall);


        policeStation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,NearestPoliceStation.class));
            }
        });

        hospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,NearestHospital.class));
            }
        });


        fakecall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String url = "https://api.plivo.com/v1/Account/MANZLLZJFKMDCYZDI5ZM/Call/";
                String url = "https://api.voice2phone.com/call";
                //Log.d("plivo",url);
                com.android.volley.RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("plivo_response", response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("plivo error", error.getMessage() + " ");
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("from","919199095326");
                        params.put("to","918789821093");
                        params.put("answer_url","54.214.95.24/sentcall.php");
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        //String credentials = "MANZLLZJFKMDCYZDI5ZM:MTVjMzYwMTdhMzk5YmM5MDk5ZjRlMTkyMzU1MDk3";
                        String credentials = ":application/json";
                        String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                        headers.put("Content-Type", "application/json");
                        headers.put("Authorization", auth);
                        return headers;
                    }
                };
                queue.add(postRequest);
            }
        });


        panicButton = (ImageView)findViewById(R.id.panicButton);
        panicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String uri = "http://maps.google.com/maps?saddr=" + currentLocation.getLatitude()+","+currentLocation.getLongitude();
                primaryContactName = sp.getString("primaryContactName","dummy");
                primaryContactPhno = sp.getString("primaryContactPhno","123");

                smsManager = SmsManager.getDefault();
                StringBuffer smsBody = new StringBuffer();

                String latitude = sp.getString("latitude","23.412894");
                String longitude = sp.getString("longitude","85.44175");

                String tempUrl = "http://maps.google.com/?q="+latitude+","+longitude;
                smsBody.append("Please help me. I am at this location : " + tempUrl);

                ArrayList<String> contactNos = getSMSnos();
                if(contactNos.size() == 0){
                    Log.d("contactnossize",contactNos.size()+"");
                    Toast.makeText(MainActivity.this,"No emergency contacts present",Toast.LENGTH_LONG).show();
                    return ;
                }
                //Toast.makeText(MainActivity.this,contactNos.size()+"",Toast.LENGTH_LONG).show();
                for(int i = 0 ;i < contactNos.size();i++){
                    smsManager.sendTextMessage(contactNos.get(i), null, smsBody.toString(), null, null);
                }
                Toast.makeText(MainActivity.this,"SMS have been sent to emergency contacts",Toast.LENGTH_LONG).show();

                if(primaryContactName.equals("dummy")){
                    if(primaryContactPhno.equals("123")) {
                        Toast.makeText(MainActivity.this, "Select a primary emergency contact to call.", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                String phoneNumber = "tel:"+primaryContactPhno;
                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(phoneNumber));
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("Call permission", "denied");
                    return;
                }
                Log.d("Call", "success");
                startActivity(callIntent);
                //call primary emergency contact
                /*smsManager.sendTextMessage("+918299807010", null, smsBody.toString(), null, null);
                smsManager.sendTextMessage("+919771661256", null, smsBody.toString(), null, null);*/
            }
        });





        camera = (ImageView)findViewById(R.id.camera);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //primaryContactName = sp.getString(primaryContactName,"dummy");
                //primaryContactPhno = sp.getString(primaryContactPhno,"123");
                /*if(primaryContactName.equals("dummy")){
                    Toast.makeText(MainActivity.this,"No primary emergency contact present to send live streaming...",Toast.LENGTH_LONG).show();
                }
                else {*/
                    Toast.makeText(MainActivity.this,"Sending SMS",Toast.LENGTH_LONG).show();
                    smsManager = SmsManager.getDefault();
                    StringBuffer smsBody = new StringBuffer();
                    TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    String IMEI = mngr.getDeviceId();
                    Log.d("IMEI",IMEI);
                    String latitude = sp.getString("latitude", "23.412");
                    String longitude = sp.getString("longitude", "85.441");
                    String tempUrl = "http://maps.google.com/?q=" + latitude + "," + longitude;
                    smsBody.append("Please help me. I am at this location : " + tempUrl);
                    smsBody.append(" Please watch my current situation using this live streaming link: ");
                    smsBody.append("www.smartsuraksha24x7.com/");
                    Log.d("primaryNo",smsBody.toString());
                    ArrayList<String> parts = smsManager.divideMessage(smsBody.toString());
                    smsManager.sendMultipartTextMessage(primaryContactPhno, null, parts, null, null);
                    Intent intent = new Intent(MainActivity.this, LiveStreaming.class);
                    startActivity(intent);
                //}
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
                    Intent intent = new Intent(MainActivity.this,MedicalInformation.class);
                    startActivity(intent);
                }
                else if(id == R.id.emergencyContacts){
                    Intent intent = new Intent(MainActivity.this,EmergencyContacts.class);
                    startActivity(intent);
                }
                else if(id == R.id.selfDefenseTechniques){
                    Intent intent = new Intent(MainActivity.this,SelfDefenseTechniques.class);
                    startActivity(intent);
                }
                dlayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    public void livestreaming(){
        ;
    }

    public ArrayList<String> getSMSnos(){
        ArrayList<String> contacts = new ArrayList<>();
        Cursor cursor = db.rawQuery("select Number from EmergencyContacts",null);
        if(cursor != null && cursor.moveToFirst()) {
            if (cursor.getCount() > 0) {
                while(cursor.moveToNext()) {
                    String phone = cursor.getString(cursor.getColumnIndex("Number"));
                    contacts.add(phone);
                }
            }
        }
        return contacts;
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
        editor.putString("latitude",latitude+"");
        editor.putString("longitude",longitude+"");
        editor.commit();
        //Toast.makeText(getApplicationContext(),latitude +" " + longitude,Toast.LENGTH_LONG).show();
    }

    /*public void callNow() {
        // Log into plivo cloud
        outgoing = endpoint.createOutgoingCall();
        Log.v("PlivoOutbound", "Create outbound call object");
        PHONE_NUMBER = "+919199095326";
        Log.v("PlivoOutbound", PHONE_NUMBER);
        outgoing.call(PHONE_NUMBER);
    }*/

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }
    @Override
    public void onProviderEnabled(String s) {

    }
    @Override
    public void onProviderDisabled(String s) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            switch (requestCode) {
                case 200: {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                        // permission was granted, yay! Do the
                        // contacts-related task you need to do.
                    } else {
                        Toast.makeText(this,"Sorry, please provide Locations permission. Try again",Toast.LENGTH_LONG).show();
                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.
                    }
                    return;
                }

                // other 'case' lines to check for other
                // permissions this app might request
            }
    }

    /*@Override
    public void onLogin() {

    }

    @Override
    public void onLogout() {

    }

    @Override
    public void onLoginFailed() {

    }

    @Override
    public void onIncomingDigitNotification(String s) {

    }

    @Override
    public void onIncomingCall(Incoming incoming) {

    }

    @Override
    public void onIncomingCallHangup(Incoming incoming) {

    }

    @Override
    public void onIncomingCallRejected(Incoming incoming) {

    }

    @Override
    public void onOutgoingCall(Outgoing outgoing) {

    }

    @Override
    public void onOutgoingCallAnswered(Outgoing outgoing) {

    }

    @Override
    public void onOutgoingCallRejected(Outgoing outgoing) {

    }

    @Override
    public void onOutgoingCallHangup(Outgoing outgoing) {

    }

    @Override
    public void onOutgoingCallInvalid(Outgoing outgoing) {

    }*/
}
