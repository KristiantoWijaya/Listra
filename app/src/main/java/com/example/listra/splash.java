package com.example.listra;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
       //give a delay for the splash screen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                //if user is currently logged in, they will be redirected to main
                if (user != null){
                    Intent i = new Intent(splash.this, MainActivity.class);
                    startActivity(i);
                    finish();
                } else{
                    //if the user isnt currently logged in, they will be redirected to login page
                    Intent i = new Intent(splash.this, LoginPage.class);
                    startActivity(i);
                    finish();
                }
            }
        },3000);

    }
}