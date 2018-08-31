package io.github.dansager.travelplanner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
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
import io.github.dansager.travelplanner.data_structures.Expense;
import io.github.dansager.travelplanner.data_structures.Trip;

public class TripDisplay extends AppCompatActivity {

    public static Trip activeTrip;
    List<Trip> tripList;
    SharedPreferences settings;
    SharedPreferences.Editor prefEditor;
    public static RecyclerView recyclerView;
    public static TripDisplayAdapter adapter;
    public static SharedPreferences pref;
    private static TripDisplay instance;

    CreateExpense pop = new CreateExpense();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;

        setContentView(R.layout.activity_trip_display);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String tripName = getIntent().getStringExtra("value");

        settings = this.getSharedPreferences("Trip_Pref", 0);
        prefEditor = settings.edit();
        final GsonBuilder builder = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeConverter());
        final Gson gson = builder.create();
        String json = settings.getString("Trips", "");
        Type type = new TypeToken<ArrayList<Trip>>(){}.getType();
        tripList = gson.fromJson(json, type);

        for (Trip t : tripList) {
            if (t.getName().equals(tripName)) {
                activeTrip = t;
            }
        }

        FloatingActionButton fa = (FloatingActionButton) findViewById(R.id.create_expense);
        fa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pop.createDialogWindow(TripDisplay.this,activeTrip);
            }
        });



        setCardInfo();

        recyclerView = (RecyclerView) findViewById(R.id.display_recyclerView);
        adapter = new TripDisplayAdapter(this, activeTrip.getList());
        recyclerView.setAdapter(adapter);
    }

    public void setCardInfo () {
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        final Boolean dateFormat = pref.getBoolean("pref_app_date_format",false);   //Default/false = mm/dd/yyyy

        TextView displayName, displayStartDate, displayEndDate, displayCost;

        displayName = (TextView) findViewById(R.id.display_name);
        displayStartDate = (TextView) findViewById(R.id.display_start_name);
        displayEndDate = (TextView) findViewById(R.id.display_end_name);
        displayCost = (TextView) findViewById(R.id.display_cost);

        displayName.setText(activeTrip.getName());

        if (dateFormat) {
            displayStartDate.setText(activeTrip.getStartDate().toString("d MMM, yyyy"));
            displayEndDate.setText(activeTrip.getEndDate().toString("d MMM, yyyy"));
        } else {
            displayStartDate.setText(activeTrip.getStartDate().toString("MMM d, yyyy"));
            displayEndDate.setText(activeTrip.getEndDate().toString("MMM d, yyyy"));
        }

        String s = Integer.toString(activeTrip.getMoneySpent());
        String currency = pref.getString("pref_app_currency","Default");
        if (currency.equals("USD")) {
            displayCost.setText("$" + s);
        } else if (currency.equals("CAD")) {
            displayCost.setText("$" + s + " CAD");
        } else if (currency.equals("GBP")) {
            displayCost.setText("£" + s);
        } else if (currency.equals("EUR")) {
            displayCost.setText("€" + s);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public static void updateAdapter (List<Trip> updatedList, Trip updatedTrip) {
        activeTrip = updatedTrip;

        adapter.notifyDataSetChanged();

        pref =  PreferenceManager.getDefaultSharedPreferences(instance);
        TextView displayCost = (TextView) instance.findViewById(R.id.display_cost);
        String s = Integer.toString(activeTrip.getMoneySpent());
        String currency = pref.getString("pref_app_currency","Default");
        if (currency.equals("USD")) {
            displayCost.setText("$" + s);
        } else if (currency.equals("CAD")) {
            displayCost.setText("$" + s + " CAD");
        } else if (currency.equals("GBP")) {
            displayCost.setText("£" + s);
        } else if (currency.equals("EUR")) {
            displayCost.setText("€" + s);
        }
    }
}
