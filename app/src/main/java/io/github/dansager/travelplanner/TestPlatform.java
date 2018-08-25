package io.github.dansager.travelplanner;

import io.github.dansager.travelplanner.data_structures.Trip;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class TestPlatform extends AppCompatActivity {

    int tripListCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_platform);

        ListView testListView = (ListView) findViewById(R.id.listView);

        CustomAdapter ca = new CustomAdapter();

        testListView.setAdapter(ca);
    }

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            SharedPreferences settings = getSharedPreferences("Trip_Pref", 0);
            Gson gson = new Gson();
            String json = settings.getString("Trips", "");

            Type type = new TypeToken<List<Trip>>(){}.getType();
            List<Trip> tripList = gson.fromJson(json, type);

            if (tripList == null) {
                return 0;
            }

            return tripList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            view = getLayoutInflater().inflate(R.layout.list_view_items,null);

            SharedPreferences settings = getSharedPreferences("Trip_Pref", 0);
            Gson gson = new Gson();
            String json = settings.getString("Trips", "");

            Type type = new TypeToken<List<Trip>>(){}.getType();
            List<Trip> tripList = gson.fromJson(json, type);

            TextView nameText = (TextView) view.findViewById(R.id.text_list_view_item_name);
            TextView descText = (TextView) view.findViewById(R.id.text_list_view_item_desc);

            nameText.setText(tripList.get(i).getName());

            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(TestPlatform.this);
            final Boolean dateFormat = pref.getBoolean("pref_app_date_format",false);   //Default/false = mm/dd/yyyy
            if (dateFormat == false) {
                descText.setText(tripList.get(i).getStartDate().getMonthDayYear() + " - " + tripList.get(i).getEndDate().getMonthDayYear());
            } else {
                descText.setText(tripList.get(i).getStartDate().getDayMonthYear() + " - " + tripList.get(i).getEndDate().getDayMonthYear());
            }

            return view;
        }
    }
}