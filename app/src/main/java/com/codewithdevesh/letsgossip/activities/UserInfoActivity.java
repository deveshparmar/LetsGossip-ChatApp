package com.codewithdevesh.letsgossip.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.codewithdevesh.letsgossip.R;
import com.codewithdevesh.letsgossip.utilities.SessionManagement;
import com.codewithdevesh.letsgossip.model.UserModel;
import com.codewithdevesh.letsgossip.databinding.ActivityUserInfoBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.File;
import java.io.IOException;

public class UserInfoActivity extends AppCompatActivity {
    private ActivityUserInfoBinding binding;
    private DatabaseReference reference;
    private String photoUri;
    private Uri picUri;
    private StorageReference storageReference;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_info);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        SessionManagement.init(UserInfoActivity.this);
        listeners();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100 && resultCode==RESULT_OK && data!=null){
            Uri uri = data.getData();
                    photoUri = uri.toString();
                    picUri = uri;
            Glide.with(this).load(uri).into(binding.profImg);

        }
    }

    private void listeners() {
        binding.bttnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = binding.etName.getText().toString();
                String bio = binding.etBio.getText().toString();

                if(name.isEmpty()){
                    binding.etName.setError("Name required");
                }else if(bio.isEmpty()){
                    binding.etBio.setError("Bio required");
                }else if(photoUri.isEmpty()){
                    binding.profImg.setImageResource(R.drawable.user);
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Default");
                    reference.child("pic").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            photoUri = snapshot.getValue(String.class);
                            picUri = Uri.parse(photoUri);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else{
                    binding.etName.setError(null);
                    binding.etBio.setError(null);

                    SessionManagement.saveUserBio(bio);
                    SessionManagement.saveUserName(name);
                    uploadImage();

//                    saveUserData();


                }
            }
        });

        binding.fabSelectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog();
            }
        });
    }

    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(UserInfoActivity.this);
        bottomSheetDialog.setContentView(R.layout.dialog_bottom);
        bottomSheetDialog.show();

        LinearLayout camera = bottomSheetDialog.findViewById(R.id.ll_camera);
        LinearLayout gallery = bottomSheetDialog.findViewById(R.id.ll_gallery);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI,"image/*");
                startActivityForResult(intent,100);
            }
        });

    }

    private void saveUserData(){
        String name = SessionManagement.getUserName();
        String bio = SessionManagement.getUserBio();
        String phoneNo = SessionManagement.getUserPhoneNo();
        String photoUri = SessionManagement.getUserPic();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User").child(phoneNo);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserModel model = new UserModel(name,bio,phoneNo,photoUri);
                    reference.setValue(model);
                    Toast.makeText(getApplicationContext(), "Profile details saved!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    finish();
                    overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), "Failed in saving details!", Toast.LENGTH_SHORT).show();
                }
            });
    }
    private void uploadImage(){
        if(picUri!=null){
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
           StorageReference ref = storageReference.child("profileImages/"+SessionManagement.getUserPhoneNo().toString()+".jpg");
           ref.putFile(picUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
               @Override
               public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                   // image uploaded
                   ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                       @Override
                       public void onSuccess(Uri uri) {
                            photoUri = uri.toString();
                            SessionManagement.saveUserPic(photoUri);
                           progressDialog.dismiss();
                           saveUserData();

                       }
                   });
               }
           }).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {
                   progressDialog.dismiss();
                   Toast.makeText(getApplicationContext(), "Fail to upload image", Toast.LENGTH_SHORT).show();
               }
           }).addOnProgressListener(
                   new OnProgressListener<UploadTask.TaskSnapshot>() {

                       // Progress Listener for loading
                       // percentage on the dialog box
                       @Override
                       public void onProgress(
                               @NonNull UploadTask.TaskSnapshot taskSnapshot)
                       {
                           double progress
                                   = (100.0
                                   * taskSnapshot.getBytesTransferred()
                                   / taskSnapshot.getTotalByteCount());
                           progressDialog.setMessage(
                                   "Uploaded "
                                           + (int)progress + "%");
                       }
                   });
        }
    }

}