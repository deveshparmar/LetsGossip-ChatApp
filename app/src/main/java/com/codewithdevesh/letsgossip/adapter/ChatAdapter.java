package com.codewithdevesh.letsgossip.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codewithdevesh.letsgossip.R;
import com.codewithdevesh.letsgossip.model.ChatModel;
import com.codewithdevesh.letsgossip.security.AES;
import com.codewithdevesh.letsgossip.utilities.SessionManagement;

import java.util.List;

import me.jagar.chatvoiceplayerlibrary.VoicePlayerView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private List<ChatModel>list;
    private Context context;
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

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
            holder.rl.setVisibility(View.GONE);
            holder.dateTime_p.setVisibility(View.GONE);
            holder.playerView.setVisibility(View.GONE);
            holder.dateTime_r.setVisibility(View.GONE);
            holder.bind(list.get(position));
        }else if(type.equals("PHOTO")){
            holder.imageView.setVisibility(View.VISIBLE);
            holder.textMsg.setVisibility(View.GONE);
            holder.dateTime_t.setVisibility(View.GONE);
            holder.playerView.setVisibility(View.GONE);
            holder.dateTime_r.setVisibility(View.GONE);
            Glide.with(context).load(model.getUrl()).placeholder(R.drawable.user).into(holder.imageView);
            holder.dateTime_p.setText(model.getDateTime());
        }else if(type.equals("AUDIO")){
            holder.rl.setVisibility(View.GONE);
            holder.textMsg.setVisibility(View.GONE);
            holder.dateTime_p.setVisibility(View.GONE);
            holder.dateTime_t.setVisibility(View.GONE);
            holder.playerView.setVisibility(View.VISIBLE);
            holder.dateTime_r.setVisibility(View.VISIBLE);
            holder.dateTime_r.setText(model.getDateTime());
            holder.playerView.setAudio(model.getUrl());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textMsg,dateTime_t,dateTime_p,dateTime_r;
        private ImageView imageView;
        private RelativeLayout rl;
        private VoicePlayerView playerView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textMsg = itemView.findViewById(R.id.tv_message);
            dateTime_t = itemView.findViewById(R.id.dateTime_text);
            imageView = itemView.findViewById(R.id.image);
            dateTime_p = itemView.findViewById(R.id.dateTime_photo);
            dateTime_r = itemView.findViewById(R.id.dateTime_record);
            rl = itemView.findViewById(R.id.rl);
            playerView = itemView.findViewById(R.id.voicePlayerView);
        }
        void bind(ChatModel chats){
            textMsg.setText(AES.decrypt(chats.getTextMessage(),"devesh1403"));
            dateTime_t.setText(chats.getDateTime());
        }
    }
    @Override
    public int getItemViewType(int position) {

        if (list.get(position).getSender().equals(SessionManagement.getUserId())){
            return MSG_TYPE_RIGHT;
        } else{
            return MSG_TYPE_LEFT;
        }
    }
}
