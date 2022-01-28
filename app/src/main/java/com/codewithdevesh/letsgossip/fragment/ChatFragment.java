package com.codewithdevesh.letsgossip.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codewithdevesh.letsgossip.R;
import com.codewithdevesh.letsgossip.activities.ContactActivity;
import com.codewithdevesh.letsgossip.adapter.ChatListAdapter;
import com.codewithdevesh.letsgossip.databinding.FragmentChatBinding;
import com.codewithdevesh.letsgossip.model.ChatListModel;
import com.codewithdevesh.letsgossip.model.UserModel;
import com.codewithdevesh.letsgossip.utilities.SessionManagement;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatFragment extends Fragment {
    private FragmentChatBinding binding;
    private List<ChatListModel> list;
    private ArrayList<String> allUserId;
    private ChatListAdapter adapter;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private Handler handler = new Handler();
    private String userNo;
    private String message;
    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  DataBindingUtil.inflate(inflater,R.layout.fragment_chat, container, false);
        SessionManagement.init(getContext());
        list = new ArrayList<>();
        allUserId = new ArrayList<>();
        binding.rvRecentChat.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ChatListAdapter(list,getContext());
        binding.rvRecentChat.setAdapter(adapter);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        if(firebaseUser!=null) {
            getChatList();
        }

        binding.fabContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ContactActivity.class));
            }
        });
        return binding.getRoot();
    }

    private void getChatList() {
        binding.pb.setVisibility(View.VISIBLE);
        list.clear();
        allUserId.clear();

        reference.child("ChatList").child(SessionManagement.getUserPhoneNo()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String userID = Objects.requireNonNull(snapshot.child("chatId").getValue()).toString();
                    Log.d("TAG", "onDataChange: userId "+userID);

                    binding.pb.setVisibility(View.GONE);
                    allUserId.add(userID);
                }
                getUserInfo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    private void getUserInfo() {

        handler.post(new Runnable() {
            @Override
            public void run() {

                for (String userID : allUserId){
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
                    reference.child(userID).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            UserModel model1 = dataSnapshot.getValue(UserModel.class);
                            userNo = model1.getPhoneNo();
                            String userName = model1.getName();
                            String profilePic = model1.getPhotoUri();
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                            databaseReference.child("LastMessage").child(SessionManagement.getUserPhoneNo()).child(userID).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        String z = snapshot.child("Message").getValue().toString();
                                        Log.e("TAG",z);
                                        message=z;
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            ChatListModel chats = new ChatListModel(userNo, userName, message, "", profilePic);
                            Log.i("TAG", "onSuccess: "+message);
                            list.add(chats);

                            if(adapter!=null){
                                adapter.notifyItemInserted(0);
                                adapter.notifyDataSetChanged();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "failed " +e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}