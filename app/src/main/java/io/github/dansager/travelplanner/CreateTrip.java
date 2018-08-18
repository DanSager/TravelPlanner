package io.github.dansager.travelplanner;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;

public class CreateTrip {

    private DatePickerDialog.OnDateSetListener startDateTextListener;
    private DatePickerDialog.OnDateSetListener endDateTextListener;
    private TextView startDateText;
    private TextView endDateText;
    private Date tripStartDate;
    private Date tripEndDate;

    public void createDialogWindow (final Context context) {
        Dialog create_window = new Dialog(context);

        create_window.setContentView(R.layout.create_trip_popup);
        create_window.getWindow();
        create_window.show();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        final Boolean dateFormat = pref.getBoolean("pref_app_date_format",false);   //Default/false = mm/dd/yyyy

        startDateText = (TextView) create_window.findViewById(R.id.create_start_date);
        startDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(context,android.R.style.Theme_Holo_Light_Dialog_MinWidth, startDateTextListener, year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow();
                dialog.show();
            }
        });

        startDateTextListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;

                String date;
                if (dateFormat == false) {
                    date = month + "/" + day + "/" + year;
                } else {
                    date = day + "/" + month + "/" + year;
                }
                startDateText.setText(date);
                tripStartDate = new Date(year,month,day);
            }
        };

        endDateText = (TextView) create_window.findViewById(R.id.create_end_date);
        endDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(context,android.R.style.Theme_Holo_Light_Dialog_MinWidth, endDateTextListener, year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow();
                dialog.show();
            }
        });

        endDateTextListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;

                String date;
                if (dateFormat == false) {
                    date = month + "/" + day + "/" + year;
                } else {
                    date = day + "/" + month + "/" + year;
                }
                endDateText.setText(date);
                tripEndDate = new Date(year,month,day);
            }
        };

        cancelButtonListener(context,create_window);
        createButtonListener(context,create_window);

    }

    private void cancelButtonListener (final Context context, final Dialog create_window) {
        Button cancelButton = (Button) create_window.findViewById(R.id.create_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                create_window.dismiss();
            }
        });
    }

    private void createButtonListener (final Context context, final Dialog create_window) {
        Button createButton = (Button) create_window.findViewById(R.id.create_create_button);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText tripName = (EditText) create_window.findViewById(R.id.create_trip_name);
                String name = tripName.getText().toString();

                if (name.equals("")) {
                    Toast.makeText(context, "Invalid Name", Toast.LENGTH_SHORT).show();
                } else if ((tripStartDate == null) || (tripEndDate == null)) {
                    Toast.makeText(context, "Missing Date", Toast.LENGTH_SHORT).show();
                } else if (tripEndDate.before(tripStartDate)){
                    Toast.makeText(context, "End Date Can't Be Before Start Date", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context,"Trip Name: " + name + ", Trip Start Date: " + tripStartDate.getMonth() + "/" + tripStartDate.getDate() + "/" + tripStartDate.getYear() + ", Trip End Date: " + tripEndDate.getMonth() + "/" + tripEndDate.getDate() + "/" + tripEndDate.getYear(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
