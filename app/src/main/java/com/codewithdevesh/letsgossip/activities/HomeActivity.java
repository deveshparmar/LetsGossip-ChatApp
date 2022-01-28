 package com.codewithdevesh.letsgossip.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.codewithdevesh.letsgossip.R;
import com.codewithdevesh.letsgossip.fragment.CallFragment;
import com.codewithdevesh.letsgossip.fragment.ChatFragment;
import com.codewithdevesh.letsgossip.fragment.ProfileFragment;
import com.codewithdevesh.letsgossip.fragment.StatusFragment;
import com.codewithdevesh.letsgossip.utilities.SessionManagement;
import com.codewithdevesh.letsgossip.utilities.Utils;
import com.codewithdevesh.letsgossip.databinding.ActivityHomeBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

 public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
    final ChatFragment chatFragment = new ChatFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        SessionManagement.init(HomeActivity.this);
        Utils.setStatusBarColor(this, R.color.light_background);
//        getImage();


        getSupportFragmentManager().beginTransaction().replace(R.id.parentContainer, chatFragment).commit();
        binding.navBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            final StatusFragment statusFragment = new StatusFragment();
            final CallFragment callFragment = new CallFragment();
            final ProfileFragment profileFragment = new ProfileFragment();
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.m_chats:
                        getSupportFragmentManager().beginTransaction().replace(R.id.parentContainer,chatFragment).commit();
                        return true;
                    case R.id.m_status:
                        getSupportFragmentManager().beginTransaction().replace(R.id.parentContainer,statusFragment).commit();
                        return true;
                    case R.id.m_calls:
                        getSupportFragmentManager().beginTransaction().replace(R.id.parentContainer,callFragment).commit();
                        return true;
                    case R.id.m_profile:
                        getSupportFragmentManager().beginTransaction().replace(R.id.parentContainer,profileFragment).commit();
                        return true;
                }
                return false;
            }
        });

        Log.e("TAG","phone" + SessionManagement.getUserPhoneNo());
        Log.e("TAG","name" + SessionManagement.getUserName());
        Log.e("TAG","bio" + SessionManagement.getUserBio());
        Log.e("TAG","pic" + SessionManagement.getUserPic());

    }

     @Override
     public void onBackPressed() {
         super.onBackPressed();
         finishAffinity();
     }

     @Override
     protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
         super.onActivityResult(requestCode, resultCode, data);

     }
 }