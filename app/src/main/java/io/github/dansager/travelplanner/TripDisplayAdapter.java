package io.github.dansager.travelplanner;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.github.dansager.travelplanner.data_structures.Expense;
import io.github.dansager.travelplanner.data_structures.Trip;

public class TripDisplayAdapter extends RecyclerView.Adapter<TripDisplayAdapter.ViewHolder> {

    private List<Expense> data;
    private Context context;
    private LayoutInflater mInflater;

    TripDisplayAdapter(Context context, List<Expense> data) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycler_expense_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Expense e = data.get(position);
        final TripDisplayAdapter.ViewHolder itemHolder = (TripDisplayAdapter.ViewHolder) holder;

        String s = Integer.toString(e.getCost());
        itemHolder.textName.setText(e.getName());
        itemHolder.textMoney.setText(s);
        itemHolder.textDesc.setText(e.getStartDate().toString("MM/d/yyyy"));
        itemHolder.textDuration.setText(e.getCurrency());
    }

    @Override
    public int getItemCount() {
        if (data == null) {
            return 0;
        }
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textName, textDesc, textTime, textMoney, textDuration;

        ViewHolder(View itemView) {
            super(itemView);
            textName = (TextView) itemView.findViewById(R.id.expense_recycler_item_name);
            textDesc = itemView.findViewById(R.id.expense_recycler_item_desc);
            textTime = itemView.findViewById(R.id.expense_recycler_item_time);
            textMoney = itemView.findViewById(R.id.expense_recycler_money_spent);
            textDuration = itemView.findViewById(R.id.expense_recycler_duration);
        }

    }
}
