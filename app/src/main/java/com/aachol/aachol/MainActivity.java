package com.aachol;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;

import com.aachol.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    FusedLocationProviderClient fusedLocationClient;
    String myLocation = "", numberCall;
    SmsManager manager = SmsManager.getDefault();
    private ImageButton logoutButton;
    private static final int REQUIRED_VOLUME_UP_COUNT = 2;
    private static final int REQUIRED_VOLUME_DOWN_COUNT = 4;
    private int volumeUpCount = 0;
    private int volumeDownCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the logout button
        logoutButton = findViewById(R.id.logoutButton);

        // Set click listener for the logout button
        logoutButton.setOnClickListener(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        findViewById(R.id.panicBtn).setOnClickListener(this);
        findViewById(R.id.mistake).setOnClickListener(this);
        findViewById(R.id.dbbutton).setOnClickListener(this);
        findViewById(R.id.fourth).setOnClickListener(this);
        findViewById(R.id.first).setOnClickListener(this);
        findViewById(R.id.second).setOnClickListener(this);
        findViewById(R.id.fifth).setOnClickListener(this);

        // Register the volume button receiver
        IntentFilter filter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        registerReceiver(volumeButtonReceiver, filter);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.logoutButton) {
            // Clear the stored login information or perform any necessary logout actions

            // For example, if you stored a "Remember Me" preference using SharedPreferences
            SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove("rememberMe");
            editor.apply();

            // Start the LoginActivity and finish the current MainActivity
            Intent intent = new Intent(this, com.aachol.activities.LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (view.getId() == R.id.fourth) {
            startActivity(new Intent(MainActivity.this, LawsActivity.class));
            MainActivity.this.finish();
        } else if (view.getId() == R.id.first) {
            startActivity(new Intent(MainActivity.this, ContactActivity.class));
            MainActivity.this.finish();
        } else if (view.getId() == R.id.fifth) {
            startActivity(new Intent(MainActivity.this, SelfDefenseActivity.class));
        } else if (view.getId() == R.id.second) {
            startActivity(new Intent(MainActivity.this, SmsActivity.class));
            MainActivity.this.finish();
        } else if (view.getId() == R.id.dbbutton) {
            startActivity(new Intent(MainActivity.this, com.aachol.activities.UsersListActivity.class));
            MainActivity.this.finish();
        } else if (view.getId() == R.id.panicBtn) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            location.getAltitude();
                            location.getLongitude();
                            myLocation = "http://maps.google.com/maps?q=loc:" + location.getLatitude() + "," + location.getLongitude();
                        } else {
                            myLocation = "Unable to Find Location :(";
                        }
                        sendMsg();
                    });

            SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
            numberCall = sharedPreferences.getString("firstNumber", "None");
            if (!numberCall.equalsIgnoreCase("None")) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + numberCall));
                startActivity(intent);
            }
        } else if (view.getId() == R.id.mistake) {
            mistake();
        }
    }

    void sendMsg() {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        Set<String> oldNumbers = sharedPreferences.getStringSet("enumbers", new HashSet<>());
        if (!oldNumbers.isEmpty()) {
            for (String ENUM : oldNumbers) {
                manager.sendTextMessage(ENUM, null, "Im in Trouble!\nSending My Location :\n" + myLocation, null, null);
            }
        }
    }

    void mistake() {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        Set<String> oldNumbers = sharedPreferences.getStringSet("enumbers", new HashSet<>());
        if (!oldNumbers.isEmpty()) {
            for (String ENUM : oldNumbers) {
                manager.sendTextMessage(ENUM, null, "Previous message was sent by mistake", null, null);
            }
        }
    }

    public BroadcastReceiver volumeButtonReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String intentAction = intent.getAction();
            if (intentAction != null) {
                mistake();
                if (intentAction.equals(Intent.ACTION_MEDIA_BUTTON)) {
                    KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                    if (event != null && event.getAction() == KeyEvent.ACTION_UP) {
                        int keyCode = event.getKeyCode();
                        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                            volumeUpCount++;
                        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                            volumeDownCount++;
                        }

                        if (volumeUpCount == REQUIRED_VOLUME_UP_COUNT && volumeDownCount == REQUIRED_VOLUME_DOWN_COUNT) {
                            mistake();
                            volumeUpCount = 0;
                            volumeDownCount = 0;
                        }
                    }
                }
            }
        }
    };
}
