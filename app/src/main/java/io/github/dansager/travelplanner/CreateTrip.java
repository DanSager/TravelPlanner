package io.github.dansager.travelplanner;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.google.gson.reflect.TypeToken;

import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.github.dansager.travelplanner.data_structures.DateTimeConverter;
import io.github.dansager.travelplanner.data_structures.Trip;

public class CreateTrip {

    MainActivity mainActivity;

    private DatePickerDialog.OnDateSetListener startDateTextListener;
    private DatePickerDialog.OnDateSetListener endDateTextListener;
    private TextView startDateText;
    private TextView endDateText;
    DateTime tripStartDate;
    DateTime tripEndDate;

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
                month++;
                tripStartDate = new DateTime(year,month,day,0,0);

                if (dateFormat == false) {
                    startDateText.setText(tripStartDate.toString("M-d-yyyy"));
                } else {
                    startDateText.setText(tripStartDate.toString("d-M-yyyy"));
                }
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
                month++;
                tripEndDate = new DateTime(year,month,day,23,59);

                if (dateFormat == false) {
                    endDateText.setText(tripEndDate.toString("M-d-yyyy"));
                } else {
                    endDateText.setText(tripEndDate.toString("d-M-yyyy"));
                }
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
                } else if (tripStartDate.isAfter(tripEndDate)){
                    Toast.makeText(context, "End Date Can't Be Before Start Date", Toast.LENGTH_SHORT).show();
                } else {

                    SharedPreferences settings = context.getSharedPreferences("Trip_Pref", 0);
                    SharedPreferences.Editor prefEditor = settings.edit();
                    final GsonBuilder builder = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeConverter());
                    final Gson gson = builder.create();
                    String json = settings.getString("Trips", "");
                    Type type = new TypeToken<ArrayList<Trip>>(){}.getType();
                    List<Trip> tripList = gson.fromJson(json, type);


                    Trip newTrip = new Trip(name,tripStartDate,tripEndDate);



                    if (tripList == null) {
                        tripList = new ArrayList<Trip>();
                    }

                    tripList.add(newTrip);


                    Collections.sort(tripList, new Comparator<Trip>() {
                        @Override
                        public int compare(Trip trip, Trip trip2) {
                            DateTime d1 = new DateTime(trip.getEndDate());
                            DateTime d2 = new DateTime(trip2.getEndDate());
                            return d1.compareTo(d2);
                            
                        }
                    });



                    json = gson.toJson(tripList);

                    prefEditor.putString("Trips", json);
                    prefEditor.commit();

                    create_window.dismiss();
                    //mainActivity.displayTrips();
                    mainActivity.updateAdapter(tripList);
                }
            }
        });
    }
}
