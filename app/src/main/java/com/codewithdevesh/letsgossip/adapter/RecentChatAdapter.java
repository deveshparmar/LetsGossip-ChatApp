package com.codewithdevesh.letsgossip.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codewithdevesh.letsgossip.R;
import com.codewithdevesh.letsgossip.activities.ChatActivity;
import com.codewithdevesh.letsgossip.model.ChatModel;
import com.codewithdevesh.letsgossip.model.RecentChatModel;
import com.codewithdevesh.letsgossip.utilities.SessionManagement;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecentChatAdapter extends RecyclerView.Adapter<RecentChatAdapter.ViewHolder> {
    private List<RecentChatModel>list;
    private Context context;

    public RecentChatAdapter(@NonNull List<RecentChatModel> list, Context context) {
        this.list = list;
        this.context = context;
    }
    public void setList(List<RecentChatModel> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override

    public RecentChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View v = LayoutInflater.from(context).inflate(R.layout.layout_recent_chat,parent,false);
      return new ViewHolder(v);
    }
    public void filterList(ArrayList<RecentChatModel> filterllist) {
        this.list = filterllist;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull RecentChatAdapter.ViewHolder holder, int position) {
        RecentChatModel model = list.get(position);
        Shimmer shimmer = new Shimmer.ColorHighlightBuilder()
                .setBaseColor(Color.parseColor("#f3f3f3"))
                .setBaseAlpha(1)
                .setHighlightColor(Color.parseColor("#cdd7df"))
                .setHighlightAlpha(1)
                .setDropoff(50)
                .build();
        ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
        shimmerDrawable.setShimmer(shimmer);
        holder.name.setText(model.getName());
        holder.textMsg.setText(model.getLastMessage());
        holder.time.setText(model.getTime());
        holder.date.setText(model.getDate());
        Glide.with(context).load(model.getProfile()).placeholder(shimmerDrawable).into(holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, ChatActivity.class)
                        .putExtra("userId", model.getUserId())
                        .putExtra("userName", model.getName())
                        .putExtra("userProfilePic", model.getProfile()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textMsg,name,date,time;
        CircleImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textMsg = itemView.findViewById(R.id.recentMsg);
            imageView = itemView.findViewById(R.id.profImgResent);
            name = itemView.findViewById(R.id.nameRecent);
            date = itemView.findViewById(R.id.recent_date);
            time = itemView.findViewById(R.id.recent_time);

        }

    }

}
