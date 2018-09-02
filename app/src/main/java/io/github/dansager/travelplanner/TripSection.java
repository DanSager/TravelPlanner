package io.github.dansager.travelplanner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.LocalDate;

import java.util.List;

import io.github.dansager.travelplanner.data_structures.ExchangeRate;
import io.github.dansager.travelplanner.data_structures.Trip;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

public class TripSection extends StatelessSection {

    public static Context mCtx;
    List<Trip> list;
    String title;

    ExchangeRate ER = new ExchangeRate();

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

        //SETS THE TRIP NAME TEXT
        itemHolder.textName.setText(trip.getName());

        //SETS THE TRIP START AND END DAY TEXT
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mCtx);
        final Boolean dateFormat = pref.getBoolean("pref_app_date_format",false);   //Default/false = mm/dd/yyyy
        if (dateFormat) {
            itemHolder.textDesc.setText(trip.getStartDate().toString("d MMM, yyyy") + " - " + trip.getEndDate().toString("d MMM, yyyy"));
        } else {
            itemHolder.textDesc.setText(trip.getStartDate().toString("MMM d, yyyy") + " - " + trip.getEndDate().toString("MMM d, yyyy"));
        }

        //SETS THE TIME SINCE/UNTIL TEXT && DIVIDER COLOR
        DateTime now = new DateTime();
        if (title.equals("Upcoming")) {
            itemHolder.divider.setBackgroundTintList(ContextCompat.getColorStateList(mCtx, R.color.colorAccent));
            int hours = Hours.hoursBetween(now,trip.getStartDate()).getHours();
            int days = hours/24 +1;
            if (hours < 24) {
                String s = Integer.toString(hours);
                itemHolder.textTime.setText(s + "h");
            } else if (days < 7) {
                String s = Integer.toString(days);
                itemHolder.textTime.setText(s + "d");
            } else {
                int weeks = days/7;
                String s = Integer.toString(weeks);
                itemHolder.textTime.setText(s + "w");
            }
        } else if (title.equals("Previous")) {
            itemHolder.divider.setBackgroundTintList(ContextCompat.getColorStateList(mCtx, R.color.colorPrimaryDark));
            int hours = Hours.hoursBetween(trip.getEndDate(),now).getHours();
            int days = hours/24 +1;
            if (hours < 24) {
                String s = Integer.toString(hours);
                itemHolder.textTime.setText("-" + s + "H");
            } else if (days < 7) {
                String s = Integer.toString(days);
                itemHolder.textTime.setText("-" + s + "D");
            } else {
                int weeks = days/7;
                String s = Integer.toString(weeks);
                itemHolder.textTime.setText("-" + s + "W");
            }
        } else {
            itemHolder.divider.setBackgroundTintList(ContextCompat.getColorStateList(mCtx, R.color.colorPrimary));
            itemHolder.textTime.setText("");
        }

        //SETS THE DURATION OF THE TRIP
        int hours = Hours.hoursBetween(trip.getStartDate(),trip.getEndDate()).getHours();
        int days = hours/24 + 1;
        int weeks = days/7;
        String h = Integer.toString(hours);
        String d = Integer.toString(days);
        String w = Integer.toString(weeks);
        if (hours < 24) {
            itemHolder.textDuration.setText(h + "H");
        } else if (days < 7) {
            itemHolder.textDuration.setText(d + "D" );
        } else {
            days = days-(weeks*7);
            d = Integer.toString(days);
            if (days == 0) {
                itemHolder.textDuration.setText(w + "W");
            } else {
                itemHolder.textDuration.setText(w + "W " + d + "D");
            }
        }

        //SETS THE MONEY SPENT TEXT OR MAKES IT AND DIVIDER INVISIBLE
        if (trip.getMoneySpent() == 0) {
            itemHolder.moneyImage.setVisibility(View.GONE);
            itemHolder.textMoney.setVisibility(View.GONE);
        } else {
            String s = Double.toString(trip.getMoneySpent());
            String currency = pref.getString("pref_app_currency","Default");
            switch (currency) {
                case "USD": itemHolder.textMoney.setText("$" + Double.toString(TripDisplay.round(trip.getMoneySpent(),2))); break;
                case "CAD": itemHolder.textMoney.setText("$" + Double.toString(TripDisplay.round((trip.getMoneySpent() * ER.getUSDtoCAD()),2)) + " CAD"); break;
                case "GBP": itemHolder.textMoney.setText("£" + Double.toString(TripDisplay.round((trip.getMoneySpent() * ER.getUSDtoGBP()),2))); break;
                case "EUR": itemHolder.textMoney.setText("€" + Double.toString(TripDisplay.round((trip.getMoneySpent() * ER.getUSDtoEUR()),2))); break;
            }
        }

        itemHolder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mCtx,TripDisplay.class);
                intent.putExtra("value",trip.getName());
                mCtx.startActivity(intent);
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

        if (list.size() > 1) {
            String size = Integer.toString(list.size());
            headerHolder.headerCount.setText(size + " Trips");
        } else {
            headerHolder.headerCount.setVisibility(View.GONE);
        }
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView headerTitle, headerCount;
        HeaderViewHolder(View view) {
            super(view);
            headerTitle = itemView.findViewById(R.id.header_title);
            headerCount = itemView.findViewById(R.id.header_count);
        }
    }

    private class TripViewHolder extends RecyclerView.ViewHolder {
        private final View rootView, divider;
        private final TextView textName, textDesc, textTime, textMoney, textDuration;
        private final ImageView moneyImage;

        public TripViewHolder(View view) {
            super(view);

            rootView = view;
            divider = itemView.findViewById(R.id.recycler_divider);

            textName = itemView.findViewById(R.id.text_recycler_item_name);
            textDesc = itemView.findViewById(R.id.text_recycler_item_desc);
            textTime = itemView.findViewById(R.id.text_recycler_item_time);
            textMoney = itemView.findViewById(R.id.text_recycler_money_spent);
            textDuration = itemView.findViewById(R.id.text_recycler_duration);
            moneyImage = itemView.findViewById(R.id.recycler_money);
        }
    }
}
