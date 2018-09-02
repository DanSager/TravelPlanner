package io.github.dansager.travelplanner;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.github.dansager.travelplanner.data_structures.DateTimeConverter;
import io.github.dansager.travelplanner.data_structures.ExchangeRate;
import io.github.dansager.travelplanner.data_structures.Expense;
import io.github.dansager.travelplanner.data_structures.Trip;

import static io.github.dansager.travelplanner.TripDisplay.round;

public class CreateExpense {
    
    private static Dialog create_window;
    private static Context mCtx;

    Boolean dateFormat;
    Boolean dateSelectFormat;
    private static Trip trip;

    private DatePickerDialog.OnDateSetListener startDateTextListener;
    private DatePickerDialog.OnDateSetListener endDateTextListener;
    private TimePickerDialog.OnTimeSetListener startTimeTextListener;
    private TimePickerDialog.OnTimeSetListener endTimeTextListener;
    private String expenseTypeSpecific = "";
    private TextView startDateText;
    private TextView startTimeText;
    private TextView endDateText;
    private TextView endTimeText;
    private boolean setEndDate = false;
    DateTime startDate = new DateTime();
    DateTime endDate;
    private String currency;
    private double cost;
    private static String type = "";

    ExchangeRate ER = new ExchangeRate();

    public void createDialogWindow (final Context context,Trip activeTrip) {
        create_window = new Dialog(context);
        mCtx = context;
        trip = activeTrip;

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mCtx);
        dateFormat = pref.getBoolean("pref_app_date_format",false);   //Default/false = mm/dd/yyyy
        dateSelectFormat = pref.getBoolean("pref_date_select_format",false); //Default/false = calendar

        create_window.setContentView(R.layout.create_expense_popup);
        create_window.getWindow();
        create_window.show();

        expenseTypeListener();
        expenseTypeSpecificListener();

        expenseStartDateListener();
        expenseStartTimeListener();
        expenseEndDateListener();
        expenseEndTimeListener();

        expenseCurrencyListener();

