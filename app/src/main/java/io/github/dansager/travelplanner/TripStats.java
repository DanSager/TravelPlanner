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
import org.joda.time.Hours;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.github.dansager.travelplanner.data_structures.DateTimeConverter;
import io.github.dansager.travelplanner.data_structures.ExchangeRate;
import io.github.dansager.travelplanner.data_structures.Expense;
import io.github.dansager.travelplanner.data_structures.Trip;

import static io.github.dansager.travelplanner.TripDisplay.round;

public class TripStats extends AppCompatActivity {

    ExchangeRate ER = new ExchangeRate();

    Trip activeTrip;
    String name = "";
    String currency = "";
    String currencySymbol = "";
    Double cost, costTrans, costAccom, costExcurs, costFood, costOther, costPerDay;
    int numTrans, numAccom, numExcurs, numFood, numOther, numDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.trip_stats);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String tripName = getIntent().getStringExtra("value");

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
        Type type = new TypeToken<ArrayList<Trip>>() {
        }.getType();
        List<Trip> tripList = gson.fromJson(json, type);

        for (Trip t : tripList) {
            if (t.getName().equals(tripName)) {
                activeTrip = t;
            }
        }

        cost = costTrans = costAccom = costExcurs = costFood = costOther = 0.0;
        numTrans = numAccom = numExcurs = numFood = numOther = numDays = 0;

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
        name = activeTrip.getName();
        cost = round(activeTrip.getMoneySpent(),2);

        for (Expense e : activeTrip.getList()) {
            switch (e.getType()) {
                case "Transportation": costTrans = round((costTrans + e.getCost()),2); numTrans++; break;
                case "Accommodation": costAccom = round((costAccom + e.getCost()),2); 
                    numAccom = numAccom + Days.daysBetween(e.getStartDate().withTimeAtStartOfDay(),e.getEndDate().withTimeAtStartOfDay()).getDays(); break;
                case "Excursion": costExcurs = round((costExcurs + e.getCost()),2); numExcurs++; break;
                case "Food": costFood = round((costFood + e.getCost()),2); numFood++; break;
                case "Other": costOther = round((costOther + e.getCost()),2); numOther++; break;
            }
        }

        int hours = Hours.hoursBetween(activeTrip.getStartDate(),activeTrip.getEndDate()).getHours();
        numDays = hours/24 + 1;
        costPerDay = cost/numDays;
    }

    private void setActivity () {
        TextView nameView = findViewById(R.id.stats_trip_name);
        nameView.setText(name);

        TextView costView = findViewById(R.id.stats_trip_total_cost);
        TextView numDaysView = findViewById(R.id.stats_trip_total_days);
        TextView costPerDayView = findViewById(R.id.stats_trip_total_average);
        numDaysView.setText("Days: " + Integer.toString(numDays));
        switch (currency) {
            case "USD":
                costView.setText("Cost: " + currencySymbol + round(cost,2));
                costPerDayView.setText("Average Cost Per Day: " + currencySymbol + round(costPerDay,2));
                break;
            case "CAD":
                costView.setText("Cost: " + currencySymbol + round((cost * ER.getUSDtoCAD()),2) + " CAD");
                costPerDayView.setText("Average Cost Per Day: " + currencySymbol + round((costPerDay * ER.getUSDtoCAD()),2) + " CAD");
                break;
            case "GBP":
                costView.setText("Cost: " + currencySymbol + round((cost * ER.getUSDtoGBP()),2));
                costPerDayView.setText("Average Cost Per Day: " + currencySymbol + round((costPerDay * ER.getUSDtoGBP()),2));
                break;
            case "EUR":
                costView.setText("Cost: " + currencySymbol + round((cost * ER.getUSDtoEUR()),2));
                costPerDayView.setText("Average Cost Per Day: " + currencySymbol + round((costPerDay * ER.getUSDtoEUR()),2));
                break;
        }

        TextView costTransView = findViewById(R.id.stats_trip_trans_cost);
        TextView countTransView = findViewById(R.id.stats_trip_trans_count);
        TextView averageTransView = findViewById(R.id.stats_trip_trans_average);
        countTransView.setText("Count: " + Integer.toString(numTrans));
        switch (currency) {
            case "USD":
                costTransView.setText("Cost: " + currencySymbol + round(costTrans,2));
                averageTransView.setText("Average Cost: " + currencySymbol + round(costTrans/numTrans,2));
                break;
            case "CAD":
                costTransView.setText("Cost: " + currencySymbol + round((costTrans * ER.getUSDtoCAD()),2) + " CAD");
                averageTransView.setText("Average Cost: " + currencySymbol + round((costTrans/numTrans * ER.getUSDtoCAD()),2) + " CAD");
                break;
            case "GBP":
                costTransView.setText("Cost: " + currencySymbol + round((costTrans * ER.getUSDtoGBP()),2));
                averageTransView.setText("Average Cost: " + currencySymbol + round((costTrans/numTrans * ER.getUSDtoGBP()),2));
                break;
            case "EUR":
                costTransView.setText("Cost: " + currencySymbol + round((costTrans * ER.getUSDtoEUR()),2));
                averageTransView.setText("Average Cost: " + currencySymbol + round((costTrans/numTrans * ER.getUSDtoEUR()),2));
                break;
        }

        TextView costAccomView = findViewById(R.id.stats_trip_accom_cost);
        TextView countAccomView = findViewById(R.id.stats_trip_accom_count);
        TextView averageAccomView = findViewById(R.id.stats_trip_accom_average);
        countAccomView.setText("Number of Nights: " + Integer.toString(numAccom));
        switch (currency) {
            case "USD":
                costAccomView.setText("Cost: " + currencySymbol + round(costAccom,2));
                averageAccomView.setText("Average Cost per Night: " + currencySymbol + round(costAccom/numAccom,2));
                break;
            case "CAD":
                costAccomView.setText("Cost: " + currencySymbol + round((costAccom * ER.getUSDtoCAD()),2) + " CAD");
                averageAccomView.setText("Average Cost per Night: " + currencySymbol + round((costAccom/numAccom * ER.getUSDtoCAD()),2) + " CAD");
                break;
            case "GBP":
                costAccomView.setText("Cost: " + currencySymbol + round((costAccom * ER.getUSDtoGBP()),2));
                averageAccomView.setText("Average Cost per Night: " + currencySymbol + round((costAccom/numAccom * ER.getUSDtoGBP()),2));
                break;
            case "EUR":
                costAccomView.setText("Cost: " + currencySymbol + round((costAccom * ER.getUSDtoEUR()),2));
                averageAccomView.setText("Average Cost per Night: " + currencySymbol + round((costAccom/numAccom * ER.getUSDtoEUR()),2));
                break;
        }
        
        TextView costExcursView = findViewById(R.id.stats_trip_excurs_cost);
        TextView countExcursView = findViewById(R.id.stats_trip_excurs_count);
        TextView averageExcursView = findViewById(R.id.stats_trip_excurs_average);
        countExcursView.setText("Count: " + Integer.toString(numExcurs));
        switch (currency) {
            case "USD":
                costExcursView.setText("Cost: " + currencySymbol + round(costExcurs,2));
                averageExcursView.setText("Average Cost: " + currencySymbol + round(costExcurs/numExcurs,2));
                break;
            case "CAD":
                costExcursView.setText("Cost: " + currencySymbol + round((costExcurs * ER.getUSDtoCAD()),2) + " CAD");
                averageExcursView.setText("Average Cost: " + currencySymbol + round((costExcurs/numExcurs * ER.getUSDtoCAD()),2) + " CAD");
                break;
            case "GBP":
                costExcursView.setText("Cost: " + currencySymbol + round((costExcurs * ER.getUSDtoGBP()),2));
                averageExcursView.setText("Average Cost: " + currencySymbol + round((costExcurs/numExcurs * ER.getUSDtoGBP()),2));
                break;
            case "EUR":
                costExcursView.setText("Cost: " + currencySymbol + round((costExcurs * ER.getUSDtoEUR()),2));
                averageExcursView.setText("Average Cost: " + currencySymbol + round((costExcurs/numExcurs * ER.getUSDtoEUR()),2));
                break;
        }

        TextView costFoodView = findViewById(R.id.stats_trip_food_cost);
        TextView countFoodView = findViewById(R.id.stats_trip_food_count);
        TextView averageFoodView = findViewById(R.id.stats_trip_food_average);
        countFoodView.setText("Count: " + Integer.toString(numFood));
        switch (currency) {
            case "USD":
                costFoodView.setText("Cost: " + currencySymbol + round(costFood,2));
                averageFoodView.setText("Average Cost: " + currencySymbol + round(costFood/numFood,2));
                break;
            case "CAD":
                costFoodView.setText("Cost: " + currencySymbol + round((costFood * ER.getUSDtoCAD()),2) + " CAD");
                averageFoodView.setText("Average Cost: " + currencySymbol + round((costFood/numFood * ER.getUSDtoCAD()),2) + " CAD");
                break;
            case "GBP":
                costFoodView.setText("Cost: " + currencySymbol + round((costFood * ER.getUSDtoGBP()),2));
                averageFoodView.setText("Average Cost: " + currencySymbol + round((costFood/numFood * ER.getUSDtoGBP()),2));
                break;
            case "EUR":
                costFoodView.setText("Cost: " + currencySymbol + round((costFood * ER.getUSDtoEUR()),2));
                averageFoodView.setText("Average Cost: " + currencySymbol + round((costFood/numFood * ER.getUSDtoEUR()),2));
                break;
        }

        TextView costOtherView = findViewById(R.id.stats_trip_other_cost);
        TextView countOtherView = findViewById(R.id.stats_trip_other_count);
        TextView averageOtherView = findViewById(R.id.stats_trip_other_average);
        countOtherView.setText("Count: " + Integer.toString(numOther));
        switch (currency) {
            case "USD":
                costOtherView.setText("Cost: " + currencySymbol + round(costOther,2));
                averageOtherView.setText("Average Cost: " + currencySymbol + round(costOther/numOther,2));
                break;
            case "CAD":
                costOtherView.setText("Cost: " + currencySymbol + round((costOther * ER.getUSDtoCAD()),2) + " CAD");
                averageOtherView.setText("Average Cost: " + currencySymbol + round((costOther/numOther * ER.getUSDtoCAD()),2) + " CAD");
                break;
            case "GBP":
                costOtherView.setText("Cost: " + currencySymbol + round((costOther * ER.getUSDtoGBP()),2));
                averageOtherView.setText("Average Cost: " + currencySymbol + round((costOther/numOther * ER.getUSDtoGBP()),2));
                break;
            case "EUR":
                costOtherView.setText("Cost: " + currencySymbol + round((costOther * ER.getUSDtoEUR()),2));
                averageOtherView.setText("Average Cost: " + currencySymbol + round((costOther/numOther * ER.getUSDtoEUR()),2));
                break;
        }
    }
}