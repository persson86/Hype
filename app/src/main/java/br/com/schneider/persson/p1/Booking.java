package br.com.schneider.persson.p1;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.GenericTypeIndicator;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.humoule.customcal.CustomCalFragment;
import com.humoule.customcal.adapter.CustomCalGridAdapter;
import com.humoule.customcal.callback.CustomCalListener;
import com.shaded.fasterxml.jackson.databind.util.Converter;

/**
 * Created by LFSPersson on 1/27/16.
 */
public class Booking extends FragmentActivity implements AdapterView.OnItemClickListener {

    private Firebase firebaseRef, bookingRef;

    ProgressDialog progress;
    ProgressBar progressBar;

    ArrayList<String> listHours = new ArrayList<String>();

    String formatedDate,
            selectedDate,
            hour;

    Boolean noData;

    private CustomCalFragment customCalFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);


        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        customCalFragment = new CustomCalFragment();


        // If Activity is created after rotation
        if (savedInstanceState != null) {
            customCalFragment.restoreStatesFromKey(savedInstanceState, "CUSTOM_CAL_SAVED_STATE");
        }
        // If activity is created from fresh
        else {
            Bundle args = new Bundle();
            Calendar cal = Calendar.getInstance();
            args.putInt(CustomCalFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            args.putInt(CustomCalFragment.YEAR, cal.get(Calendar.YEAR));
            args.putBoolean(CustomCalFragment.ENABLE_SWIPE, true);

            customCalFragment.setArguments(args);
        }

        setCustomResourceForDates();

        // Attach to the activity
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar1, customCalFragment);
        t.commit();

        setListenerCalendar();

        /*// Setup listener
        final CustomCalListener listener = new CustomCalListener() {

            @Override
            public void onSelectDate(Date date, View view) {
                selectedDate = formatter.format(date);
                loadProgressBar();
                //checkBookingFirebase(selectedDate);
                //Toast.makeText(getApplicationContext(), date.toString(), Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(), formatter.format(date), Toast.LENGTH_SHORT).show();
                if (CustomCalGridAdapter.selected != null) {
                    CustomCalGridAdapter.selected.findViewById(R.id.calendar_iv).setVisibility(View.INVISIBLE);
                }
                view.findViewById(R.id.calendar_iv).setVisibility(View.VISIBLE);
                CustomCalGridAdapter.setSelectedDate(date);
                CustomCalGridAdapter.setSelectedView(view);

            }

            @Override
            public void onCustomCalViewCreated() {
                //Toast.makeText(getApplicationContext(), "View is ready !", Toast.LENGTH_SHORT).show();

            }

        };

        customCalFragment.setCustomCalListener(listener);*/

        listHours.add("9:00");
        listHours.add("10:00");
        listHours.add("11:00");
        listHours.add("12:00");
        listHours.add("13:00");
        listHours.add("14:00");
        listHours.add("15:00");
        listHours.add("16:00");
        listHours.add("17:00");
        listHours.add("18:00");

        noData = false;

        //createHourList(noData);

        //Save in memory selected day in calendar when is initialized - LF
        Date dateNow = new Date();
        selectedDate = formatter.format(dateNow);
        formatedDate = selectedDate.replaceAll("/", "");
        setConfigFirebase();
        setProgress();
    }

    private void setCustomResourceForDates() {
        Calendar cal = Calendar.getInstance();

        // Min date is last 7 days
        cal.add(Calendar.DATE, -18);
        Date blueDate = cal.getTime();

        // Max date is next 7 days
        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 16);

        if (customCalFragment != null) {
            customCalFragment.setBackgroundResourceForDate(R.color.blue, blueDate);
            customCalFragment.setTextColorForDate(R.color.white, blueDate);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (customCalFragment != null) {
            customCalFragment.saveStatesToKey(outState, "CUSTOM_CAL_SAVED_STATE");
        }

    }

    public void setConfigFirebase() {
        Firebase.setAndroidContext(this);
        firebaseRef = new Firebase(getResources().getString(R.string.firebase_url));
    }


    public void createHourList(Boolean noData) {

        if (noData == true){
            listHours.clear();
            listHours.add("9:00");
            listHours.add("10:00");
            listHours.add("11:00");
            listHours.add("12:00");
            listHours.add("13:00");
            listHours.add("14:00");
            listHours.add("15:00");
            listHours.add("16:00");
            listHours.add("17:00");
            listHours.add("18:00");
        }

        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listHours);

        ListView listView = (ListView) findViewById(R.id.listView1);
        listView.setAdapter(itemsAdapter);
        listView.setOnItemClickListener(this);

    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        hour = ((TextView) view).getText().toString();

        Intent it;
        Bundle bundle = new Bundle();
        bundle.putString("BookingDate", selectedDate);
        bundle.putString("BookingHour", hour);

        it = new Intent(this, BookingConfirm.class);
        it.putExtras(bundle);
        startActivity(it);
    }

    public  void setListenerCalendar(){
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());

        // Setup listener
        final CustomCalListener listener = new CustomCalListener() {

            @Override
            public void onSelectDate(Date date, View view) {
                selectedDate = formatter.format(date);
                formatedDate = selectedDate.replaceAll("/", "");
                setProgress();

                if (CustomCalGridAdapter.selected != null) {
                    CustomCalGridAdapter.selected.findViewById(R.id.calendar_iv).setVisibility(View.INVISIBLE);
                }
                view.findViewById(R.id.calendar_iv).setVisibility(View.VISIBLE);
                CustomCalGridAdapter.setSelectedDate(date);
                CustomCalGridAdapter.setSelectedView(view);

            }

            @Override
            public void onCustomCalViewCreated() {
                //Toast.makeText(getApplicationContext(), "View is ready !", Toast.LENGTH_SHORT).show();
            }

        };

        customCalFragment.setCustomCalListener(listener);
    }

    public void setProgress(){

        noData = false;

        new Thread(new Runnable() {
            @Override
            public void run() {

                String url_booking = "https://perssomobappfirebase.firebaseio.com/booking/";
                url_booking = url_booking.concat(formatedDate);
                bookingRef = new Firebase(url_booking);

                bookingRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.getValue() == null) {
                            noData = true;
                            createHourList(noData);
                        }
                        for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                            BookingEvent bookingEvent = eventSnapshot.getValue(BookingEvent.class);

                            String eventHour = bookingEvent.getHour();

                            for (int i = 0; i < listHours.size(); i++) {
                                if (listHours.get(i).equals(eventHour)) {
                                    listHours.remove(i);
                                }
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    createHourList(noData);
                                    //progress.dismiss();
                                }
                            });

                        }

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        Log.i("LF", firebaseError.getMessage());
                    }
                });

            }
        }).start();

    }


}




