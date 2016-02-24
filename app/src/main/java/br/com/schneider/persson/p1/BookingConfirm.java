package br.com.schneider.persson.p1;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class BookingConfirm extends AppCompatActivity {

    private Firebase firebaseRef,
            firebaseBookingRef,
            bookingRef,
            indexIdRef;

    String date,
            hour,
            formatedDate,
            userID;
    Boolean okRecord;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirm);
        setConfigFirebase();
        getDateHour();
    }

    public void setConfigFirebase() {
        Firebase.setAndroidContext(this);
        firebaseRef = new Firebase(getResources().getString(R.string.firebase_url));
        firebaseBookingRef = new Firebase(getResources().getString(R.string.firebase_booking_url));
    }

    public void getDateHour() {
        Bundle b = getIntent().getExtras();

        date = b.getString("BookingDate");
        hour = b.getString("BookingHour");

        TextView tdate = (TextView) findViewById(R.id.date);
        TextView thour = (TextView) findViewById(R.id.hour);

        tdate.setText(date);
        thour.setText(hour);
    }


    public void onClick_ConfirmBooking(View view) {
        //checkDBforDuplicate();
        createBookingFirebase();
    }

    public void checkDBforDuplicate() {

        formatedDate = date.replaceAll("/", "");

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
                            okRecord = true;
                        }

                        for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                            BookingEvent bookingEvent = eventSnapshot.getValue(BookingEvent.class);

                            String eventHour = bookingEvent.getHour();

                            if (eventHour == hour){
                                okRecord = false;
                                Log.i("LF",   "Your not allowed to book at this hour " + okRecord );
                                //Toast.makeText(getApplicationContext(), "Your not allowed to book at this hour", Toast.LENGTH_SHORT).show();
                            }


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (okRecord == true) {
                                        createBookingFirebase();
                                    }
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


    public void createBookingFirebase() {

        userID = firebaseRef.getAuth().getUid();
        formatedDate = date.replaceAll("/", "");

        String url_booking = "https://perssomobappfirebase.firebaseio.com/booking/";
        url_booking = url_booking.concat(formatedDate);
        bookingRef = new Firebase(url_booking);

        Log.i("LF", userID);

        Firebase newBookingRef = bookingRef.push();

        Map<String, String> bookingMap = new HashMap<String, String>();
        bookingMap.put("hour", hour);
        bookingMap.put("userId", userID);
        bookingMap.put("date", date);
        newBookingRef.setValue(bookingMap);

        String postBokking = newBookingRef.getKey();
        if (postBokking != null) {
            Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "We have a problem, please check your reservation", Toast.LENGTH_SHORT).show();
        }

        String url_indexId = "https://perssomobappfirebase.firebaseio.com/indexId/";
        url_indexId = url_indexId.concat(userID);
        indexIdRef = new Firebase(url_indexId);
        Firebase newIndexIdRef = indexIdRef.push();
        Map<String, String> indexIdMap = new HashMap<String, String>();
        indexIdMap.put("hour", hour);
        indexIdMap.put("date", date);
        indexIdMap.put("node", postBokking);
        newIndexIdRef.setValue(indexIdMap);

        String postId = newIndexIdRef.getKey();
        if (postId != null) {
            Log.i("LF", postId);
        } else {
            Log.i("LF", "null");
        }

    }

}
