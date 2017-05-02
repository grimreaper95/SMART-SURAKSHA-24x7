package shashank.grimreaper.smartsuraksha24x7;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Shashank on 23-10-2016.
 */


public class NearestPoliceStation extends AppCompatActivity implements AsyncDelegate,OnMapReadyCallback,ActivityCompat.OnRequestPermissionsResultCallback,GoogleApiClient.OnConnectionFailedListener,LocationListener {

    GoogleMap mMap;
    private LocationManager locationManager;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;
    private int PROXIMITY_RADIUS = 10000;
    private static final int REQUEST_INTERNET = 200;
    double mLatitude=0;
    double mLongitude=0;
    ListView policeStationList ;
    StringBuilder googlePlacesUrl,googleDistanceUrl,googleDetailedPlacesUrl;
    String curDistance = "100KM";
    ArrayList<PoliceStationAttr> policeStation;
    ArrayList<String> distance;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    ProgressDialog pDialog;
    MyPoliceStationAdapter myPoliceStationAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.police_station);
        Toolbar toolbar = (Toolbar)findViewById(R.id.mytoolbar2);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sp.edit();
        policeStation = new ArrayList<PoliceStationAttr>();
        distance = new ArrayList<>();
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        policeStationList = (ListView)findViewById(R.id.policeStationList);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), true));
        if (location != null) {
            onLocationChanged(location);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(NearestPoliceStation.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_INTERNET);
        }

        else{

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER
            googleMap.addMarker(new MarkerOptions().position(new LatLng(mLatitude, mLongitude)).title("You are here"));
            //googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            googleMap.getUiSettings().setCompassEnabled(true);
            mMap.setMyLocationEnabled(true);
            getData("police");
        }
    }



    public void getData(String type){
        googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + mLatitude + "," + mLongitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&types=" + type);
        googlePlacesUrl.append("&key=" + "AIzaSyCCMmQ04uVMXxzvWUj1MiBKEqKbmwcroGc");
        Log.d("Url",googlePlacesUrl.toString());
        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute();
    }

    @Override
    public void asyncComplete(boolean success) {
        ListView policeStationListUpd = (ListView) findViewById(R.id.policeStationList);
        Log.d("policeSize",policeStation.size()+"");
        View child;
        for (int i = 0; i < policeStation.size(); i++) {
            child = policeStationListUpd.getChildAt(i);
            TextView placeDistance = (TextView) child.findViewById(R.id.placeDistance);
            placeDistance.setText(distance.get(i));
            Log.d(i+"distance123",distance.get(i));
        }
        myPoliceStationAdapter.notifyDataSetChanged();
    }

    public void updateList(){
        ListView policeStationListUpd = (ListView) findViewById(R.id.policeStationList);
        Log.d("policeSize",distance.size()+"");
        View child;
        for (int i = 0; i < distance.size(); i++) {
            child = policeStationListUpd.getChildAt(i);
            TextView placeDistance = (TextView) child.findViewById(R.id.placeDistance);
            placeDistance.setText(distance.get(i));
            Log.d(i+"distance123",distance.get(i));
        }
        myPoliceStationAdapter.notifyDataSetChanged();
    }


    public class MyAsyncTask3 extends AsyncTask<String,String,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            googleDetailedPlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
            googleDetailedPlacesUrl.append("placeid=" + strings[0]);
            googleDetailedPlacesUrl.append("&key=" + "AIzaSyCCMmQ04uVMXxzvWUj1MiBKEqKbmwcroGc");
            Log.d("detailedURL",googleDetailedPlacesUrl+"");
            BufferedReader reader = null;
            String result = null;
            try{
                HttpURLConnection urlConnection = null;
                URL url = new URL(googleDetailedPlacesUrl.toString());
                urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer buffer = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    return null;
                }
                result = buffer.toString();
                Log.d("BUFFER",buffer.toString());
            }catch (Exception e){
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result == null)
                result = " ";
            else{
                try {
                    JSONObject data = new JSONObject(result);
                    JSONObject obj = data.getJSONObject("result");
                    String phno = obj.getString("formatted_phone_number");
                    Log.d("Phone_",phno);
                    Toast.makeText(NearestPoliceStation.this,phno,Toast.LENGTH_LONG).show();
                    String phoneNumber = "tel:"+"123";
                    Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(phoneNumber));
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        Log.d("Call permission", "denied");
                        return;
                    }
                    Log.d("Call", "success");
                    startActivity(callIntent);
                }
                catch(Exception e){
                    Toast.makeText(NearestPoliceStation.this,"Phone no. is not available. Dialing 100.",Toast.LENGTH_LONG).show();
                    String phoneNumber = "tel:"+"321";
                    Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(phoneNumber));
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        Log.d("Call permission", "denied");
                        return;
                    }
                    Log.d("Call", "success");
                    startActivity(callIntent);
                }
            }
            Log.d("data1",result);
        }
    }
    class MyAsyncTask extends AsyncTask<Void,String,String>{
        @Override
        protected String doInBackground(Void... voids) {
            BufferedReader reader = null;
            String result = null ;
            try{
                HttpURLConnection urlConnection = null;
                URL url = new URL(googlePlacesUrl.toString());
                urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer buffer = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    return null;
                }
                result = buffer.toString();
                Log.d("BUFFER",buffer.toString());
            }catch (Exception e){
            }
            return result;
        }
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(NearestPoliceStation.this);
            pDialog.setMessage("Please Wait...");
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String result) {
            pDialog.dismiss();
            if(result == null)
                result = " ";
            else{
                try {
                    JSONObject data = new JSONObject(result);
                    JSONArray arr = data.getJSONArray("results");
                    for (int j = 0; j < arr.length(); j++) {
                        JSONObject obj2 = arr.getJSONObject(j);
                        String name = obj2.getString("name");
                        String address = obj2.getString("vicinity");
                        String longitude = obj2.getJSONObject("geometry").getJSONObject("location").getString("lng");
                        String latitude = obj2.getJSONObject("geometry").getJSONObject("location").getString("lat");
                        String placeID = obj2.getString("place_id");
                        PoliceStationAttr tempAttr = new PoliceStationAttr();
                        tempAttr.name = name;
                        tempAttr.address = address;
                        tempAttr.latitude = latitude;
                        tempAttr.longitude = longitude;
                        mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude))).title(name));
                        tempAttr.placeID = placeID;
                        policeStation.add(tempAttr);
                        myPoliceStationAdapter = new MyPoliceStationAdapter(NearestPoliceStation.this, 0, policeStation);
                        policeStationList.setAdapter(myPoliceStationAdapter);
                        /*policeStationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                String latitude = policeStation.get(i).latitude;
                                String longitude = policeStation.get(i).longitude;
                                String name = policeStation.get(i).name;
                                mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude))).title(name));
                            }
                        });*/
                        MyAsyncTask2 myAsyncTask2 = new MyAsyncTask2();
                        myAsyncTask2.execute(latitude, longitude);
                    }
             }catch(Exception e){
             }
            Log.d("check_data",result);
        }
    }

        class MyAsyncTask2 extends AsyncTask<String,String,String> {
            @Override
            protected void onPreExecute() {
            }
            protected String doInBackground(String... distanceCoordinates) {
                googleDistanceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/distancematrix/json?");
                googleDistanceUrl.append("origins=" + mLatitude + "," + mLongitude);
                googleDistanceUrl.append("&destinations=" + distanceCoordinates[0] + "," + distanceCoordinates[1]);
                googleDistanceUrl.append("&key=" + "AIzaSyCCMmQ04uVMXxzvWUj1MiBKEqKbmwcroGc");
                Log.d("newURL",googleDistanceUrl + "");
                BufferedReader reader = null;
                String result = null;
                try{
                    HttpURLConnection urlConnection = null;
                    URL url = new URL(googleDistanceUrl.toString());
                    urlConnection = (HttpURLConnection)url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();
                    InputStream inputStream = urlConnection.getInputStream();
                    if (inputStream == null) {
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuffer buffer = new StringBuffer();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }
                    if (buffer.length() == 0) {
                        return null;
                    }
                    result = buffer.toString();
                    Log.d("BUFFER",buffer.toString());
                }catch (Exception e){
                }
                return result;
            }
            @Override
            protected void onPostExecute(String result) {
                if(result == null)
                    result = " ";
                try{
                    JSONObject obj1 = new JSONObject(result);
                    JSONArray rows = obj1.getJSONArray("rows");
                    JSONObject obj2 = rows.getJSONObject(0);
                    JSONArray elements = obj2.getJSONArray("elements");
                    JSONObject obj3 = elements.getJSONObject(0);
                    JSONObject obj4 = obj3.getJSONObject("distance");
                    String text = obj4.getString("text");
                    Log.d("text_",text);
                    curDistance = text;
                    distance.add(curDistance);
                    //if(distance.size() == 12){
                        for(int i = 0 ; i < distance.size();i++){
                            Log.d("pdistance_",distance.get(i));
                            policeStation.get(i).distance = distance.get(i)+"";
                        }
                        asyncComplete(true);
                        updateList();
                    //}
                    asyncComplete(true);
                }
                catch(Exception e){
                    Log.d("SQLError3","SQLerror3");
                }
            }
        }
}

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER
                    mMap.addMarker(new MarkerOptions().position(new LatLng(mLatitude, mLongitude)).title("You are here"));
                    //mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    mMap.getUiSettings().setCompassEnabled(true);
                    mMap.setMyLocationEnabled(true);
                    getData("police");
                }
            } else {
                Toast.makeText(this,"Permission denied",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this,"Check your internet connection",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        mMap.animateCamera(cameraUpdate);
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        editor.putString("latitude",mLatitude+"");
        editor.putString("longitude",mLongitude+"");
        editor.commit();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        String longitude = "Longitude: " + location.getLongitude();
        Log.d("Longitude", longitude);
        String latitude = "Latitude: " + location.getLatitude();
        Log.d("Latitude", latitude);
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


    class MyPoliceStationAdapter extends ArrayAdapter<PoliceStationAttr> {
        Context context;
        List<PoliceStationAttr> policeStation;
        LayoutInflater mInflater;

        public MyPoliceStationAdapter(Context context, int resource, List<PoliceStationAttr> objects) {
            super(context, resource, objects);
            this.context = context;
            this.policeStation = objects;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView == null)
            {
                convertView = mInflater.inflate(R.layout.custom_listview, parent, false);
            }
            TextView placeName = (TextView) convertView.findViewById(R.id.placeName);
            ImageView placePhoto = (ImageView) convertView.findViewById(R.id.placePhoto);
            TextView placeDistance = (TextView) convertView.findViewById(R.id.placeDistance);
            ImageButton callPlace = (ImageButton) convertView.findViewById(R.id.call_place);
            callPlace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String placeID = policeStation.get(position).placeID;
                    MyAsyncTask3 myAsyncTask3 = new MyAsyncTask3();
                    myAsyncTask3.execute(placeID);
                }
            });

            final ImageButton showMarker = (ImageButton) convertView.findViewById(R.id.marker);
            showMarker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String latitude = policeStation.get(position).latitude;
                    String longitude = policeStation.get(position).longitude;
                    String name = policeStation.get(position).name;
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(
                                    Double.parseDouble(latitude),
                                    Double.parseDouble(longitude)
                            ))
                            .title(name));
                    marker.showInfoWindow();
                    try {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(
                                        Double.parseDouble(latitude),
                                        Double.parseDouble(longitude))
                                , new Float(15.0)
                        ));
                    } catch (Exception e) {
                        //ignore
                    }
                    //mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude))).title(name));
                }
            });

            placeDistance.setText(policeStation.get(position).distance);
            //placeDistance.setText(nh.distance.get(position));
            placePhoto.setImageResource(R.drawable.police_photo);
            placeName.setText(policeStation.get(position).name);
            Log.d("name_",policeStation.get(position).name);
            return convertView;
        }
    }
}

