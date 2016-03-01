package br.com.schneider.persson.p1;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class ReservationDetail extends Activity {

    ProgressBar progressBar;
    String date,
            hour,
            position,
            url_indexId,
            userId,
            node;
    int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_detail);
        setConfigScreen();
        setConfigFirebase();
        getDateHour();
    }

    public void setConfigScreen() {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void setConfigFirebase() {
        Firebase.setAndroidContext(this);
        //Firebase firebaseRef = new Firebase(getResources().getString(R.string.firebase_url));
    }

    public void getDateHour() {
        Bundle b = getIntent().getExtras();

        date = b.getString("Date");
        hour = b.getString("Hour");
        position = b.getString("Position");

        TextView tdate = (TextView) findViewById(R.id.date);
        TextView thour = (TextView) findViewById(R.id.hour);

        tdate.setText(date);
        thour.setText(hour);
    }

    public void loadAsyncTask() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        new AsyncTask<String, Integer, Integer>() {
            @Override
            protected Integer doInBackground(String... params) {

                /*try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/

                deleteRegister();

                return 1;
            }

            @Override
            protected void onPreExecute() {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
            }

            @Override
            protected void onPostExecute(Integer integer) {
            }

        }.execute();
    }

    public void deleteRegister() {

        final Firebase indexIdRef, firebaseRef;
        firebaseRef = new Firebase("https://perssomobappfirebase.firebaseio.com");
        userId = firebaseRef.getAuth().getUid();

        url_indexId = "https://perssomobappfirebase.firebaseio.com/indexId/";
        url_indexId = url_indexId.concat(userId);
        indexIdRef = new Firebase(url_indexId);

        indexIdRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists() == false) {
                    //progressBar.setVisibility(View.GONE);
                    //TextView textView = (TextView) findViewById(R.id.tVMsg);
//                    textView.setVisibility(View.VISIBLE);
                    return;
                }

                for (final DataSnapshot eventSnapshot : snapshot.getChildren()) {
                    final IndexId indexId = eventSnapshot.getValue(IndexId.class);
                    String eventHour = indexId.getHour();
                    String eventDate = indexId.getDate();

                    if (date.equals(eventDate) && hour.equals(eventHour)) {
                        node = indexId.getNode();

                        final Firebase indexDeleteRef;
                        String indexDelete = url_indexId;
                        indexDelete = indexDelete.concat("/" + eventSnapshot.getKey().toString());
                        indexDeleteRef = new Firebase(indexDelete);
                        indexDeleteRef.removeValue();
                        String formatedDate = date.replaceAll("/", "");

                        final Firebase bookingRef;
                        String url_booking = "https://perssomobappfirebase.firebaseio.com/booking/";
                        url_booking = url_booking.concat(formatedDate + "/" + node);
                        bookingRef = new Firebase(url_booking);
                        bookingRef.removeValue();

                    }

                }

                progressBar.setVisibility(View.GONE);
                TextView tMsg = (TextView) findViewById(R.id.tMsg);
                TextView tdate = (TextView) findViewById(R.id.date);
                TextView thour = (TextView) findViewById(R.id.hour);
                Button bDel    = (Button) findViewById(R.id.delete_btn);

                tMsg.setVisibility(View.VISIBLE);
                tdate.setVisibility(View.INVISIBLE);
                thour.setVisibility(View.INVISIBLE);
                bDel.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.i("LF", firebaseError.getMessage());
            }
        });
    }

    public void deleteBDBooking(String node) {
        String formatedDate = date.replaceAll("/", "");

        final Firebase bookingRef;
        String url_booking = "https://perssomobappfirebase.firebaseio.com/booking/";
        url_booking = url_booking.concat(formatedDate + "/" + node);
        bookingRef = new Firebase(url_booking);
        bookingRef.removeValue();
    }

    public void obnCLickDelete(View view) {
        loadAsyncTask();
        Toast.makeText(getApplicationContext(), "Reservation was deleted successfully", Toast.LENGTH_SHORT).show();
    }
}
