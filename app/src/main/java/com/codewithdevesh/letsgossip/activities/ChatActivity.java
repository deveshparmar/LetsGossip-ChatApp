package com.codewithdevesh.letsgossip.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.bumptech.glide.Glide;
import com.codewithdevesh.letsgossip.R;
import com.codewithdevesh.letsgossip.adapter.ChatAdapter;
import com.codewithdevesh.letsgossip.databinding.ActivityChatBinding;
import com.codewithdevesh.letsgossip.model.ChatModel;
import com.codewithdevesh.letsgossip.model.RecentChatModel;
import com.codewithdevesh.letsgossip.security.AES;
import com.codewithdevesh.letsgossip.utilities.SessionManagement;
import com.codewithdevesh.letsgossip.utilities.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.google.GoogleEmojiProvider;


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
    private String typing;
    private String userCurrentStatus;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setStatusBarColor(this, R.color.light_background);
        SessionManagement.init(ChatActivity.this);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
        Intent i = getIntent();
        receiverId = i.getStringExtra("userId");
        receiverName = i.getStringExtra("userName");
        receiverPic = i.getStringExtra("userProfilePic");
        getDetails();
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
                    setTypingStatus("not");
                    typing="not";
                } else {
                    binding.fabSend.setEnabled(true);
                    setTypingStatus("typing...");
                    typing="typing...";
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        listeners();
        list = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager.setStackFromEnd(true);
        binding.rvChat.setLayoutManager(layoutManager);
        readChats();
        binding.attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
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
                        if (chatModel != null && chatModel.getSender().equals(SessionManagement.getUserPhoneNo()) && chatModel.getReceiver().equals(receiverId) || chatModel.getReceiver().equals(SessionManagement.getUserPhoneNo()) && chatModel.getSender().equals(receiverId)) {
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
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(binding.etMessage.getText().toString())) {
                    sendMessage(binding.etMessage.getText().toString());
                }
                binding.etMessage.setText("");
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendMessage(String msg) {
        Date date = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String currentDay = dateFormat.format(date);

        Calendar current = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        String currentTime = timeFormat.format(current.getTime());

        ChatModel model = new ChatModel(SessionManagement.getUserPhoneNo(), receiverId, "TEXT", "", msg, currentDay + "," + currentTime);
        RecentChatModel model1 = new RecentChatModel(msg,currentDay,currentTime,receiverName,receiverPic,receiverId);
        RecentChatModel model2 = new RecentChatModel(msg,currentDay,currentTime,SessionManagement.getUserName(),SessionManagement.getUserPic(),SessionManagement.getUserPhoneNo());
        reference.child("Chats").push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        reference.child("MyChats").child(SessionManagement.getUserPhoneNo()).child(receiverId).push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });

        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("RecentChat").child(SessionManagement.getUserPhoneNo()).child(receiverId);
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ref1.setValue(model1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("RecentChat").child(receiverId).child(SessionManagement.getUserPhoneNo());
        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ref2.setValue(model2);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("ChatList").child(SessionManagement.getUserPhoneNo()).child(receiverId);
        reference1.child("chatId").setValue(receiverId);
        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("ChatList").child(receiverId).child(SessionManagement.getUserPhoneNo());
        reference2.child("chatId").setValue(SessionManagement.getUserPhoneNo());

        DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference("LastMessage").child(SessionManagement.getUserPhoneNo()).child(receiverId);
        reference3.child("Message").setValue(msg);
        DatabaseReference reference4 = FirebaseDatabase.getInstance().getReference("LastMessage").child(receiverId).child(SessionManagement.getUserPhoneNo());
        reference4.child("Message").setValue(msg);


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
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("UserStatus").child(SessionManagement.getUserPhoneNo());
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
        reference.child("Status").child(SessionManagement.getUserPhoneNo()).child(receiverId).child("isTyping").setValue(typing);
    }
    private void getTypingStatus(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Status").child(receiverId).child(SessionManagement.getUserPhoneNo());
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
}