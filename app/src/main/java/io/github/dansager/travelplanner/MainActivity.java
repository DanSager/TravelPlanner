package io.github.dansager.travelplanner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.dansager.travelplanner.data_structures.ListComparator;
import io.github.dansager.travelplanner.data_structures.Trip;

public class MainActivity extends AppCompatActivity {

    CreateTrip pop = new CreateTrip();

    public static List<Trip> tripList;
    public static RecyclerView recyclerView;

    public static TripAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        themeSelector();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences settings = getSharedPreferences("Trip_Pref", 0);
        Gson gson = new Gson();
        String json = settings.getString("Trips", "");

        Type type = new TypeToken<List<Trip>>(){}.getType();
        tripList = gson.fromJson(json, type);

        if (tripList == null) {
            tripList = new ArrayList<Trip>();
        }

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TripAdapter(this, tripList);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.create_new_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pop.createDialogWindow(MainActivity.this);
            }
        });
    }

    @Override
    public void onResume() {  // After a pause OR at startup
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_stats) {
            Toast.makeText(this, "WIP",Toast.LENGTH_LONG).show();
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.action_test_platform) {
            Intent intent = new Intent(MainActivity.this, TestPlatform.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void themeSelector() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String color = pref.getString("pref_app_color","Default");
        if (color.equals("Teal")) {
            setTheme(R.style.AppTheme);
        } else if (color.equals("Red")) {
            setTheme(R.style.RedStyle);
        } else if (color.equals("Orange")) {
            setTheme(R.style.OrangeStyle);
        } else if (color.equals("Yellow")) {
            setTheme(R.style.YellowStyle);
        } else if (color.equals("Green")) {
            setTheme(R.style.GreenStyle);
        } else if (color.equals("Blue")) {
            setTheme(R.style.BlueStyle);
        } else if (color.equals("Purple")) {
            setTheme(R.style.PurpleStyle);
        }
    }

    public static void updateAdapter (List<Trip> updatedListTrip) {
        tripList.clear();
        tripList.addAll(updatedListTrip);
        adapter.notifyDataSetChanged();
    }
}