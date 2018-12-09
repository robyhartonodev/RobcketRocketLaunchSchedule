package com.example.android.robcket_rocketlaunchschedule.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.robcket_rocketlaunchschedule.R;
import com.example.android.robcket_rocketlaunchschedule.model.Rocket;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

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
        View view = layoutInflater.inflate(R.layout.rocket_list_item, viewGroup, false);
        return new RocketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RocketAdapter.RocketViewHolder rocketViewHolder, int position) {
        // Set the Text
        rocketViewHolder.txtRocketName.setText(rocketList.get(position).getName());


        // Set the ImageView based on String image Url
        Picasso.with(mContext)
                .load(rocketList.get(position).getImageURL())
                .placeholder(R.drawable.ic_placeholder_rocket)
                .into(rocketViewHolder.ivRocketImage);

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

    // Clean all elements of the recycler
    public void clear() {
        rocketList.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(ArrayList<Rocket> list) {
        rocketList.addAll(list);
        notifyDataSetChanged();
    }
}