        cancelButtonListener();
        createButtonListener();
    }

    private void expenseTypeListener () {
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
                        switch (i) {
                            case 0:
                                createExpenseType.setText("Transportation");
                                createExpenseTypeSpecifics.setVisibility(View.VISIBLE);
                                createExpenseTypeSpecifics.setText(null);
                                setEndDate = true;
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
                                expenseTypeSpecific = "";
                                setEndDate = true;
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
                                expenseTypeSpecific = "";
                                setEndDate = false;
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
                                expenseTypeSpecific = "";
                                setEndDate = false;
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

    private void expenseTypeSpecificListener () {
        TextView createExpenseTypeSpecifics = (TextView) create_window.findViewById(R.id.create_expense_type_specifics);
        createExpenseTypeSpecifics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder buildee = new AlertDialog.Builder(create_window.getContext());
                buildee.setTitle("Type Options");
                boolean skip = false;

                if (type.equals("")) {
                    Toast.makeText(mCtx, "Must select a type before picking options", Toast.LENGTH_SHORT).show();
                    skip = true;
                } else if (type.equals("Transportation")) {
                    buildee.setItems(R.array.expense_transportation_items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switch (i) {
                                case 0: createExpenseTypeSpecifics.setText("Plane"); expenseTypeSpecific = "Plane"; break;
                                case 1: createExpenseTypeSpecifics.setText("Train"); expenseTypeSpecific = "Train";break;
                                case 2: createExpenseTypeSpecifics.setText("Coach"); expenseTypeSpecific = "Coach"; break;
                                case 3: createExpenseTypeSpecifics.setText("Car"); expenseTypeSpecific = "Car"; break;
                                case 4: createExpenseTypeSpecifics.setText("Boat"); expenseTypeSpecific = "Boat"; break;
                                case 5: createExpenseTypeSpecifics.setText("Taxi/Uber"); expenseTypeSpecific = "Taxi/Uber"; break;
                                case 6: createExpenseTypeSpecifics.setText("Other"); expenseTypeSpecific = "Other"; break;
                            }
                        }
                    });
                } else if (type.equals("Accommodation")) {
                    buildee.setItems(R.array.expense_accommodation_items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switch (i) {
                                case 0: createExpenseTypeSpecifics.setText("Hotel"); expenseTypeSpecific = "Hotel"; break;
                                case 1: createExpenseTypeSpecifics.setText("Airbnb"); expenseTypeSpecific = "Airbnb"; break;
                                case 2: createExpenseTypeSpecifics.setText("Hostel"); expenseTypeSpecific = "Hostel"; break;
                                case 3: createExpenseTypeSpecifics.setText("Friend"); expenseTypeSpecific = "Friend"; break;
                                case 4: createExpenseTypeSpecifics.setText("Other"); expenseTypeSpecific = "Other"; break;
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

    private void expenseStartDateListener () {
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
                    dialog = new DatePickerDialog(mCtx,AlertDialog.THEME_DEVICE_DEFAULT_DARK, startDateTextListener, year,month,day);
                } else {
                    dialog = new DatePickerDialog(mCtx, AlertDialog.THEME_HOLO_DARK, startDateTextListener, year,month,day);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }

                dialog.getDatePicker().setMinDate(trip.getStartDate().getMillis());
                dialog.getDatePicker().setMaxDate(trip.getEndDate().getMillis());
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

    private void expenseStartTimeListener () {
        startTimeText = (TextView) create_window.findViewById(R.id.create_expense_start_time);
        startTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(mCtx,AlertDialog.THEME_DEVICE_DEFAULT_DARK, startTimeTextListener,hour,minute, true);
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

    private void expenseEndDateListener () {
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
                    dialog = new DatePickerDialog(mCtx,AlertDialog.THEME_DEVICE_DEFAULT_DARK, endDateTextListener, year,month,day);
                } else {
                    dialog = new DatePickerDialog(mCtx, AlertDialog.THEME_HOLO_DARK, startDateTextListener, year,month,day);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }

                dialog.getDatePicker().setMinDate(startDate.getMillis());
                dialog.getDatePicker().setMaxDate(trip.getEndDate().getMillis());
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

    private void expenseEndTimeListener () {
        endTimeText = (TextView) create_window.findViewById(R.id.create_expense_end_time);
        endTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(mCtx,AlertDialog.THEME_DEVICE_DEFAULT_DARK, endTimeTextListener,hour,minute, true);
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

    private void expenseCurrencyListener (){
        TextView currencyText = (TextView) create_window.findViewById(R.id.create_expense_currency);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mCtx);
        currency = pref.getString("pref_app_currency","Default");
        switch (currency) {
            case "USD": currencyText.setText("USD"); break;
            case "CAD": currencyText.setText("CAD"); break;
            case "GBP": currencyText.setText("GBP"); break;
            case "EUR": currencyText.setText("EUR"); break;
        }
        currencyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder buildee = new AlertDialog.Builder(create_window.getContext());
                buildee.setTitle("Currency");
                buildee.setItems(R.array.pref_app_currency_entries, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0: currencyText.setText("USD"); currency = "USD"; break;
                            case 1: currencyText.setText("CAD"); currency = "CAD"; break;
                            case 2: currencyText.setText("GBP"); currency = "GBP"; break;
                            case 3: currencyText.setText("EUR"); currency = "EUR"; break;
                        }
                    }
                } );
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

    private void cancelButtonListener () {
        Button cancelButton = (Button) create_window.findViewById(R.id.create_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                create_window.dismiss();
            }
        });
    }

    private void createButtonListener () {
        Button createButton = (Button) create_window.findViewById(R.id.create_create_button);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText expenseName = (EditText) create_window.findViewById(R.id.create_expense_name);
                EditText expenseCost = (EditText) create_window.findViewById(R.id.create_expense_cost);
                String name = expenseName.getText().toString();
                String inT = expenseCost.getText().toString();
                if (inT.equals("")) {
                    cost = 0.0;
                } else {
                    cost = Double.parseDouble(inT);
                }

                if (name.equals("")) {
                    Toast.makeText(mCtx, "Invalid Name", Toast.LENGTH_SHORT).show();
                } else if (startDate == null) {
                    Toast.makeText(mCtx, "Missing Start Date", Toast.LENGTH_SHORT).show();
                }   else {

                    SharedPreferences settings = mCtx.getSharedPreferences("Trip_Pref", 0);
                    SharedPreferences.Editor prefEditor = settings.edit();
                    final GsonBuilder builder = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeConverter());
                    final Gson gson = builder.create();
                    String json = settings.getString("Trips", "");
                    Type typeList = new TypeToken<ArrayList<Trip>>(){}.getType();
                    List<Trip> tripList = gson.fromJson(json, typeList);

                    switch (currency) {
                        case "USD": trip.setMoneySpent(trip.getMoneySpent() + cost); break;
                        case "CAD": cost = cost * ER.getCADtoUSD(); trip.setMoneySpent(trip.getMoneySpent() + cost); break;
                        case "GBP": cost = cost * ER.getGBPtoUSD(); trip.setMoneySpent(trip.getMoneySpent() + cost); break;
                        case "EUR": cost = cost * ER.getEURtoUSD(); trip.setMoneySpent(trip.getMoneySpent() + cost); break;
                    }

                    Expense e = new Expense(name,type,currency,cost,startDate);

                    if (endDate != null) {
                        e.setEndDate(endDate);
                    }
                    if (expenseTypeSpecific != null || !expenseTypeSpecific.equals("")) {
                        e.setTypeSpecific(expenseTypeSpecific);
                    }

                    if (round(trip.getBudget(),2) != 0.0 || trip.getBudget() != 0) {
                        trip.setBudget(trip.getBudget() - round(cost,2));
                    }

                    trip.addToList(e);

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
                                d1 = new DateTime(e1.getEndDate());
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
