package com.example.googlemap;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity  {
    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private Button map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (isServiceOK()) {
            map = findViewById(R.id.map);
            map.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this,MapActivity.class);
                    startActivity(intent);
                }
            });
        }

    }
    public boolean isServiceOK(){
        Log.d(TAG, "layanan bagus, coba check versi google map anda");
        int availlable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if (availlable == ConnectionResult.SUCCESS){
            Log.d(TAG,"layanan berhasil: Google play service berkerja");
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(availlable)){
            Log.d(TAG, "Layanan gagal: Google play tidak berjalan ");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this,availlable,ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this,"kamu harus membuat request",Toast.LENGTH_SHORT).show();
        }
        return false;
    }

}
