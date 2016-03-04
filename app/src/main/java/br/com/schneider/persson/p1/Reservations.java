package br.com.schneider.persson.p1;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

public class Reservations extends Activity {

    private ProgressBar progressBar;
    final ArrayList<String> list = new ArrayList<String>();
    int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservations);

        setConfigScreen();
        setConfigFirebase();
        loadAsyncTask();

    }

    public void setConfigScreen() {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void setConfigFirebase() {
        Firebase.setAndroidContext(this);
        //firebaseRef = new Firebase(getResources().getString(R.string.firebase_url));
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

                getReservations();

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

    public void getReservations() {

        String userId,
                url_indexId;

        Firebase firebaseRef,
                indexIdRef;

        Firebase.setAndroidContext(getApplicationContext());
        firebaseRef = new Firebase(getResources().getString(R.string.firebase_url));

        userId = firebaseRef.getAuth().getUid();
        url_indexId = "https://perssomobappfirebase.firebaseio.com/indexId/";
        url_indexId = url_indexId.concat(userId);
        indexIdRef = new Firebase(url_indexId);

        list.clear();

        Query queryRef = indexIdRef.orderByValue();

        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists() == false) {
                    progressBar.setVisibility(View.GONE);
                    TextView textView = (TextView) findViewById(R.id.tVMsg);
                    textView.setVisibility(View.VISIBLE);
                    list.clear();
                    final ListView listView = (ListView) findViewById(R.id.reservationsList);
                    listView.setVisibility(View.INVISIBLE);

                    return;
                }

                for (DataSnapshot idSnapshot : snapshot.getChildren()) {
                    IndexId indexId = idSnapshot.getValue(IndexId.class);

                    String eventHour = indexId.getHour();
                    String eventDate = indexId.getDate();

                    list.add(eventDate + " at " + eventHour);

                }

                progressBar.setVisibility(View.GONE);
                loadReservationList();

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.i("LF", firebaseError.getMessage());
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    public void loadReservationList() {

        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);


        final ListView listView = (ListView) findViewById(R.id.reservationsList);
        listView.setAdapter(itemsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String line = list.get(position).toString();

                String[] parts = line.split(" at ");
                String date = parts[0];
                String hour = parts[1];

                Intent it;
                Bundle bundle = new Bundle();
                bundle.putString("Date", date);
                bundle.putString("Hour", hour);
                bundle.putString("Position", String.valueOf(position));
                pos = position;

                list.clear();

                it = new Intent(getApplicationContext(), ReservationDetail.class);
                it.putExtras(bundle);
                startActivity(it);
            }
        });
    }

}
