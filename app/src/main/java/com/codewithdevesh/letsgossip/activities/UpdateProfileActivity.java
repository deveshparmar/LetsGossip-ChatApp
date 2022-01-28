package com.codewithdevesh.letsgossip.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codewithdevesh.letsgossip.R;
import com.codewithdevesh.letsgossip.databinding.ActivityUpdateProfileBinding;
import com.codewithdevesh.letsgossip.databinding.ActivityUpdateProfileBinding;
import com.codewithdevesh.letsgossip.model.UserModel;
import com.codewithdevesh.letsgossip.utilities.SessionManagement;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UpdateProfileActivity extends AppCompatActivity {
    private ActivityUpdateProfileBinding binding;
    private String photoUri;
    private Uri picUri;
    private StorageReference storageReference;
    private FirebaseStorage storage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        SessionManagement.init(UpdateProfileActivity.this);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_update_profile);
        binding.etBio.setText(SessionManagement.getUserBio());
        binding.etName.setText(SessionManagement.getUserName());
        photoUri = SessionManagement.getUserPic();
        picUri = Uri.parse(photoUri);
        Glide.with(UpdateProfileActivity.this).load(SessionManagement.getUserPic()).into(binding.profImg);
        binding.fabSelectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog();
            }
        });
        binding.bttnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = binding.etName.getText().toString();
                String bio = binding.etBio.getText().toString();
                String phoneNo = SessionManagement.getUserPhoneNo();
                if(name.isEmpty()){
                    binding.etName.setError("Name required");
                }else if(bio.isEmpty()){
                    binding.etBio.setError("Bio required");
                }
                 else {
                    binding.etName.setError(null);
                    binding.etBio.setError(null);
                    SessionManagement.saveUserName(name);
                    SessionManagement.saveUserBio(bio);
                    SessionManagement.saveUserPhoneNo(phoneNo);
                    if(photoUri.equals(SessionManagement.getUserPic())){
                        saveUserData();
                    }else {
                        uploadImage();
                    }

                }
//                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
//                reference.child(SessionManagement.getUserPhoneNo()).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        UserModel model = new UserModel(name,bio,phoneNo,photoUri);
//                        reference.setValue(model);
//                        SessionManagement.saveUserName(name);
//                        SessionManagement.saveUserBio(bio);
//                        SessionManagement.saveUserPhoneNo(phoneNo);
//                        binding.etName.setText(SessionManagement.getUserName());
//                        binding.etBio.setText(SessionManagement.getUserBio());
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Toast.makeText(getApplicationContext(), "Failed to update details try again later!", Toast.LENGTH_SHORT).show();
//                    }
//                });
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
    private void saveUserData(){
        String name = binding.etName.getText().toString();
        String bio = binding.etBio.getText().toString();
        String phoneNo = SessionManagement.getUserPhoneNo();
        String photoUri = SessionManagement.getUserPic();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User").child(phoneNo);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel model = new UserModel(name,bio,phoneNo,photoUri);
                reference.setValue(model);
                Toast.makeText(getApplicationContext(), "Profile updated Successfully!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed to update profile!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(UpdateProfileActivity.this);
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
}