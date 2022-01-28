package com.codewithdevesh.letsgossip.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.bumptech.glide.Glide;
import com.codewithdevesh.letsgossip.R;
import com.codewithdevesh.letsgossip.activities.UpdateProfileActivity;
import com.codewithdevesh.letsgossip.databinding.FragmentProfileBinding;
import com.codewithdevesh.letsgossip.utilities.SessionManagement;

import java.util.Objects;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        SessionManagement.init(getActivity());
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_profile, container, false);
        Glide.with(getActivity()).load(SessionManagement.getUserPic()).into(binding.profileImg);
        binding.tvBio.setText(SessionManagement.getUserBio());
        binding.tvName.setText(SessionManagement.getUserName());
        binding.tvPhone.setText(SessionManagement.getUserPhoneNo());
        Log.e("TAG",SessionManagement.getUserBio());
        Log.e("TAG",SessionManagement.getUserName());
        Log.e("TAG",SessionManagement.getUserPic());
        binding.bttnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT,"Share Lets Gossip");
                i.putExtra(Intent.EXTRA_TEXT,"Lets chat on Lets Gossip. Its fast and secure and on testing stage.Install it now from");
                startActivity(Intent.createChooser(i, "Share Via"));
            }
        });
        binding.bttnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), UpdateProfileActivity.class));
                requireActivity().overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
            }
        });
        return binding.getRoot();
    }
}