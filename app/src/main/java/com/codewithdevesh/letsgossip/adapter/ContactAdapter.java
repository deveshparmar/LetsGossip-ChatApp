package com.codewithdevesh.letsgossip.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codewithdevesh.letsgossip.R;
import com.codewithdevesh.letsgossip.activities.ChatActivity;
import com.codewithdevesh.letsgossip.model.RecentChatModel;
import com.codewithdevesh.letsgossip.model.UserModel;
import com.codewithdevesh.letsgossip.utilities.SessionManagement;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private List<UserModel>list;
    private Context context;
    private Bitmap bitmap;

    public ContactAdapter(List<UserModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_contact,parent,false);
        return new ViewHolder(view);
    }
    public void filterList(ArrayList<UserModel> filterllist) {
        this.list = filterllist;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ContactAdapter.ViewHolder holder, int position) {
       final UserModel model = list.get(position);
        Shimmer shimmer = new Shimmer.ColorHighlightBuilder()
                .setBaseColor(Color.parseColor("#f3f3f3"))
                .setBaseAlpha(1)
                .setHighlightColor(Color.parseColor("#cdd7df"))
                .setHighlightAlpha(1)
                .setDropoff(50)
                .build();
       String num = model.getPhoneNo();
       holder.name.setText(model.getName());
       holder.bio.setText(model.getBio());
        ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
        shimmerDrawable.setShimmer(shimmer);
        Glide.with(context).load(model.getPhotoUri()).placeholder(shimmerDrawable).into(holder.profilePic);
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        StorageReference storageReference = storage.getReference();
//        StorageReference ref = storageReference.child("profileImages/"+ num.toString()+".jpg");
//        Log.e("TAG","print:"+num.toString());
//        try{
//            File file = File.createTempFile("tempfile",".jpg");
//            ref.getFile(file)
//                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//                            Picasso.get().load(bitmap).into(holder.profilePic);
//
//
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(context, "Failed to load", Toast.LENGTH_SHORT).show();
//                }
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, ChatActivity.class)
                .putExtra("userId",model.getId())
                .putExtra("userName",model.getName())
                .putExtra("userProfilePic",model.getPhotoUri()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profilePic;
        TextView name,bio;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.profImgContact);
            name = itemView.findViewById(R.id.nameContact);
            bio = itemView.findViewById(R.id.bioContact);
        }
    }

}
