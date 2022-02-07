package com.codewithdevesh.letsgossip.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.codewithdevesh.letsgossip.R;
import com.codewithdevesh.letsgossip.activities.ContactActivity;
import com.codewithdevesh.letsgossip.adapter.ChatListAdapter;
import com.codewithdevesh.letsgossip.adapter.RecentChatAdapter;
import com.codewithdevesh.letsgossip.databinding.FragmentChatBinding;
import com.codewithdevesh.letsgossip.model.ChatListModel;
import com.codewithdevesh.letsgossip.model.RecentChatModel;
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
import java.util.Locale;
import java.util.Objects;

public class ChatFragment extends Fragment {
    private FragmentChatBinding binding;
    private List<RecentChatModel> list;
    private RecentChatAdapter adapter;
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

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        list = new ArrayList<>();
        if(firebaseUser!=null) {
           getChatList();
        }

        binding.fabContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ContactActivity.class));
            }
        });
        binding.sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filter(s);
                return false;
            }
        });
        return binding.getRoot();
    }

    private void filter(String newText) {
        ArrayList<RecentChatModel>filterList = new ArrayList<>();
        for(RecentChatModel model: list){
            if(model.getName().toLowerCase(Locale.ROOT).contains(newText.toLowerCase(Locale.ROOT))){
                filterList.add(model);
            }
        }
        if(filterList.isEmpty()){
            Toast.makeText(getActivity(), "No Data Found..", Toast.LENGTH_SHORT).show();
        }else{
            adapter.filterList(filterList);
        }
    }

    private void getChatList() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("RecentChat").child(SessionManagement.getUserPhoneNo());
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            binding.shimmerLayout.stopShimmer();
                            binding.shimmerLayout.setVisibility(View.GONE);
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                RecentChatModel model = ds.getValue(RecentChatModel.class);
                                list.add(model);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                adapter = new RecentChatAdapter(list,getActivity());
                binding.rvRecentChat.setLayoutManager(new LinearLayoutManager(getActivity()));
                binding.rvRecentChat.setAdapter(adapter);
            }
        });
    }
}