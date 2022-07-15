package com.example.fotaandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class NotificationActivity extends AppCompatActivity {

    TextView textView6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        textView6 = findViewById(R.id.textView6);

        String message = getIntent().getStringExtra("lastUpload");
        textView6.setText(message);
    }
}