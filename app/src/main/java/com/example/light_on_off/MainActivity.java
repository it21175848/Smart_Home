package com.example.light_on_off;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.RecognitionListener;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    SwitchCompat switch1, switch2, switch3;
    ImageView bulb1, bulb2, bulb3;
    Button voiceCommandButton;
    BluetoothManager bluetoothManager;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    SpeechRecognizer speechRecognizer;
    TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switch1 = findViewById(R.id.switch1);
        switch2 = findViewById(R.id.switch2);
        switch3 = findViewById(R.id.switch3);
        voiceCommandButton = findViewById(R.id.voiceButton);

        bulb1 = findViewById(R.id.bulb1);
        bulb2 = findViewById(R.id.bulb2);
        bulb3 = findViewById(R.id.bulb3);

        bluetoothManager = BluetoothManager.getInstance();
        sharedPreferences = getSharedPreferences("LED_PREFS", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        restoreSwitchStates();

        switch1.setOnCheckedChangeListener((buttonView, isChecked) -> toggleLED(1, isChecked));
        switch2.setOnCheckedChangeListener((buttonView, isChecked) -> toggleLED(2, isChecked));
        switch3.setOnCheckedChangeListener((buttonView, isChecked) -> toggleLED(3, isChecked));

        // Initialize Text-to-Speech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.ENGLISH);
            }
        });

        // Set up Voice Command Button
        voiceCommandButton.setOnClickListener(v -> startVoiceRecognition());
    }

    private void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say a command...");

        try {
            startActivityForResult(intent, 1);
        } catch (Exception e) {
            Toast.makeText(this, "Voice recognition not supported!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && result.size() > 0) {
                String voiceCommand = result.get(0).toLowerCase();
                processVoiceCommand(voiceCommand);
            }
        }
    }

    private void processVoiceCommand(String command) {
        if (command.contains("on light one")) {
            toggleLED(1, true);
            speak("Turning on Light one");
        } else if (command.contains("off light 1")) {
            toggleLED(1, false);
            speak("Turning off Light 1");
        } else if (command.contains("on light 2")) {
            toggleLED(2, true);
            speak("Turning on Light 2");
        } else if (command.contains("off light 2")) {
            toggleLED(2, false);
            speak("Turning off Light 2");
        } else if (command.contains("on light 3")) {
            toggleLED(3, true);
            speak("Turning on Light 3");
        } else if (command.contains("off light 3")) {
            toggleLED(3, false);
            speak("Turning off Light 3");
        } else {
            speak("Sorry, I didn't understand the command.");
        }
    }

    private void speak(String message) {
        textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    private void restoreSwitchStates() {
        boolean led1State = sharedPreferences.getBoolean("LED1", false);
        boolean led2State = sharedPreferences.getBoolean("LED2", false);
        boolean led3State = sharedPreferences.getBoolean("LED3", false);

        switch1.setChecked(led1State);
        switch2.setChecked(led2State);
        switch3.setChecked(led3State);

        bulb1.setImageResource(led1State ? R.drawable.light_on : R.drawable.light_off);
        bulb2.setImageResource(led2State ? R.drawable.light_on : R.drawable.light_off);
        bulb3.setImageResource(led3State ? R.drawable.light_on : R.drawable.light_off);
    }


    private void toggleLED(int ledNumber, boolean isChecked) {
        String ledKey = "LED" + ledNumber;
        editor.putBoolean(ledKey, isChecked).apply();

        String command = isChecked ? String.valueOf(ledNumber + 4) : String.valueOf(ledNumber);
        bluetoothManager.sendData(command);

        ImageView bulb = ledNumber == 1 ? bulb1 : (ledNumber == 2 ? bulb2 : bulb3);
        bulb.setImageResource(isChecked ? R.drawable.light_on : R.drawable.light_off);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bluetoothManager.isConnected()) {
            resendLEDStates();
        }
    }

    private void resendLEDStates() {
        boolean led1State = sharedPreferences.getBoolean("LED1", false);
        boolean led2State = sharedPreferences.getBoolean("LED2", false);
        boolean led3State = sharedPreferences.getBoolean("LED3", false);

        if (led1State) bluetoothManager.sendData("5"); else bluetoothManager.sendData("1");
        if (led2State) bluetoothManager.sendData("6"); else bluetoothManager.sendData("2");
        if (led3State) bluetoothManager.sendData("7"); else bluetoothManager.sendData("3");
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
