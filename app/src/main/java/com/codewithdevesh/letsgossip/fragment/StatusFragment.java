package com.codewithdevesh.letsgossip.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codewithdevesh.letsgossip.R;
import com.codewithdevesh.letsgossip.databinding.FragmentStatusBinding;
import com.codewithdevesh.letsgossip.utilities.SessionManagement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;


public class StatusFragment extends Fragment {

    private FragmentStatusBinding binding;
    private SimpleDateFormat dateFormat;
    private Uri imageUri;
    private Calendar calendar;
    private ArrayList<MyStory>stories;
    public StatusFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_status, container, false);
        stories = new ArrayList<>();
        calendar = Calendar.getInstance();
        binding.fabStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });
        dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        binding.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(stories.isEmpty()){
                    pickImage();
                }else{
                    new StoryView.Builder(getParentFragmentManager()).setStoriesList(stories).setStoryDuration(5000)
                            .setTitleText(SessionManagement.getUserName())
                            .setTitleLogoUrl(SessionManagement.getUserPic())
                            .setStoryClickListeners(new StoryClickListeners() {
                                @Override
                                public void onDescriptionClickListener(int position) {

                                }

                                @Override
                                public void onTitleIconClickListener(int position) {

                                }
                            }).build().show();
                }
            }
        });
        return binding.getRoot();

    }

    private void pickImage() {
        Intent intent = new Intent();
        intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI,"image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Images"),100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if(requestCode==100){
                    if(resultCode==Activity.RESULT_OK){
                        if(data.getClipData()!=null){
                            int c = data.getClipData().getItemCount();
                            for(int i=0;i<c;i++){
                                imageUri = data.getClipData().getItemAt(i).getUri();
                                stories.add(new MyStory(imageUri.toString(),dateFormat.parse("10/2/2022 4:00:00")));
                            }
                            Glide.with(getContext()).load(imageUri).into(binding.profImgResent);
                        }else{
                            imageUri = data.getData();
                            stories.add(new MyStory(imageUri.toString(),dateFormat.parse("10/2/2022 4:00:00")));
                            Glide.with(getContext()).load(imageUri).into(binding.profImgResent);
                        }
                    }

            }else {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(getContext(), "Error - "+e.toString(), Toast.LENGTH_SHORT).show();
        }

    }
}