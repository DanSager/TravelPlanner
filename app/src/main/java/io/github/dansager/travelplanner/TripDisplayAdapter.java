package io.github.dansager.travelplanner;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.github.dansager.travelplanner.data_structures.DateTimeConverter;
import io.github.dansager.travelplanner.data_structures.ExchangeRate;
import io.github.dansager.travelplanner.data_structures.Expense;
import io.github.dansager.travelplanner.data_structures.Trip;

import static io.github.dansager.travelplanner.TripDisplay.round;

public class TripDisplayAdapter extends RecyclerView.Adapter<TripDisplayAdapter.ViewHolder> {

    private List<Expense> data;
    private Trip trip;
    private Context context;
    private LayoutInflater mInflater;

    ExchangeRate ER = new ExchangeRate();

    TripDisplayAdapter(Context context, List<Expense> data, Trip trip) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.data = data;
        this.trip = trip;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycler_expense_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        final Boolean dateFormat = pref.getBoolean("pref_app_date_format",false);

        final Expense e = data.get(position);
        final TripDisplayAdapter.ViewHolder itemHolder = holder;

        String s = Double.toString(e.getCost());
        itemHolder.textName.setText(e.getName());
        itemHolder.textMoney.setText(s);
        switch (e.getCurrency()) {
            case "USD": itemHolder.textMoney.setText("$" + Double.toString(round((e.getCost()),2))); break;
            case "CAD": itemHolder.textMoney.setText("$" + Double.toString(round((e.getCost() * ER.getUSDtoCAD()),2)) + " CAD"); break;
            case "GBP": itemHolder.textMoney.setText("£" + Double.toString(round((e.getCost() * ER.getUSDtoGBP()),2))); break;
            case "EUR": itemHolder.textMoney.setText("€" + Double.toString(round((e.getCost() * ER.getUSDtoEUR()),2))); break;
        }

        if (e.getEndDate() != null) {
            if (e.getStartDate().toString(("M/d/yyyy")).equals(e.getEndDate().toString(("M/d/yyyy")))) {
                if (dateFormat) {
                    itemHolder.textDesc.setText(e.getStartDate().toString("d/M/yyyy"));
                } else {
                    itemHolder.textDesc.setText(e.getStartDate().toString("M/d/yyyy"));
                }
            } else {
                if (dateFormat) {
                    itemHolder.textDesc.setText(e.getStartDate().toString("d/M/yyyy") + " - " + e.getEndDate().toString("d/M/yyyy"));
                } else {
                    itemHolder.textDesc.setText(e.getStartDate().toString("M/d/yyyy") + " - " + e.getEndDate().toString("M/d/yyyy"));
                }
            }
            itemHolder.textTimeEnd.setVisibility(View.VISIBLE);
            itemHolder.textTime.setText("Start time " + e.getStartDate().toString("H:mm"));
            itemHolder.textTimeEnd.setText("End time " + e.getEndDate().toString("H:mm"));

        } else {
            if (dateFormat) {
                itemHolder.textDesc.setText(e.getStartDate().toString("d/M/yyyy"));
            } else {
                itemHolder.textDesc.setText(e.getStartDate().toString("M/d/yyyy"));
            }
            itemHolder.textTime.setText("Start time " + e.getStartDate().toString("H:mm"));
        }

        itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences settings = context.getSharedPreferences("Trip_Pref", 0);
                SharedPreferences.Editor prefEditor = settings.edit();
                boolean delete = settings.getBoolean("delete",false);
                if (delete) {
                    final GsonBuilder builder = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeConverter());
                    final Gson gson = builder.create();
                    String json = settings.getString("Trips", "");
                    Type type = new TypeToken<ArrayList<Trip>>(){}.getType();
                    List<Trip> tripList = gson.fromJson(json, type);
                    for (Trip t : tripList) {
                        if (t.getName().equals(trip.getName())) {
                            for (Expense expense : t.getList()) {
                                if (expense.getName().equals(e.getName())) {
                                    t.getList().remove(expense);
                                    t.setMoneySpent(t.getMoneySpent() - e.getCost());
                                    json = gson.toJson(tripList);
                                    prefEditor.putString("Trips", json);
                                    prefEditor.commit();
                                    MainActivity.updateAdapter(tripList);
                                    TripDisplay.updateAdapter(tripList,t);
                                    Toast.makeText(context,"Reopen Trip to Update Expenses",Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }
                        }
                    }

                }
                delete = false;
                prefEditor.putBoolean("delete",delete);
                prefEditor.commit();
            }
        });

        switch (e.getType()) {
            case "Transportation":
                itemHolder.divider.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.popupone));
                break;
            case "Accommodation":
                itemHolder.divider.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.popupfour));
                break;
            case "Excursion":
                itemHolder.divider.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.popupthree));
                break;
            case "Food":
                itemHolder.divider.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.popupfive));
                break;
            case "Other":
                itemHolder.divider.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.popupsix));
                break;
        }

        if (!e.getTypeSpecific().equals("")) {
            itemHolder.infoImage.setVisibility(View.VISIBLE);
            itemHolder.textSpecific.setVisibility(View.VISIBLE);
            itemHolder.textSpecific.setText(e.getTypeSpecific());
        }
    }

    @Override
    public int getItemCount() {
        if (data == null) {
            return 0;
        }
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final View divider;
        private final ImageView infoImage;
        private final TextView textName, textDesc, textTime, textTimeEnd, textMoney, textSpecific;

        ViewHolder(View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.expense_recycler_item_name);
            textDesc = itemView.findViewById(R.id.expense_recycler_item_desc);
            textTime = itemView.findViewById(R.id.expense_recycler_item_time);
            textTimeEnd = itemView.findViewById(R.id.expense_recycler_item_time_end);
            textMoney = itemView.findViewById(R.id.expense_recycler_money_spent);
            divider = itemView.findViewById(R.id.expense_recycler_divider);
            infoImage = itemView.findViewById(R.id.expense_recycler_info);
            textSpecific = itemView.findViewById(R.id.expense_recycler_type_specific);
        }
    }
}
