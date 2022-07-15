package com.example.fotaandroid;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {


    EditText userETLogin, passETLogin;
    AppCompatButton loginBtn;
    AppCompatButton NotAMemberBtn;

    @Override
    protected void onStart() {
        super.onStart();
        //FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //checking for user existence : saving the current user
        if (firebaseUser != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userETLogin = findViewById(R.id.editTextUserName);
        passETLogin = findViewById(R.id.editTextPassword);
        loginBtn = findViewById(R.id.loginBtn);
        NotAMemberBtn = findViewById(R.id.NotAMemberBtn);


        //login Button
        loginBtn.setOnClickListener(v -> {
            String email_text = userETLogin.getText().toString();
            String pass_text = passETLogin.getText().toString();

            //Checking if it is empty
            if (TextUtils.isEmpty(email_text) || TextUtils.isEmpty(pass_text)) {
                Toast.makeText(this, "please Fill all required fields", Toast.LENGTH_SHORT).show();
            } else {
                LoginNow(email_text, pass_text);
            }

        });

        //NotAMemberBtn Button
        NotAMemberBtn.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

    }

    private void LoginNow(String email, String password) {
        //FireBase Auth
        FirebaseAuth auth;
        auth = FirebaseAuth.getInstance();

        if (isValidEmail(email)) {
            Log.d("TAG", "validMail");
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("TAG", "signInWithCredential:enterOnComplete");
                            if (task.isSuccessful()) {
                                Log.d("TAG", "signInWithCredential:success");
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                Log.d("TAG", "signInWithCredential:failure", task.getException());
                                Log.w("TAG", "signInWithEmail:failed", task.getException());

                                Toast.makeText(LoginActivity.this, "Login has been failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "Enter correct email format please!", Toast.LENGTH_SHORT).show();
        }


    }

//    @Override
//    public void onBackPressed() {
//        moveTaskToBack(true);
//    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

}