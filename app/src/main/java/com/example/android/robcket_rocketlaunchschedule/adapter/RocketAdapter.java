package com.example.android.robcket_rocketlaunchschedule.adapter;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.robcket_rocketlaunchschedule.R;
import com.example.android.robcket_rocketlaunchschedule.activity.MainActivity;
import com.example.android.robcket_rocketlaunchschedule.model.Rocket;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RocketAdapter extends RecyclerView.Adapter<RocketAdapter.RocketViewHolder> {

    private ArrayList<Rocket> rocketList;
    private Context mContext;

    public RocketAdapter(Context context, ArrayList<Rocket> rocketList) {
        this.rocketList = rocketList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public RocketAdapter.RocketViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.launch_list_item, viewGroup, false);
        return new RocketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RocketAdapter.RocketViewHolder rocketViewHolder, int position) {
        // Set the Text
        rocketViewHolder.txtRocketName.setText(rocketList.get(position).getName());

        // Set the ImageView based on String image Url
        Picasso.with(mContext).load(rocketList.get(position).getImageURL()).into(rocketViewHolder.ivRocketImage);
    }

    @Override
    public int getItemCount() {
        return rocketList.size();
    }

    class RocketViewHolder extends RecyclerView.ViewHolder {

        TextView txtRocketName;
        ImageView ivRocketImage;

        RocketViewHolder(@NonNull View itemView) {
            super(itemView);
            txtRocketName = itemView.findViewById(R.id.txt_rocket_title);
            ivRocketImage = itemView.findViewById(R.id.rocket_image_view);
        }
    }
}
