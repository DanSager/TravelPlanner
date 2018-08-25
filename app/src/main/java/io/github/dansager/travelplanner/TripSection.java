package io.github.dansager.travelplanner;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import io.github.dansager.travelplanner.data_structures.Trip;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

public class TripSection extends StatelessSection {

    public static Context mCtx;

    List<Trip> list;
    String title;

    public TripSection(Context mCtx, String title, List<Trip> list) {
        super(SectionParameters.builder().itemResourceId(R.layout.recycler_items).headerResourceId(R.layout.recycler_header).build());

        this.mCtx = mCtx;
        this.title = title;
        this.list = list;
    }

    @Override
    public int getContentItemsTotal() {
        return list.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new TripViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Trip trip = list.get(position);

        final TripViewHolder itemHolder = (TripViewHolder) holder;

        itemHolder.textName.setText(trip.getName());

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mCtx);
        final Boolean dateFormat = pref.getBoolean("pref_app_date_format",false);   //Default/false = mm/dd/yyyy
        if (dateFormat == false) {
            itemHolder.textDesc.setText(trip.getStartDate().getMonthDayYear() + " - " + trip.getEndDate().getMonthDayYear());
        } else if (dateFormat == true) {
            itemHolder.textDesc.setText(trip.getStartDate().getDayMonthYear() + " - " + trip.getEndDate().getDayMonthYear());
        }

        itemHolder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("myTag",trip.getName());
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
        headerHolder.headerTitle.setText(title);
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView headerTitle;
        HeaderViewHolder(View view) {
            super(view);
            headerTitle = itemView.findViewById(R.id.header_title);
        }
    }

    private class TripViewHolder extends RecyclerView.ViewHolder {
        private final View rootView;
        private final TextView textName, textDesc;

        public TripViewHolder(View view) {
            super(view);

            rootView = view;

            textName = itemView.findViewById(R.id.text_list_view_item_name);
            textDesc = itemView.findViewById(R.id.text_list_view_item_desc);
        }
    }
}
