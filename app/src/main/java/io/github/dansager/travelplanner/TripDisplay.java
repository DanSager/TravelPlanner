package io.github.dansager.travelplanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.github.dansager.travelplanner.data_structures.DateTimeConverter;
import io.github.dansager.travelplanner.data_structures.ExchangeRate;
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
    ExchangeRate ER = new ExchangeRate();

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

        boolean delete = false;
        prefEditor.putBoolean("delete",delete);
        prefEditor.commit();

        for (Trip t : tripList) {
            if (t.getName().equals(tripName)) {
                activeTrip = t;
            }
        }

        setCardInfo();

        recyclerView = (RecyclerView) findViewById(R.id.display_recyclerView);
        adapter = new TripDisplayAdapter(this, activeTrip.getList(),activeTrip);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trip, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: onBackPressed(); return true;
            case R.id.expense_create: pop.createDialogWindow(TripDisplay.this,activeTrip); return true;
            case R.id.expense_stat:
                Intent intent = new Intent(this,TripStats.class);
                intent.putExtra("value",activeTrip.getName());
                startActivity(intent);
                return true;
            case R.id.expense_delete:
                boolean delete = true;
                prefEditor.putBoolean("delete",delete);
                prefEditor.commit();
                Toast.makeText(instance,"Click On An Expense to Delete It",Toast.LENGTH_LONG).show();
                return true;
            case R.id.expense_delete_trip:
                SharedPreferences settings = this.getSharedPreferences("Trip_Pref", 0);
                SharedPreferences.Editor prefEditor = settings.edit();
                final GsonBuilder builder = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeConverter());
                final Gson gson = builder.create();
                String json = settings.getString("Trips", "");
                Type type = new TypeToken<ArrayList<Trip>>(){}.getType();
                List<Trip> tripList = gson.fromJson(json, type);
                for (Trip t : tripList) {
                    if (t.getName().equals(activeTrip.getName())) {
                        tripList.remove(t);
                        break;
                    }
                }
                json = gson.toJson(tripList);
                prefEditor.putString("Trips", json);
                prefEditor.commit();
                finish();
                MainActivity.updateAdapter(tripList);
        }
        return super.onOptionsItemSelected(item);
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

        String currency = pref.getString("pref_app_currency","Default");
        switch (currency) {
            case "USD": displayCost.setText("$" + Double.toString(round((activeTrip.getMoneySpent()),2))); break;
            case "CAD": displayCost.setText("$" + Double.toString(round((activeTrip.getMoneySpent() * ER.getUSDtoCAD()),2)) + " CAD"); break;
            case "GBP": displayCost.setText("£" + Double.toString(round((activeTrip.getMoneySpent() * ER.getUSDtoGBP()),2))); break;
            case "EUR": displayCost.setText("€" + Double.toString(round((activeTrip.getMoneySpent() * ER.getUSDtoEUR()),2))); break;
        }

        TextView budgetText = findViewById(R.id.display_budget);
        if (round(activeTrip.getBudget(),2) == 0.0 || activeTrip.getBudget() == 0) {
            budgetText.setVisibility(View.GONE);
        } else {
            switch (currency) {
                case "USD": budgetText.setText("$" + Double.toString(round((activeTrip.getBudget()),2))); break;
                case "CAD": budgetText.setText("$" + Double.toString(round((activeTrip.getBudget() * ER.getUSDtoCAD()),2)) + " CAD"); break;
                case "GBP": budgetText.setText("£" + Double.toString(round((activeTrip.getBudget() * ER.getUSDtoGBP()),2))); break;
                case "EUR": budgetText.setText("€" + Double.toString(round((activeTrip.getBudget() * ER.getUSDtoEUR()),2))); break;
            }
        }
    }

    public static void updateAdapter (List<Trip> updatedList, Trip updatedTrip) {
        activeTrip = updatedTrip;

        adapter.notifyDataSetChanged();

        pref =  PreferenceManager.getDefaultSharedPreferences(instance);
        TextView displayCost = (TextView) instance.findViewById(R.id.display_cost);
        String s = Double.toString(activeTrip.getMoneySpent());
        String currency = pref.getString("pref_app_currency","Default");
        switch (currency) {
            case "USD": displayCost.setText("$" + Double.toString(round((activeTrip.getMoneySpent()),2))); break;
            case "CAD": displayCost.setText("$" + Double.toString(round((activeTrip.getMoneySpent() * 1.30475),2)) + " CAD"); break;
            case "GBP": displayCost.setText("£" + Double.toString(round((activeTrip.getMoneySpent() * .771545),2))); break;
            case "EUR": displayCost.setText("€" + Double.toString(round((activeTrip.getMoneySpent() * .860475),2))); break;
        }
        TextView budgetText = instance.findViewById(R.id.display_budget);
        if (activeTrip.getBudget() == 0.0 || activeTrip.getBudget() == 0) {
            budgetText.setVisibility(View.GONE);
        } else {
            switch (currency) {
                case "USD": budgetText.setText("$" + Double.toString(round((activeTrip.getBudget()),2))); break;
                case "CAD": budgetText.setText("$" + Double.toString(round((activeTrip.getBudget() * 1.30475),2)) + " CAD"); break;
                case "GBP": budgetText.setText("£" + Double.toString(round((activeTrip.getBudget() * .771545),2))); break;
                case "EUR": budgetText.setText("€" + Double.toString(round((activeTrip.getBudget() * .860475),2))); break;
            }
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}