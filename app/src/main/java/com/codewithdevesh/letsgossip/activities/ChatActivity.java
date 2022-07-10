package com.codewithdevesh.letsgossip.activities;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codewithdevesh.letsgossip.R;
import com.codewithdevesh.letsgossip.adapter.ChatAdapter;
import com.codewithdevesh.letsgossip.databinding.ActivityChatBinding;
import com.codewithdevesh.letsgossip.model.ChatModel;
import com.codewithdevesh.letsgossip.model.RecentChatModel;
import com.codewithdevesh.letsgossip.security.AES;
import com.codewithdevesh.letsgossip.utilities.SessionManagement;
import com.codewithdevesh.letsgossip.utilities.Utils;
import com.devlomi.record_view.OnRecordListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private String receiverId, receiverName, receiverPic;
    private ActivityChatBinding binding;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private ChatAdapter adapter;
    private List<ChatModel> list;
    private MediaRecorder mediaRecorder;
    private String typing,audioPath;
    private String userCurrentStatus;
    private Handler handler = new Handler();
    String[] cameraPermission;
    String[] storagePermission;
    Uri image_uri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setStatusBarColor(this, R.color.light_background);
        SessionManagement.init(ChatActivity.this);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
        cameraPermission = new String[]{Manifest.permission.CAMERA, WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{WRITE_EXTERNAL_STORAGE};
        Intent i = getIntent();
        receiverId = i.getStringExtra("userId");
        receiverName = i.getStringExtra("userName");
        receiverPic = i.getStringExtra("userProfilePic");
        getDetails();
        listeners();
        list = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager.setStackFromEnd(true);
        binding.rvChat.setLayoutManager(layoutManager);
        readChats();
        binding.attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog();
            }
        });
    }

    private void setUpRecording() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"LetsGossip/Media/Recording");
        if(!file.exists()){
            file.mkdirs();
        }
        audioPath = file.getAbsolutePath()+File.separator+System.currentTimeMillis()+".3gp";
        mediaRecorder.setOutputFile(audioPath);
    }

    private void readChats() {
        try {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("LastMessage");
            reference.child("Chats").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    list.clear();
                    for (DataSnapshot ds1 : snapshot.getChildren()) {
                        ChatModel chatModel = ds1.getValue(ChatModel.class);
                        if (chatModel != null && chatModel.getSender().equals(SessionManagement.getUserId()) && chatModel.getReceiver().equals(receiverId) || chatModel.getReceiver().equals(SessionManagement.getUserId()) && chatModel.getSender().equals(receiverId)) {
                            list.add(chatModel);
                        }
                    }
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    } else {
                        adapter = new ChatAdapter(list, ChatActivity.this);
                        binding.rvChat.setAdapter(adapter);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void listeners() {
        binding.fabSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(binding.etMessage.getText().toString())) {
                    sendMessage(binding.etMessage.getText().toString(),"TEXT","");
                }
                binding.etMessage.setText("");
            }
        });

        handler.post(new Runnable() {
            @Override
            public void run() {
                getTypingStatus();
                getUserStatus();
            }
        });
        binding.etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(binding.etMessage.getText().toString()) || binding.etMessage.getText().toString().trim().length() == 0) {
                    binding.fabSend.setEnabled(false);
                    binding.fabSend.setVisibility(View.GONE);
                    binding.btRec.setVisibility(View.VISIBLE);
                    setTypingStatus("not");
                    typing="not";
                } else {
                    binding.fabSend.setEnabled(true);
                    binding.fabSend.setVisibility(View.VISIBLE);
                    binding.btRec.setVisibility(View.GONE);
                    setTypingStatus("typing...");
                    typing="typing...";
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                setUpRecording();
                try{
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                binding.cvMsg.setVisibility(View.GONE);
                binding.recordView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancel() {
                mediaRecorder.reset();
                mediaRecorder.release();
                File file = new File(audioPath);
                if(file.exists()){
                    file.delete();
                }
                binding.recordView.setVisibility(View.GONE);
                binding.cvMsg.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish(long recordTime, boolean limitReached) {
                try {
                    mediaRecorder.stop();
                    mediaRecorder.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                binding.recordView.setVisibility(View.GONE);
                binding.cvMsg.setVisibility(View.VISIBLE);
                    sendRecordingMessage(audioPath);
            }

            @Override
            public void onLessThanSecond() {
                mediaRecorder.reset();
                mediaRecorder.release();
                File file = new File(audioPath);
                if(file.exists()){
                    file.delete();
                }
                binding.recordView.setVisibility(View.GONE);
                binding.cvMsg.setVisibility(View.VISIBLE);
                Toast.makeText(ChatActivity.this, "Hold Button to record", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btRec.setRecordView(binding.recordView);
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO)== PackageManager.PERMISSION_GRANTED){
            binding.btRec.setListenForRecord(true);
        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},100);
        }
    }


    private void sendMessage(String msg,String type,String url) {
        String encryptedMsg = AES.encrypt(msg,"devesh1403");
        Date date = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String currentDay = dateFormat.format(date);

        Calendar current = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        String currentTime = timeFormat.format(current.getTime());

        ChatModel model = new ChatModel(SessionManagement.getUserId(), receiverId, type, url, encryptedMsg, currentDay + "," + currentTime);
        RecentChatModel model1 = new RecentChatModel(encryptedMsg,currentDay,currentTime,receiverName,receiverPic,receiverId);
        RecentChatModel model2 = new RecentChatModel(encryptedMsg,currentDay,currentTime,SessionManagement.getUserName(),SessionManagement.getUserPic(),SessionManagement.getUserId());
        reference.child("Chats").push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("RecentChat").child(SessionManagement.getUserId()).child(receiverId);
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ref1.setValue(model1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("RecentChat").child(receiverId).child(SessionManagement.getUserId());
        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ref2.setValue(model2);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("ChatList").child(SessionManagement.getUserId()).child(receiverId);
//        reference1.child("chatId").setValue(receiverId);
//        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("ChatList").child(receiverId).child(SessionManagement.getUserId());
//        reference2.child("chatId").setValue(SessionManagement.getUserId());
//
//        DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference("LastMessage").child(SessionManagement.getUserId()).child(receiverId);
//        reference3.child("Message").setValue(msg);
//        DatabaseReference reference4 = FirebaseDatabase.getInstance().getReference("LastMessage").child(receiverId).child(SessionManagement.getUserId());
//        reference4.child("Message").setValue(msg);


    }

    private void getDetails() {
        binding.toolbarChat.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChatActivity.this, HomeActivity.class));
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
            }
        });
        if (receiverId != null) {
            binding.name.setText(receiverName);
            if (receiverPic != null) {
                if (receiverPic.equals("")) {
                    binding.profImg.setImageResource(R.drawable.camera);
                } else {
                    Glide.with(this).load(receiverPic).into(binding.profImg);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ChatActivity.this, HomeActivity.class));
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
    }

    private void getConnectionStatus(String status) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("UserStatus").child(SessionManagement.getUserId());
        reference.child("status").setValue(status);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getConnectionStatus("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        getConnectionStatus("Offline");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getConnectionStatus("Offline");
    }

    private void getUserStatus() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("UserStatus").child(receiverId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String status = snapshot.child("status").getValue(String.class);
                    if (status.equals("Online")) {
                        binding.status.setText("Online");
                        userCurrentStatus = "Online";
                    } else {
                        binding.status.setText("Offline");
                        userCurrentStatus = "Offline";

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void setTypingStatus(String typing){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.child("Status").child(SessionManagement.getUserId()).child(receiverId).child("isTyping").setValue(typing);
    }
    private void getTypingStatus(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Status").child(receiverId).child(SessionManagement.getUserId());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String x = snapshot.child("isTyping").getValue().toString();
                    if(x.equals("typing...")){
                       binding.typing.setVisibility(View.VISIBLE);
                       binding.typing.setText(x);
                    }else{
                        binding.typing.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ChatActivity.this);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100 && resultCode==RESULT_OK && data!=null){
            Uri uri = data.getData();
//            photoUri = uri.toString();
//            picUri = uri;
//            Glide.with(this).load(uri).into(binding.);
            try {
                sendImageMessage(uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendImageMessage(Uri uri) throws IOException {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Sending Image...");
        dialog.setCancelable(false);
        dialog.show();
        String timeStamp = ""+System.currentTimeMillis();
        String path = "Media/Photos/"+SessionManagement.getUserId()+"/"+"sent_"+System.currentTimeMillis();
        Bitmap bitmap  = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte[] bytes = stream.toByteArray();
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(path);
        ref.putBytes(bytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                dialog.dismiss();
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while(!uriTask.isSuccessful());
                    String downloadUri = uriTask.getResult().toString();
                    if(uriTask.isSuccessful()){
                       sendMessage("Sent a photo","PHOTO",downloadUri);
                    }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(ChatActivity.this, "Failed to send Image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendRecordingMessage(String path){
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Sending Audio...");
        dialog.setCancelable(false);
        dialog.show();
        StorageReference reference = FirebaseStorage.getInstance().getReference("Media/Audios/"+SessionManagement.getUserId()+"/"+"sent_"+System.currentTimeMillis());
        Uri audioFile = Uri.fromFile(new File(audioPath));
        reference.putFile(audioFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                dialog.dismiss();
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                uriTask.addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        String downloadUrl = uriTask.getResult().toString();
                        if(uriTask.isSuccessful()){
                            sendMessage("Sent an audio","AUDIO",downloadUrl);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(ChatActivity.this, "Failed to send Image", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}