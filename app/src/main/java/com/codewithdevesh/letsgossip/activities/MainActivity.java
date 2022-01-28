package com.codewithdevesh.letsgossip.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.codewithdevesh.letsgossip.R;
import com.codewithdevesh.letsgossip.utilities.Utils;
import com.codewithdevesh.letsgossip.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Utils.setStatusBarColor(this,R.color.light_background);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    finish();
                    overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
                }
            },900);
        }else {
            showSplashScreen();
        }
    }

    private void showSplashScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
            }
        }, 900);
    }
}