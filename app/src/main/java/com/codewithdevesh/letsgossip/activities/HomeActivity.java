 package com.codewithdevesh.letsgossip.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import com.codewithdevesh.letsgossip.R;
import com.codewithdevesh.letsgossip.fragment.CallFragment;
import com.codewithdevesh.letsgossip.fragment.ChatFragment;
import com.codewithdevesh.letsgossip.fragment.ProfileFragment;
import com.codewithdevesh.letsgossip.fragment.StatusFragment;
import com.codewithdevesh.letsgossip.utilities.SessionManagement;
import com.codewithdevesh.letsgossip.utilities.Utils;
import com.codewithdevesh.letsgossip.databinding.ActivityHomeBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


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
        Log.e("TAG","id" + SessionManagement.getUserId());

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
     private void getConnectionStatus(String status){
         DatabaseReference reference = FirebaseDatabase.getInstance().getReference("UserStatus").child(SessionManagement.getUserId());
         reference.child("status").setValue(status);

     }

     @Override
     protected void onDestroy() {
         super.onDestroy();
         getConnectionStatus("Offline");
     }

     @Override
     protected void onPause() {
         super.onPause();
         getConnectionStatus("Offline");
     }

     @Override
     protected void onResume() {
         super.onResume();
         getConnectionStatus("Online");
         if(!Utils.isPermissionGranted(this)){
             new AlertDialog.Builder(this).setTitle("App Permissions")
                     .setMessage("Due to security purpose ,this app requires permission")
                     .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialogInterface, int i) {
                                takePermission();
                         }
                     }).setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialogInterface, int i) {

                         }
                     }).show();
         }
     }

     private void takePermission() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R) {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.addCategory("android.intent.category.DEFAULT");
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, 100);
                }catch (Exception e){
                    e.printStackTrace();
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    startActivityForResult(intent,100);
                }
        }else{
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE
            },100);
        }
     }

     @Override
     public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
         super.onRequestPermissionsResult(requestCode, permissions, grantResults);
         if(grantResults.length>0){
             if(requestCode==100){
                 boolean ext = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                 if(!ext){
                     takePermission();
                 }
             }
         }
     }
 }