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

public class SettingActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    //MainActivity mainA = new MainActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
        PreferenceManager.setDefaultValues(this, R.xml.preference, false);
        initSummary(getPreferenceScreen());
        themeSelector();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)  {
        updatePrefSummary(findPreference(key));

        if (key.equals("pref_app_color")) {
            themeSelector();
            Intent intent = new Intent(this, SettingActivity.class);
            finish();
            this.overridePendingTransition(0, 0);
            startActivity(intent);
        }
    }

    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        finish();
        startActivity(intent);
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
}
