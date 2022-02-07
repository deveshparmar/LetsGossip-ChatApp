package com.codewithdevesh.letsgossip.fragment;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codewithdevesh.letsgossip.R;
import com.codewithdevesh.letsgossip.adapter.ChatAdapter;
import com.codewithdevesh.letsgossip.adapter.RecentChatAdapter;
import com.codewithdevesh.letsgossip.adapter.StatusAdapter;
import com.codewithdevesh.letsgossip.databinding.FragmentStatusBinding;
import com.codewithdevesh.letsgossip.model.ChatModel;
import com.codewithdevesh.letsgossip.model.RecentChatModel;
import com.codewithdevesh.letsgossip.model.StatusModel;
import com.codewithdevesh.letsgossip.utilities.SessionManagement;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import xute.storyview.StoryModel;


public class StatusFragment extends Fragment {

    private FragmentStatusBinding binding;
    int PICK_IMAGE_MULTIPLE = 1;
    private List<RecentChatModel>list;
    RecentChatAdapter adapter;
    ArrayList<String>imgList;
    public StatusFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_status, container, false);
        list = new ArrayList<>();
        imgList = new ArrayList<>();
        binding.fabStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
                StatusModel model = new StatusModel();
                model.setName(SessionManagement.getUserName());
                Date date = new Date();
                String time = String.valueOf(date.getTime());
                model.setTime(time);
                model.setStatuses(imgList);


            }
        });
        return binding.getRoot();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == Activity.RESULT_OK && null != data) {
            // Get the Image from data
            if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                int cout = data.getClipData().getItemCount();
                for (int i = 0; i < cout; i++) {
                    // adding imageuri in array
                    Uri imageurl = data.getClipData().getItemAt(i).getUri();
                    imgList.add(imageurl.toString());
                }
                // setting 1st selected image into image switcher
            } else {
                Uri imageurl = data.getData();
                imgList.add(imageurl.toString());

            }
        } else {
            // show this if no image is selected
            Toast.makeText(getActivity(), "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }
}