package com.example.googlemaps;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity {
    private static final  String TAG = "MainActivity";
    private static final int Error = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (isservisiceok()){
            init();
        }
    }
    private void init(){
        Button Btnmap = (Button) findViewById(R.id.Btnmap);
        Btnmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,MapActivity.class);
                startActivity(intent);
            }
        });
    }
    public Boolean isservisiceok() {
        Log.d(TAG, "isservisiceok: cheking google services version");
        int availabale = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
          if(availabale == ConnectionResult.SUCCESS){
              Log.d(TAG, "isservisiceok: Google play services is working");
              return true;
          }else if ( GoogleApiAvailability.getInstance().isUserResolvableError(availabale)){
              Log.d(TAG, "isservisiceok: an error is occured but we can fix it");
              Dialog dialog =  GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this,availabale,Error);
              dialog.show();
          }else{
              Toast.makeText(this,"you cant creat map requwest",Toast.LENGTH_SHORT).show();
          }
          return false;
    }

}