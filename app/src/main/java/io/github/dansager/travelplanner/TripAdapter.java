package io.github.dansager.travelplanner;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

import io.github.dansager.travelplanner.data_structures.Trip;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    public static Context mCtx;
    public static List<Trip> tripList;

    public TripAdapter(Context mCtx, List<Trip> tripList) {
        this.mCtx = mCtx;
        this.tripList = tripList;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_view_items, null);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, final int position) {
        final Trip trip = tripList.get(position);

        holder.textName.setText(trip.getName());
        holder.textDesc.setText(trip.getStartDate().getMonthDayYear() + " - " + trip.getEndDate().getMonthDayYear());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mCtx,trip.getName(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    class TripViewHolder extends RecyclerView.ViewHolder {

        TextView textName, textDesc;

        public TripViewHolder(View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.text_list_view_item_name);
            textDesc = itemView.findViewById(R.id.text_list_view_item_desc);
        }
    }

    public void updateTripList(List<Trip> newlist) {
        tripList.clear();
        tripList.addAll(newlist);
        this.notifyDataSetChanged();
    }
}