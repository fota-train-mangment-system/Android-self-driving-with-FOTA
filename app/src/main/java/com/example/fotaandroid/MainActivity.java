package com.example.fotaandroid;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.example.fotaandroid.Model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;
    DatabaseReference ref, lastUpdate, userName;

    Button uptodatebtn;
    TextView editTextId, editTextUserName, editTextEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uptodatebtn = findViewById(R.id.uptodatebtn);

        editTextId = findViewById(R.id.editTextId);
        editTextUserName = findViewById(R.id.editTextUserName);
        editTextEmail = findViewById(R.id.editTextEmail);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;

        //String username = firebaseUser.getDisplayName();
        String id = firebaseUser.getUid();
        editTextId.setText(id);

        userName = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("username");
        userName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = Objects.requireNonNull(snapshot.getValue()).toString();
                editTextUserName.setText(username);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        String email = firebaseUser.getEmail();
        editTextEmail.setText(email);


        ref = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Button to get the last updated code
        lastUpdate = FirebaseDatabase.getInstance().getReference("FTP").child("FromFTPServer").child("latest_upload");
        uptodatebtn.setOnClickListener(v -> {
            lastUpdate.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String lastUpload = Objects.requireNonNull(snapshot.getValue()).toString();
                        Toast.makeText(MainActivity.this, "The last version is: " + lastUpload + "\nYou are UP-TO-Date!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Can not retrieve the last version", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, "Can not retrieve the last version", Toast.LENGTH_SHORT).show();
                }
            });
        });


        //to get notified if the code is updated
        lastUpdate.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String lastUpload = Objects.requireNonNull(snapshot.getValue()).toString();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel("myCh", "My Channel", NotificationManager.IMPORTANCE_DEFAULT);
                        NotificationManager notificationManager = getSystemService(NotificationManager.class);
                        notificationManager.createNotificationChannel(channel);
                    }

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "myCh")
                            .setSmallIcon(R.drawable.ic_notification_alert)
                            .setContentTitle("You've got an update for you")
                            .setAutoCancel(true);


                    Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("lastUpload", "the last update for your Awesome car is here\n" +
                            "It's " + lastUpload + "\n\n\n Congrats ❤ \uD83D\uDD25");

                    PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setContentIntent(pendingIntent);
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(1, builder.build());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Can not retrieve the last version", Toast.LENGTH_SHORT).show();
            }
        });

    }

    //Adding logout and About functionality
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                return true;
            case R.id.AboutUs:
                startActivity(new Intent(MainActivity.this, AboutUs.class));
                return true;
        }
        return false;
    }

}