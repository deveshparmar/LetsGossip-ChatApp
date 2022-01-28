package com.codewithdevesh.letsgossip.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;

import com.codewithdevesh.letsgossip.R;
import com.codewithdevesh.letsgossip.utilities.Utils;
import com.codewithdevesh.letsgossip.databinding.ActivityWelcomeBinding;

public class WelcomeActivity extends AppCompatActivity {
    ActivityWelcomeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this, R.layout.activity_welcome);
        Utils.setStatusBarColor(this,R.color.light_background);
        listeners();
    }

    private void listeners() {

        binding.bttnAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
            }
        });

        binding.policy.setMovementMethod(LinkMovementMethod.getInstance());
        binding.policy.setLinkTextColor(Color.BLUE);
    }
}