package io.github.dansager.travelplanner;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import io.github.dansager.travelplanner.data_structures.Trip;

public class MainActivity extends AppCompatActivity {

    //final Date datee = new Date();
    //Trip newTrip = new Trip();
    private DatePickerDialog.OnDateSetListener startDateListener;
    private TextView startDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        themeSelector();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.create_new_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDialogWindow();
            }
        });

    }

    public void createDialogWindow () {
        Dialog create_window = new Dialog(MainActivity.this);

        create_window.setContentView(R.layout.create_trip_popup);
        create_window.getWindow();
        create_window.show();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        final Boolean dateFormat = pref.getBoolean("pref_app_date_format",false);   //Default/false = mm/dd/yyyy

        startDate = (TextView) create_window.findViewById(R.id.create_start_date);
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(MainActivity.this,android.R.style.Theme_Holo_Light_Dialog_MinWidth, startDateListener, year,month,day);
                dialog.getWindow();
                dialog.show();
            }
        });

        startDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                if (dateFormat == false) {
                    Toast.makeText(MainActivity.this, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "onDateSet: dd/mm/yyy: " + day + "/" + month + "/" + year, Toast.LENGTH_SHORT).show();
                }

                String date;
                if (dateFormat == false) {
                    date = month + "/" + day + "/" + year;
                } else {
                    date = day + "/" + month + "/" + year;
                }
                startDate.setText(date);


//                Date testStartDate = new Date(year, month, day);
//                if (newTrip.getStartDate() == null) {
//                    newTrip.setStartDate(testStartDate);
//                } else {
//                    newTrip.setEndDate(testStartDate);
//                    if (testStartDate.after(newTrip.getStartDate())) {
//                        Toast.makeText(MainActivity.this, "DATES IN CORRECT ORDER", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(MainActivity.this, "DATES IN WRONG ORDER", Toast.LENGTH_SHORT).show();
//                    }
//
//
//                }


            }
        };
    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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
}