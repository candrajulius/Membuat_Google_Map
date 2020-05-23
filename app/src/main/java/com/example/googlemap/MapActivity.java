package com.example.googlemap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String Fine_Location_Access = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String Course_Location = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String TAG = "Consele";
    private Boolean mlocationPermissionGranted = false;
    private static final int RequestCode = 1234;

    // widgets
    private EditText editText;
    private ImageView imageView;

    // vars
    private GoogleMap map;
    private FusedLocationProviderClient server;
    private final float DEFAULT_ZOMM = 15f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        editText = findViewById(R.id.input_search);
        imageView = findViewById(R.id.gps);

        getLocationPermission();

    }

    // Mencari lokasi akurat dari google map
    private void getDeviceLocation(){
        Log.d(TAG, "Mendapatkan Lokasi: Lokasi akurat didapatkan");
        server = LocationServices.getFusedLocationProviderClient(this);

        try {
          if (mlocationPermissionGranted){
              final Task location = server.getLastLocation();
              location.addOnCompleteListener(new OnCompleteListener() {
                  @Override
                  public void onComplete(@NonNull Task task) {
                      if (task.isSuccessful()){
                          Log.d(TAG, "Berhasil : Berhasil mencari lokasi");
                          Location currentlokasi = (Location) task.getResult();
                          if (currentlokasi != null) {
                              moreConscured(new LatLng(currentlokasi.getLatitude(),currentlokasi.getLongitude()),DEFAULT_ZOMM,"My Lokasi");
                          }
                      }else{
                          Log.d(TAG,"Enable to get current lokasi");
                          Toast.makeText(MapActivity.this, "Gagal mengambil lokasi perangkat", Toast.LENGTH_SHORT).show();
                      }
                  }
              });
          }
        }catch (SecurityException e){
            Log.d(TAG,"Lokasi perangkat: Excpetion Security " + e.getMessage());
        }
    }

    // Membuat camera zoom pada google map
    private void moreConscured (LatLng latLng, float satu, String tittle){
        Log.d(TAG,"Moreconscuerd : moving the camera to let: " + latLng.latitude + " , lng: " + latLng.latitude);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,satu));

        if  (!tittle.equals("My Location")){
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(tittle);
            map.addMarker(markerOptions);
        }
        hidesoftKeyboard();
    }


    // Menginisasi google map untuk di tampilkan
    private void iniMap() {
        Log.d(TAG,"Map Ternisisasi: Inisiasi Map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(MapActivity.this);
        }
    }

    // mengizinkan lokasi untuk di cari dari google map
    private void getLocationPermission(){
        Log.d(TAG,"Lokasi diizinkan: : Lokasi didapatkan");
        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Fine_Location_Access) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    Course_Location) == PackageManager.PERMISSION_GRANTED) {
                mlocationPermissionGranted = true;
                iniMap();
            } else {
                ActivityCompat.requestPermissions(this, permission, RequestCode);
            }
        }else{
            ActivityCompat.requestPermissions(this,permission,RequestCode);
        }

    }

    // Membuat request permission dari googleMap
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG,"Lokasi permission berkerja: Terpanggil");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mlocationPermissionGranted = false;
        if (requestCode == RequestCode) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        mlocationPermissionGranted = false;
                        Log.d(TAG, "onRequestPermissionResult: Permission Tag");
                        return;
                    }
                }
                Log.d(TAG, "Permission Granted: permission granted");
                mlocationPermissionGranted = true;
                // Inisiasi lokasi map anda
                iniMap();

            }
        }
        }

        // Membuka device map untuk di jalankan di aplikasi
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this,"Map dimulai",Toast.LENGTH_SHORT).show();
        Log.d(TAG,"Map dimulai: Dimulai");
        map = googleMap;

        if (mlocationPermissionGranted){
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,Manifest.permission.ACCESS_COARSE_LOCATION
            )!= PackageManager.PERMISSION_GRANTED ){
                return;
            }
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
            init();
        }
    }

    // Membuat aksi pada teks view dan mencari lokasi perangkat
    private void init(){
        Log.d(TAG,"Inisiasi : Inisasi");
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if  (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_NONE
                            || event.getAction() == KeyEvent.ACTION_DOWN
                            || event.getAction() == KeyEvent.KEYCODE_ENTER)
                {
                        // Membuat method search
                    geolocate();

                }
                return false;
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"onClick : click gps icon");
                getDeviceLocation();
            }
        });
        hidesoftKeyboard();
    }

    // Mencari alamat dari perangkat yang ada di google map
    private void geolocate(){
        Log.d(TAG,"excute: Eksekusi");
        String search = editText.getText().toString();

        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(search,1);
        }catch (IOException e ){
            Log.d(TAG,"Exception: IOException" + e.getMessage());
        }
        if (list.size() > 0){
            Address address = list.get(0);
            Log.d(TAG, "tidak ditemukan : " +  address.toString());
           // Toast.makeText(MapActivity.this, address.toString(), Toast.LENGTH_SHORT).show();
            moreConscured(new LatLng(address.getLatitude(), address.getLongitude()),DEFAULT_ZOMM,address.getAddressLine(0));
        }
    }

    private void hidesoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
