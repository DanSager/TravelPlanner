package io.github.dansager.travelplanner;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.github.dansager.travelplanner.data_structures.DateTimeConverter;
import io.github.dansager.travelplanner.data_structures.ExchangeRate;
import io.github.dansager.travelplanner.data_structures.Expense;
import io.github.dansager.travelplanner.data_structures.Trip;

import static io.github.dansager.travelplanner.TripDisplay.round;

public class OverallStats extends AppCompatActivity {

    ExchangeRate ER = new ExchangeRate();

    String currency = "";
    String currencySymbol = "";

    int tripCount, amountOfDays;
    double totalCost, averageCost, costPerDay, costOfTransportation, costOfAccommodation;

    List<Trip> tripList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.overall_stats);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences settings = this.getSharedPreferences("Trip_Pref", 0);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        currency = pref.getString("pref_app_currency","Default");
        switch (currency) {
            case "USD": currencySymbol = "$"; break;
            case "CAD": currencySymbol = "$"; break;
            case "GBP": currencySymbol = "€"; break;
            case "EUR": currencySymbol = "$"; break;
        }

        final GsonBuilder builder = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeConverter());
        final Gson gson = builder.create();
        String json = settings.getString("Trips", "");
        Type type = new TypeToken<ArrayList<Trip>>() {   }.getType();
        tripList = gson.fromJson(json, type);

        tripCount = amountOfDays = 0;
        totalCost = averageCost = costPerDay = costOfTransportation = costOfAccommodation = 0.0;

        obtainValues();
        setActivity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void obtainValues () {
        for (Trip t : tripList) {
            tripCount++;
            totalCost = totalCost + round(t.getMoneySpent(),2);
            amountOfDays = amountOfDays + (Days.daysBetween(t.getStartDate().withTimeAtStartOfDay(),t.getEndDate().withTimeAtStartOfDay() ).getDays() +1);
            for (Expense e : t.getList()) {
                if (e.getType().equals("Transportation")) {
                    costOfTransportation = costOfTransportation + e.getCost();
                } else if (e.getType().equals("Accommodation")) {
                    costOfAccommodation = costOfAccommodation + e.getCost();
                }
            }
        }

        averageCost = round(totalCost/tripCount,2);
        costPerDay = round(totalCost/amountOfDays,2);
    }

    private void setActivity () {
        TextView numOfTrips = findViewById(R.id.stats_overall_count_value);
        numOfTrips.setText(Integer.toString(tripCount));

        TextView costOfTrips = findViewById(R.id.stats_overall_cost_value);
        TextView averageCostOfTrips = findViewById(R.id.stats_overall_average_cost_value);
        TextView numOfDays = findViewById(R.id.stats_overall_days_value);
        TextView averageCostPerDay = findViewById(R.id.stats_overall_average_cost_day_value);
        TextView costTrans = findViewById(R.id.stats_overall_transportation_cost_value);
        TextView costAccom = findViewById(R.id.stats_overall_accom_cost_value);

        numOfDays.setText(Integer.toString(amountOfDays));
        switch (currency) {
            case "USD":
                costOfTrips.setText(currencySymbol + Double.toString(round(totalCost,2)));
                averageCostOfTrips.setText(currencySymbol + Double.toString(round(averageCost,2)));
                averageCostPerDay.setText(currencySymbol + Double.toString(round(costPerDay,2)));
                costTrans.setText(currencySymbol + Double.toString(round(costOfTransportation,2)));
                costAccom.setText(currencySymbol + Double.toString(round(costOfAccommodation,2)));
                break;
            case "CAD":
                costOfTrips.setText(currencySymbol + Double.toString(round(totalCost * ER.getUSDtoCAD(),2)) + " CAD");
                averageCostOfTrips.setText(currencySymbol + Double.toString(round(averageCost * ER.getUSDtoCAD(),2)));
                averageCostPerDay.setText(currencySymbol + Double.toString(round(costPerDay * ER.getUSDtoCAD(),2)));
                costTrans.setText(currencySymbol + Double.toString(round(costOfTransportation * ER.getUSDtoCAD(),2)));
                costAccom.setText(currencySymbol + Double.toString(round(costOfAccommodation * ER.getUSDtoCAD(),2)));
                break;
            case "GBP":
                costOfTrips.setText(currencySymbol + Double.toString(round(totalCost * ER.getUSDtoGBP(),2)));
                averageCostOfTrips.setText(currencySymbol + Double.toString(round(averageCost * ER.getUSDtoGBP(),2)));
                averageCostPerDay.setText(currencySymbol + Double.toString(round(costPerDay * ER.getUSDtoGBP(),2)));
                costTrans.setText(currencySymbol + Double.toString(round(costOfTransportation * ER.getUSDtoGBP(),2)));
                costAccom.setText(currencySymbol + Double.toString(round(costOfAccommodation * ER.getUSDtoGBP(),2)));
                break;
            case "EUR":
                costOfTrips.setText(currencySymbol + Double.toString(round(totalCost * ER.getUSDtoEUR(),2)));
                averageCostOfTrips.setText(currencySymbol + Double.toString(round(averageCost * ER.getUSDtoEUR(),2)));
                averageCostPerDay.setText(currencySymbol + Double.toString(round(costPerDay * ER.getUSDtoEUR(),2)));
                costTrans.setText(currencySymbol + Double.toString(round(costOfTransportation * ER.getUSDtoEUR(),2)));
                costAccom.setText(currencySymbol + Double.toString(round(costOfAccommodation * ER.getUSDtoEUR(),2)));
                break;
        }
    }
}
