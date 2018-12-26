package com.example.android.robcket_rocketlaunchschedule.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.robcket_rocketlaunchschedule.R;
import com.example.android.robcket_rocketlaunchschedule.activity.LaunchDetailActivity;
import com.example.android.robcket_rocketlaunchschedule.model.Launch;
import com.example.android.robcket_rocketlaunchschedule.model.LaunchNextList;
import com.example.android.robcket_rocketlaunchschedule.model.Mission;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LaunchNextAdapter extends RecyclerView.Adapter<LaunchNextAdapter.LaunchNextViewHolder> {

    // Private Variables for putExtra intents
    private String ROCKET_IMAGE_EXTRA = "LAUNCH_IMAGE_URL";
    private String LAUNCH_TITLE_EXTRA = "LAUNCH_TITLE";
    private String MISSION_NAME_EXTRA = "LAUNCH_MISSION_NAME";
    private String MISSION_SUMMARY_EXTRA = "LAUNCH_MISSION_SUMMARY";
    private String LAUNCH_VID_URL_EXTRA = "LAUNCH_VID_URL";
    private String LAUNCH_DATE_EXTRA = "LAUNCH_DATE";
    private String LAUNCH_WINDOW_EXTRA = "LAUNCH_WINDOW";
    private String ROCKET_NAME_EXTRA = "LAUNCH_ROCKET_NAME";
    private String ROCKET_WIKI_URL_EXTRA = "LAUNCH_ROCKET_WIKI_URL";
    private String PAD_NAME_EXTRA = "LAUNCH_PAD";
    private String PAD_MAP_URL_EXTRA = "LAUNCH_PAD_MAP_URL";
    private String AGENCY_NAME_EXTRA = "LAUNCH_AGENCY_NAME";
    private String AGENCY_WIKI_URL_EXTRA = "LAUNCH_AGENCY_WIKI_URL";


    // Private Variables for constructors
    private ArrayList<Launch> launchList;
    private Context mContext;
    private ArrayList<Mission> missionList;

    public LaunchNextAdapter(Context context, ArrayList<Launch> launchList, ArrayList<Mission> missionList) {
        this.launchList = launchList;
        this.mContext = context;
        this.missionList = missionList;
    }

    @NonNull
    @Override
    public LaunchNextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.launch_list_item, parent, false);
        return new LaunchNextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LaunchNextViewHolder launchNextViewHolder, int position) {
        // Set the launch title text
        launchNextViewHolder.txtLaunchTitle.setText(launchList.get(position).getName());

        // Set the launch image
        // Set the ImageView based on String image Url
        Picasso.with(mContext)
                .load(launchList.get(position).getRocket().getImageURL())
                .placeholder(R.drawable.ic_rocket)
                .into(launchNextViewHolder.ivLaunchImage);

        // Set the launch date text
        launchNextViewHolder.txtLaunchDate.setText(getLocalDate(launchList.get(position).getWindowstart()));

        // Set the launch location text
        launchNextViewHolder.txtLaunchLocation.setText(launchList.get(position).getLocation().getName());

    }

    @Override
    public int getItemCount() {
        return launchList.size();
    }

    public class LaunchNextViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtLaunchTitle;
        ImageView ivLaunchImage;
        TextView txtLaunchDate;
        TextView txtLaunchLocation;

        public LaunchNextViewHolder(@NonNull View itemView) {
            super(itemView);
            txtLaunchTitle = itemView.findViewById(R.id.txt_launch_title);
            ivLaunchImage = itemView.findViewById(R.id.launch_rocket_image_view);
            txtLaunchDate = itemView.findViewById(R.id.txt_launch_date);
            txtLaunchLocation = itemView.findViewById(R.id.txt_launch_location);

            // Set the OnClickListener to entire view
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // Image URL for the Rocket
            String currentRocketImageUrl = launchList.get(getAdapterPosition()).getRocket().getImageURL();

            // Launch Title
            String currentLaunchTitle = launchList.get(getAdapterPosition()).getName();

            // Launch Date
            String currentLaunchDate = launchList.get(getAdapterPosition()).getWindowstart();
            String currentLaunchDateLocal = getLocalDate(currentLaunchDate);

            // Launch Window
            String currentLaunchStartWindow = launchList.get(getAdapterPosition()).getWindowstart();
            String currentLaunchEndWindow = launchList.get(getAdapterPosition()).getWindowend();
            String currentLaunchWindow = getLocalTimeWindow(currentLaunchStartWindow, currentLaunchEndWindow);

            // Mission Name / Type
            String currentMissionName = missionList.get(getAdapterPosition()).getName();

            // Mission Summary
            String currentMissionSummary = missionList.get(getAdapterPosition()).getDescription();

            // Rocket Name
            String currentRocketName = launchList.get(getAdapterPosition()).getRocket().getName();

            // Rocket Wiki Url
            String currentRocketWikiUrl = launchList.get(getAdapterPosition()).getRocket().getWikiURL();

            // Pad Name
            String currentPadName = launchList.get(getAdapterPosition()).getLocation().getPads().get(0).getName();

            // Pad Google Map Url
            String currentPadMapUrl = launchList.get(getAdapterPosition()).getLocation().getPads().get(0).getMapURL();

            // Agency (Launch Service Provider) Name
            String currentAgencyName = launchList.get(getAdapterPosition()).getLsp().getName();

            // Agency (Launch Service Provider) Url
            String currentAgencyUrl = launchList.get(getAdapterPosition()).getLsp().getWikiURL();

            String currentLaunchVidsUrls;
            // Launch Vid Urls (first index)
            if (launchList.get(getAdapterPosition()).getVidURLs().isEmpty()) {
                currentLaunchVidsUrls = "empty";
            } else {
                currentLaunchVidsUrls = launchList.get(getAdapterPosition()).getVidURLs().get(0);
            }

            Intent detailLaunchIntent = new Intent(mContext, LaunchDetailActivity.class);

            // PutExtra all information for LaunchDetailActivity.class
            detailLaunchIntent.putExtra(LAUNCH_TITLE_EXTRA, currentLaunchTitle);
            detailLaunchIntent.putExtra(ROCKET_IMAGE_EXTRA, currentRocketImageUrl);
            detailLaunchIntent.putExtra(MISSION_NAME_EXTRA, currentMissionName);
            detailLaunchIntent.putExtra(MISSION_SUMMARY_EXTRA, currentMissionSummary);
            detailLaunchIntent.putExtra(LAUNCH_DATE_EXTRA, currentLaunchDateLocal);
            detailLaunchIntent.putExtra(LAUNCH_WINDOW_EXTRA, currentLaunchWindow);
            detailLaunchIntent.putExtra(ROCKET_NAME_EXTRA, currentRocketName);
            detailLaunchIntent.putExtra(ROCKET_WIKI_URL_EXTRA, currentRocketWikiUrl);
            detailLaunchIntent.putExtra(PAD_NAME_EXTRA, currentPadName);
            detailLaunchIntent.putExtra(PAD_MAP_URL_EXTRA, currentPadMapUrl);
            detailLaunchIntent.putExtra(AGENCY_NAME_EXTRA, currentAgencyName);
            detailLaunchIntent.putExtra(AGENCY_WIKI_URL_EXTRA, currentAgencyUrl);
            detailLaunchIntent.putExtra(LAUNCH_VID_URL_EXTRA, currentLaunchVidsUrls);

            mContext.startActivity(detailLaunchIntent);
        }
    }

    // Get the List
    public ArrayList<Launch> getItems() {
        return launchList;
    }

    // Add elements to the list
    public void addAll(ArrayList<Launch> launchList) {
        launchList.addAll(launchList);
        notifyDataSetChanged();
    }


    // Clean all elements of the recycler
    public void clear() {
        launchList.clear();
        notifyDataSetChanged();
    }

    /**
     * This method parses JSON Time Response to Local Time
     *
     * @param ourDate String of json time response, example: December 13, 2018 04:00:00 UTC
     * @return String of local time , example: December 13, 2018 10:00:00 CEST (If device in Germany)
     */
    private String getLocalDate(String ourDate) {
        // Example Date String Response: "December 13, 2018 04:00:00 UTC"
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("MMM d, yyyy HH:mm:ss z", Locale.ENGLISH);
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date value = formatter.parse(ourDate);

            SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM d, yyyy HH:mm:ss z", Locale.ENGLISH); //this format changeable
            dateFormatter.setTimeZone(TimeZone.getDefault());
            ourDate = dateFormatter.format(value);

            //Log.d("ourDate", ourDate);
        } catch (Exception e) {
            ourDate = "00-00-0000 00:00";
        }
        return ourDate;
    }

    /**
     * This method parses JSON Time Response to Window Time
     *
     * @param startDate String of json time response for start of launch,
     *                  example: December 13, 2018 04:00:00 UTC
     * @param endDate   String of json time response for end of launch,
     *                  example: December 13, 2018 04:00:00 UTC
     * @return String of window time , example: 10:00:00 - 16:00:00 CEST
     */
    private String getLocalTimeWindow(String startDate, String endDate) {
        // String for time window of launch
        String windowTime;

        // Example Date String Response: "December 13, 2018 04:00:00 UTC"
        try {
            // If startDate is not equal with EndDate
            if (!startDate.equals(endDate)) {
                // Start Date
                SimpleDateFormat formatter = new SimpleDateFormat("MMM d, yyyy HH:mm:ss z", Locale.ENGLISH);
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date valueDateStart = formatter.parse(startDate);

                SimpleDateFormat timeStartFormatter = new SimpleDateFormat("HH:mm", Locale.ENGLISH); //this format changeable
                timeStartFormatter.setTimeZone(TimeZone.getDefault());
                startDate = timeStartFormatter.format(valueDateStart);

                // End Date
                Date valueDateEnd = formatter.parse(endDate);

                SimpleDateFormat timeEndFormatter = new SimpleDateFormat("HH:mm z", Locale.ENGLISH); //this format changeable
                timeEndFormatter.setTimeZone(TimeZone.getDefault());
                endDate = timeEndFormatter.format(valueDateEnd);

                // Set the window time string
                windowTime = String.format("%s - %s", startDate, endDate);
            } else {
                windowTime = "Unknown";
            }

            //Log.d("ourDate", ourDate);
        } catch (Exception e) {
            windowTime = "00-00-0000 00:00";
        }
        return windowTime;
    }


}
