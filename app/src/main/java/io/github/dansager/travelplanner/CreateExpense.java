package io.github.dansager.travelplanner;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
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
import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.github.dansager.travelplanner.data_structures.DateTimeConverter;
import io.github.dansager.travelplanner.data_structures.Expense;
import io.github.dansager.travelplanner.data_structures.Trip;

public class CreateExpense {

    MainActivity mainActivity;

    Boolean dateFormat;
    Boolean dateSelectFormat;
    Trip trip;

    private DatePickerDialog.OnDateSetListener startDateTextListener;
    private DatePickerDialog.OnDateSetListener endDateTextListener;
    private TimePickerDialog.OnTimeSetListener startTimeTextListener;
    private TimePickerDialog.OnTimeSetListener endTimeTextListener;
    private String expenseType;
    private String expenseTypeSpecific;
    private TextView startDateText;
    private TextView startTimeText;
    private TextView endDateText;
    private TextView endTimeText;
    private boolean setEndDate = false;
    private boolean setTypeSpecific = false;
    DateTime startDate = new DateTime();
    DateTime endDate = new DateTime();
    private String currency;
    private int cost;
    String type = "";

    public void createDialogWindow (final Context context,Trip activeTrip) {
        Dialog create_window = new Dialog(context);
        trip = activeTrip;

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        dateFormat = pref.getBoolean("pref_app_date_format",false);   //Default/false = mm/dd/yyyy
        dateSelectFormat = pref.getBoolean("pref_date_select_format",false); //Default/false = calendar

        create_window.setContentView(R.layout.create_expense_popup);
        create_window.getWindow();
        create_window.show();

        expenseTypeListener(context,create_window);
        expenseTypeSpecificListener(context,create_window);

        expenseStartDateListener(context,create_window);
        expenseStartTimeListener(context,create_window);
        expenseEndDateListener(context,create_window);
        expenseEndTimeListener(context,create_window);

        expenseCurrencyListener(context,create_window);

        cancelButtonListener(context,create_window);
        createButtonListener(context,create_window);
    }

