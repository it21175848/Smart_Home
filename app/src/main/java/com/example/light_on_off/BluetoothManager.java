package com.example.light_on_off;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class

BluetoothManager {
    private static BluetoothManager instance;
    private final BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;
    private static final String DEVICE_ADDRESS = "98:D3:36:81:15:A8"; // Change to your HC-05/HC-06 MAC Address
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothManager() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public static BluetoothManager getInstance() {
        if (instance == null) {
            instance = new BluetoothManager();
        }
        return instance;
    }

    @SuppressLint("MissingPermission")
    public boolean connect() {
        if (bluetoothSocket != null && bluetoothSocket.isConnected()) {
            Log.d("BluetoothManager", "Already connected");
            return true; // Prevent reconnecting if already connected
        }

        try {
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(DEVICE_ADDRESS);
            bluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID);

            bluetoothAdapter.cancelDiscovery(); // Stops scanning before connecting
            bluetoothSocket.connect();
            outputStream = bluetoothSocket.getOutputStream();

            Log.d("BluetoothManager", "Connected to Bluetooth device");
            return true;
        } catch (IOException e) {
            Log.e("BluetoothManager", "Connection failed", e);
            disconnect();  // Ensures cleanup on failure
            return false;
        }
    }

    public void sendData(String data) {
        if (outputStream != null) {
            try {
                outputStream.write(data.getBytes());
                Log.d("BluetoothManager", "Sent: " + data);
            } catch (IOException e) {
                Log.e("BluetoothManager", "Failed to send data", e);
            }
        }
    }

    public boolean isConnected() {
        return bluetoothSocket != null && bluetoothSocket.isConnected();
    }

    public void disconnect() {
        try {
            if (bluetoothSocket != null) {
                bluetoothSocket.close();
                bluetoothSocket = null;
            }
        } catch (IOException e) {
            Log.e("BluetoothManager", "Error closing socket", e);
        }
    }
}
