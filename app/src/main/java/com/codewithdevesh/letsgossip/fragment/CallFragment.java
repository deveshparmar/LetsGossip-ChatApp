package com.codewithdevesh.letsgossip.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codewithdevesh.letsgossip.R;
import com.codewithdevesh.letsgossip.databinding.FragmentCallBinding;


public class CallFragment extends Fragment {

    public CallFragment() {
        // Required empty public constructor
    }
   FragmentCallBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_call, container, false);
//        ArrayList<StoryModel> StoriesList = new ArrayList<>();  // create a Array list of Stories
//        StoriesList.add(new StoryModel("https://images.unsplash.com/photo-1503023345310-bd7c1de61c7d?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&w=1000&q=80","Status 1","Yesterday"));
//        StoriesList.add(new StoryModel("https://www.bigstockphoto.com/images/homepage/module-6.jpg","Status 2","10:15 PM"));
//        StoriesList.add(new StoryModel("https://www.gettyimages.com/gi-resources/images/500px/983794168.jpg","Satus 3","Today,2:31 PM"));
//        binding.storyView.setImageUris(StoriesList);
        return binding.getRoot();
    }
}