    private void expenseTypeListener (final Context context, final Dialog create_window) {
        TextView createExpenseType = (TextView) create_window.findViewById(R.id.create_expense_type);
        TextView createExpenseTypeSpecifics = (TextView) create_window.findViewById(R.id.create_expense_type_specifics);
        TextView createExpenseEndDate = (TextView) create_window.findViewById(R.id.create_expense_end_date);
        TextView createExpenseEndTime = (TextView) create_window.findViewById(R.id.create_expense_end_time);
        createExpenseType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder buildee = new AlertDialog.Builder(create_window.getContext());
                buildee.setTitle("Activity Type");
                buildee.setItems(R.array.expense_items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String toString = Integer.toString(i);
                        expenseType = toString;
                        switch (i) {
                            case 0:
                                createExpenseType.setText("Transportation");
                                createExpenseTypeSpecifics.setVisibility(View.VISIBLE);
                                createExpenseTypeSpecifics.setText(null);
                                setEndDate = true;
                                setTypeSpecific = true;
                                createExpenseEndDate.setVisibility(View.VISIBLE);
                                createExpenseEndDate.setText(null);
                                createExpenseEndTime.setVisibility(View.VISIBLE);
                                createExpenseEndTime.setText(null);
                                type = "Transportation";
                                break;
                            case 1:
                                createExpenseType.setText("Accommodation");
                                createExpenseTypeSpecifics.setVisibility(View.VISIBLE);
                                createExpenseTypeSpecifics.setText(null);
                                setEndDate = true;
                                setTypeSpecific = true;
                                createExpenseEndDate.setVisibility(View.VISIBLE);
                                createExpenseEndDate.setText(null);
                                createExpenseEndTime.setVisibility(View.VISIBLE);
                                createExpenseEndTime.setText(null);
                                type = "Accommodation";
                                break;
                            case 2:
                                createExpenseType.setText("Excursion");
                                createExpenseTypeSpecifics.setVisibility(View.GONE);
                                createExpenseTypeSpecifics.setText(null);
                                setEndDate = true;
                                setTypeSpecific = false;
                                createExpenseEndDate.setVisibility(View.VISIBLE);
                                createExpenseEndDate.setText(null);
                                createExpenseEndTime.setVisibility(View.VISIBLE);
                                createExpenseEndTime.setText(null);
                                type = "Excursion";
                                break;
                            case 3:
                                createExpenseType.setText("Food");
                                createExpenseTypeSpecifics.setVisibility(View.GONE);
                                createExpenseTypeSpecifics.setText(null);
                                setEndDate = false;
                                setTypeSpecific = false;
                                createExpenseEndDate.setVisibility(View.GONE);
                                createExpenseEndDate.setText(null);
                                createExpenseEndTime.setVisibility(View.GONE);
                                createExpenseEndTime.setText(null);
                                type = "Food";
                                break;
                            case 4:
                                createExpenseType.setText("Other");
                                createExpenseTypeSpecifics.setVisibility(View.GONE);
                                createExpenseTypeSpecifics.setText(null);
                                setEndDate = false;
                                setTypeSpecific = false;
                                createExpenseEndDate.setVisibility(View.GONE);
                                createExpenseEndDate.setText(null);
                                createExpenseEndTime.setVisibility(View.GONE);
                                createExpenseEndTime.setText(null);
                                type = "Other";
                                break;
                        }
                    }
                });
                buildee.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

                AlertDialog dialog = buildee.create();
                dialog.show();
            }
        });

    }

    private void expenseTypeSpecificListener (final Context context, final Dialog create_window) {
        TextView createExpenseTypeSpecifics = (TextView) create_window.findViewById(R.id.create_expense_type_specifics);
        createExpenseTypeSpecifics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder buildee = new AlertDialog.Builder(create_window.getContext());
                buildee.setTitle("Type Options");
                boolean skip = false;

                if (type.equals("")) {
                    Toast.makeText(context, "Must select a type before picking options", Toast.LENGTH_SHORT).show();
                    skip = true;
                } else if (type.equals("Transportation")) {
                    buildee.setItems(R.array.expense_transportation_items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String toString = Integer.toString(i);
                            expenseTypeSpecific = toString;
                            switch (i) {
                                case 0: createExpenseTypeSpecifics.setText("Plane"); break;
                                case 1: createExpenseTypeSpecifics.setText("Train"); break;
                                case 2: createExpenseTypeSpecifics.setText("Coach"); break;
                                case 3: createExpenseTypeSpecifics.setText("Car"); break;
                                case 4: createExpenseTypeSpecifics.setText("Boat"); break;
                                case 5: createExpenseTypeSpecifics.setText("Taxi/Uber"); break;
                                case 6: createExpenseTypeSpecifics.setText("Other"); break;
                            }
                        }
                    });
                } else if (type.equals("Accommodation")) {
                    buildee.setItems(R.array.expense_accommodation_items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String toString = Integer.toString(i);
                            switch (i) {
                                case 0: createExpenseTypeSpecifics.setText("Hotel"); break;
                                case 1: createExpenseTypeSpecifics.setText("Airbnb"); break;
                                case 2: createExpenseTypeSpecifics.setText("Hostel"); break;
                                case 3: createExpenseTypeSpecifics.setText("Friend"); break;
                                case 4: createExpenseTypeSpecifics.setText("Other"); break;
                            }
                        }
                    });
                }

                buildee.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

                if (skip == false) {
                    AlertDialog dialog = buildee.create();
                    dialog.show();
                }
            }
        });
    }

    private void expenseStartDateListener (final Context context, final Dialog create_window) {
        startDateText = (TextView) create_window.findViewById(R.id.create_expense_start_date);
        startTimeText = (TextView) create_window.findViewById(R.id.create_expense_start_time);
        startDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog;

                if(!dateSelectFormat) {
                    dialog = new DatePickerDialog(context,AlertDialog.THEME_DEVICE_DEFAULT_DARK, startDateTextListener, year,month,day);
                } else {
                    dialog = new DatePickerDialog(context, AlertDialog.THEME_HOLO_DARK, startDateTextListener, year,month,day);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }

                dialog.getWindow();
                dialog.show();
            }
        });

        startDateTextListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month++;
                startDate = new DateTime(year,month,day,0,0);
                startTimeText.setText(null);
                if (dateFormat == false) {
                    startDateText.setText(startDate.toString("M-d-yyyy"));
                } else {
                    startDateText.setText(startDate.toString("d-M-yyyy"));
                }
            }
        };
    }

    private void expenseStartTimeListener (final Context context, final Dialog create_window) {
        startTimeText = (TextView) create_window.findViewById(R.id.create_expense_start_time);
        startTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(context,AlertDialog.THEME_DEVICE_DEFAULT_DARK, startTimeTextListener,hour,minute, true);
                mTimePicker.getWindow();
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        startTimeTextListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                if (selectedMinute < 10) {
                    startTimeText.setText(selectedHour + ":0" + selectedMinute);
                } else {
                    startTimeText.setText(selectedHour + ":" + selectedMinute);
                }
                startDate = new DateTime(startDate.getYear(),startDate.getMonthOfYear(),startDate.getDayOfMonth(),selectedHour,selectedMinute);
            }
        };
    }

    private void expenseEndDateListener (final Context context, final Dialog create_window) {
        endDateText = (TextView) create_window.findViewById(R.id.create_expense_end_date);
        endTimeText = (TextView) create_window.findViewById(R.id.create_expense_end_time);
        endDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog;

                if(!dateSelectFormat) {
                    dialog = new DatePickerDialog(context,AlertDialog.THEME_DEVICE_DEFAULT_DARK, endDateTextListener, year,month,day);
                } else {
                    dialog = new DatePickerDialog(context, AlertDialog.THEME_HOLO_DARK, startDateTextListener, year,month,day);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }

                dialog.getWindow();
                dialog.show();
            }
        });

        endDateTextListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month++;
                endDate = new DateTime(year,month,day,0,0);
                endTimeText.setText(null);
                if (dateFormat == false) {
                    endDateText.setText(endDate.toString("M-d-yyyy"));
                } else {
                    endDateText.setText(endDate.toString("d-M-yyyy"));
                }
            }
        };
    }

    private void expenseEndTimeListener (final Context context, final Dialog create_window) {
        endTimeText = (TextView) create_window.findViewById(R.id.create_expense_end_time);
        endTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(context,AlertDialog.THEME_DEVICE_DEFAULT_DARK, endTimeTextListener,hour,minute, true);
                mTimePicker.getWindow();
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        endTimeTextListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                if (selectedMinute < 10) {
                    endTimeText.setText(selectedHour + ":0" + selectedMinute);
                } else {
                    endTimeText.setText(selectedHour + ":" + selectedMinute);
                }
                endDate = new DateTime(endDate.getYear(),endDate.getMonthOfYear(),endDate.getDayOfMonth(),selectedHour,selectedMinute);
                Log.i("myTag", "Start date: " + startDate.toString("M-d-yyyy HH:mm") + ", End date: " + endDate.toString("M-d-yyyy HH:mm"));
            }
        };
    }

    private void expenseCurrencyListener (final Context context, final Dialog create_window){
        TextView currencyText = (TextView) create_window.findViewById(R.id.create_expense_currency);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        currency = pref.getString("pref_app_currency","Default");
        if (currency.equals("USD")) {
            currencyText.setText("$");
        } else if (currency.equals("CAD")) {
            currencyText.setText("$" + " CAD");
        } else if (currency.equals("GBP")) {
            currencyText.setText("£");
        } else if (currency.equals("EUR")) {
            currencyText.setText("€");
        }
        currencyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder buildee = new AlertDialog.Builder(create_window.getContext());
                buildee.setTitle("Currency");
                buildee.setItems(R.array.pref_app_currency_entries, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String toString = Integer.toString(i);
                        switch (i) {
                            case 0: currencyText.setText("USD"); currency = "USD"; break;
                            case 1: currencyText.setText("CAD"); currency = "CAD"; break;
                            case 2: currencyText.setText("GBP"); currency = "GBP"; break;
                            case 3: currencyText.setText("EUR"); currency = "EUR"; break;
                        }
                    }
                } );
            }
        });
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
                EditText expenseName = (EditText) create_window.findViewById(R.id.create_expense_name);
                EditText expenseCost = (EditText) create_window.findViewById(R.id.create_expense_cost);
                String name = expenseName.getText().toString();
                String inT = expenseCost.getText().toString();
                cost = Integer.parseInt(inT);

                if (name.equals("")) {
                    Toast.makeText(context, "Invalid Name", Toast.LENGTH_SHORT).show();
                } else if (startDate == null) {
                    Toast.makeText(context, "Missing Start Date", Toast.LENGTH_SHORT).show();
                } else if (startDate.isAfter(endDate)){
                    Toast.makeText(context, "End Date Can't Be Before Start Date", Toast.LENGTH_SHORT).show();
                } else if (startDate.isBefore(trip.getStartDate())) {
                    Toast.makeText(context, "Start Date Can't Be Before Trip Start Date", Toast.LENGTH_SHORT).show();
                } else if (setEndDate == true && endDate.isAfter(trip.getEndDate())) {
                    Toast.makeText(context, "End Date Can't Be After Trip End Date", Toast.LENGTH_SHORT).show();
                } else {

                    SharedPreferences settings = context.getSharedPreferences("Trip_Pref", 0);
                    SharedPreferences.Editor prefEditor = settings.edit();
                    final GsonBuilder builder = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeConverter());
                    final Gson gson = builder.create();
                    String json = settings.getString("Trips", "");
                    Type type = new TypeToken<ArrayList<Trip>>(){}.getType();
                    List<Trip> tripList = gson.fromJson(json, type);

                    Expense e = new Expense(name,expenseType,currency,cost,startDate);

                    if (setEndDate == true) {
                        e.setEndDate(endDate);
                    }

                    trip.addToList(e);
                    trip.setMoneySpent(trip.getMoneySpent() + cost);

                    int i =0;
                    for (Trip t : tripList) {

                        if (t.getName().equals(trip.getName())) {
                            tripList.set(i,trip);
                            break;
                        }
                        i++;
                    }

                    Collections.sort(trip.getList(), new Comparator<Expense>() {
                        @Override
                        public int compare(Expense e1, Expense e2) {
                            DateTime d1;
                            DateTime d2;
                            if (e1.getEndDate() == null) {
                                if (e2.getEndDate() == null) {
                                    d1 = new DateTime(e1.getStartDate());
                                    d2 = new DateTime(e2.getStartDate());
                                } else {
                                    d1 = new DateTime(e1.getStartDate());
                                    d2 = new DateTime(e2.getEndDate());
                                }
                            } else if (e2.getEndDate() == null) {
                                d1 = new DateTime(e1.getEndDate());
                                d2 = new DateTime(e2.getStartDate());
                            } else {
                                d1 = new DateTime(e1.getStartDate());
                                d2 = new DateTime(e2.getEndDate());
                            }
                            return d1.compareTo(d2);
                        }
                    });

                    json = gson.toJson(tripList);

                    prefEditor.putString("Trips", json);
                    prefEditor.commit();

                    MainActivity.updateAdapter(tripList);
                    TripDisplay.updateAdapter(tripList,trip);

                    create_window.dismiss();
                }
            }
        });
    }
}
