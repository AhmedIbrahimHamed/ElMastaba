package com.example.android.elmastaba;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class LaunchActivity extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 800;
    private FirebaseAuth mFirebaseAuth; // firebase authentication.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        mFirebaseAuth = FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable() {
            // This method will be executed once the timer is over.
            @Override
            public void run() {
                Intent intent;
                //if the user is signed in he will go to the Main activity otherwise he will go to SignIn Activity.
                if (mFirebaseAuth.getCurrentUser() != null) {
                    intent = new Intent(LaunchActivity.this, MainActivity.class);
                } else {
                    intent = new Intent(LaunchActivity.this, SignInActivity.class);
                }
                startActivity(intent);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}