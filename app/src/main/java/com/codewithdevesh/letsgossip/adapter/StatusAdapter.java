package com.codewithdevesh.letsgossip.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codewithdevesh.letsgossip.R;
import com.codewithdevesh.letsgossip.model.StatusModel;

import java.util.List;

import xute.storyview.StoryModel;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.ViewhHolder> {
    private List<StoryModel>list;
    private Context context;

    public StatusAdapter(List<StoryModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public StatusAdapter.ViewhHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v =  LayoutInflater.from(context).inflate(R.layout.layout_status,parent,false);
        return new ViewhHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusAdapter.ViewhHolder holder, int position) {
        StoryModel model = list.get(position);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewhHolder extends RecyclerView.ViewHolder {
        public ViewhHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
