package io.github.dansager.travelplanner;

import io.github.dansager.travelplanner.data_structures.Trip;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class TestPlatform extends AppCompatActivity {

    private static final String TAG = "TestPlat";
    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    Trip firstTrip = new Trip();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        themeSelector();
        setContentView(R.layout.activity_test_platform);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        final Boolean dateFormat = pref.getBoolean("pref_app_date_format",false);               //Default/false = mm/dd/yyyy
        Toast.makeText(TestPlatform.this,dateFormat.toString(),Toast.LENGTH_SHORT).show();

        mDisplayDate = (TextView) findViewById(R.id.start_date);

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        TestPlatform.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                if (dateFormat == false) {
                    Toast.makeText(TestPlatform.this, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TestPlatform.this, "onDateSet: dd/mm/yyy: " + day + "/" + month + "/" + year, Toast.LENGTH_SHORT).show();
                }


                Date testStartDate = new Date(year,month,day);
                if (firstTrip.getStartDate() == null) {
                    firstTrip.setStartDate(testStartDate);
                } else {
                    firstTrip.setEndDate(testStartDate);
                    if (testStartDate.after(firstTrip.getStartDate())) {
                        Toast.makeText(TestPlatform.this,"DATES IN CORRECT ORDER", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(TestPlatform.this,"DATES IN WRONG ORDER", Toast.LENGTH_SHORT).show();
                    }


                }


                String date = month + "/" + day + "/" + year;

            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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


/*


public void testTrip() {

        t.setName("Spain");
        t.setMoneySpent(120);
        t.setStartDate(datee);

        Log.i("myTag", "Name: " + t.getName() + ", Money Lost: " + t.getMoneySpent() + ", Date Start: " + t.getStartDate());


    }
 */