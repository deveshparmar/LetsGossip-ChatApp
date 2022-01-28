package com.codewithdevesh.letsgossip.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codewithdevesh.letsgossip.R;
import com.codewithdevesh.letsgossip.activities.ChatActivity;
import com.codewithdevesh.letsgossip.model.ChatListModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter  extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {
    private List<ChatListModel> list;
    private Context context;

    public ChatListAdapter(List<ChatListModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.layout_recent_chat,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListAdapter.ViewHolder holder, int position) {
        final ChatListModel model = list.get(position);
        holder.name.setText(model.getUsrName());
        holder.desc.setText(model.getDescription());
        holder.date.setText(model.getDate());
        Glide.with(context).load(model.getUrlProfile()).into(holder.profile);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, ChatActivity.class)
                        .putExtra("userId",model.getUsrId())
                        .putExtra("userName",model.getUsrName())
                        .putExtra("userProfilePic",model.getUrlProfile()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name,desc,date;
        private CircleImageView profile;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameRecent);
            desc = itemView.findViewById(R.id.recentMsg);
            date = itemView.findViewById(R.id.recent_date);
            profile=itemView.findViewById(R.id.profImgResent);

        }
    }
}
