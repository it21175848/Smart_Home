package com.example.light_on_off;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class HomeActivity extends AppCompatActivity {

    Button goToLightControl, bluetoothConnectButton, logoutButton; // Added logout button
    ImageView bluetoothIcon;
    BluetoothManager bluetoothManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        goToLightControl = findViewById(R.id.goToLightControl1);
        bluetoothConnectButton = findViewById(R.id.bluetoothConnectButton);
        bluetoothIcon = findViewById(R.id.bluetoothIcon);
        logoutButton = findViewById(R.id.logoutButton); // Initialize Logout button

        bluetoothManager = BluetoothManager.getInstance();

        goToLightControl.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
        });

        bluetoothConnectButton.setOnClickListener(v -> {
            if (checkBluetoothPermissions()) {
                if (bluetoothManager.connect()) {
                    Toast.makeText(this, "Bluetooth Connected!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show();
                }
            } else {
                requestBluetoothPermissions();
            }
        });

        bluetoothIcon.setOnClickListener(v -> {
            if (checkBluetoothPermissions()) {
                if (bluetoothManager.connect()) {
                    Toast.makeText(this, "Bluetooth Connected!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show();
                }
            } else {
                requestBluetoothPermissions();
            }
        });

        // Set OnClickListener for the Logout button
        logoutButton.setOnClickListener(v -> {
            finish(); // This will close the current activity
        });
    }

    private boolean checkBluetoothPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED;
        }
        return true; // No permissions needed below Android 12
    }

    private void requestBluetoothPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN},
                    1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Bluetooth Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Bluetooth Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
