package com.codewithdevesh.letsgossip.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codewithdevesh.letsgossip.R;
import com.codewithdevesh.letsgossip.model.ChatModel;
import com.codewithdevesh.letsgossip.utilities.SessionManagement;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private List<ChatModel>list;
    private Context context;
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private FirebaseUser firebaseUser;

    public ChatAdapter(@NonNull List<ChatModel> list, Context context) {
        this.list = list;
        this.context = context;
    }
    public void setList(List<ChatModel> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override

    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MSG_TYPE_LEFT){
            View v = LayoutInflater.from(context).inflate(R.layout.receive_layout,parent,false);
            return new ViewHolder(v);
        }else{
            View v = LayoutInflater.from(context).inflate(R.layout.send_layout,parent,false);
            return new ViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        ChatModel model = list.get(position);
//        holder.bind(list.get(position));
        String type = model.getType();
        if(type.equals("TEXT")){
            holder.textMsg.setVisibility(View.VISIBLE);
            holder.imageView.setVisibility(View.GONE);
            holder.bind(list.get(position));
        }else if(type.equals("PHOTO")){
            holder.imageView.setVisibility(View.VISIBLE);
            holder.textMsg.setVisibility(View.GONE);
            Glide.with(context).load(model.getUrl()).placeholder(R.drawable.user).into(holder.imageView);
            holder.dateTime.setText(model.getDateTime());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textMsg,dateTime;
        private ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textMsg = itemView.findViewById(R.id.tv_message);
            dateTime = itemView.findViewById(R.id.dateTime);
            imageView = itemView.findViewById(R.id.image);
        }
        void bind(ChatModel chats){
            textMsg.setText(chats.getTextMessage());
            dateTime.setText(chats.getDateTime());
        }
    }
    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (list.get(position).getSender().equals(SessionManagement.getUserPhoneNo())){
            return MSG_TYPE_RIGHT;
        } else{
            return MSG_TYPE_LEFT;
        }
    }
}
