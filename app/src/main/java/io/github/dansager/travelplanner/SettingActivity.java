package io.github.dansager.travelplanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.github.dansager.travelplanner.data_structures.Trip;

public class SettingActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
        PreferenceManager.setDefaultValues(this, R.xml.preference, false);
        initSummary(getPreferenceScreen());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)  {
        updatePrefSummary(findPreference(key));

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        final Boolean delete_trip = pref.getBoolean("delete_all_trips_pref",false);
        SharedPreferences.Editor prefEdit = pref.edit();

        if(key.equals("delete_all_trips_pref") && delete_trip) {
            SharedPreferences settings = getSharedPreferences("Trip_Pref", 0);
            SharedPreferences.Editor prefEditor = settings.edit();

            List<Trip> tripList = new ArrayList<Trip>();

            Gson gson = new Gson();
            String json = settings.getString("Trips", "");
            json = gson.toJson(tripList);
            prefEditor.putString("Trips", json);
            prefEditor.commit();

            prefEdit.putBoolean("delete_all_trips_pref",false);
            prefEdit.commit();
        }
    }

    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        finish();
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    private void initSummary(Preference p) {
        if (p instanceof PreferenceGroup) {
            PreferenceGroup pGrp = (PreferenceGroup) p;
            for (int i = 0; i < pGrp.getPreferenceCount(); i++) {
                initSummary(pGrp.getPreference(i));
            }
        } else {
            updatePrefSummary(p);
        }
    }

    private void updatePrefSummary(Preference p) {
        if (p instanceof ListPreference) {
            ListPreference listPref = (ListPreference) p;
            p.setSummary(listPref.getEntry());
        }
        if (p instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) p;
            if (p.getTitle().toString().toLowerCase().contains("password"))
            {
                p.setSummary("******");
            } else {
                p.setSummary(editTextPref.getText());
            }
        }
        if (p instanceof MultiSelectListPreference) {
            EditTextPreference editTextPref = (EditTextPreference) p;
            p.setSummary(editTextPref.getText());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }
}
