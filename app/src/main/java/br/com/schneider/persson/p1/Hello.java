package br.com.schneider.persson.p1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Hello extends FragmentActivity {
    private Firebase firebaseRef, firebaseUsersRef;

    public String usersValue;

    private Planet[] planets;
    private ArrayAdapter<Planet> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);
        setConfigScreen();
        setConfigFirebase();
        getUsers();
    }

    public void setConfigScreen() {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void setConfigFirebase() {
        Firebase.setAndroidContext(this);
        firebaseRef = new Firebase(getResources().getString(R.string.firebase_url));
        firebaseUsersRef = new Firebase(getResources().getString(R.string.firebase_url));
    }

    public void getUsers() {

        firebaseUsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.i("LF", snapshot.getValue().toString());
                Log.i("LF", firebaseRef.getAuth().getProviderData().toString());
                usersValue = snapshot.getValue().toString();

                int size = usersValue.length();
                Log.i("LF", Integer.toString(size));

                int start2 = 0;
                int count = 0;
                while (count < size) {
                    count = count + 1;
                    Log.i("LF", Integer.toString(count));
                    int found = Arrays.asList(usersValue).indexOf(".");
                    Log.i("LF", Integer.toString(found));
                    if (found != -1) {
                        Log.i("LF", "Found Hello at index " + found);
                    }
                    if (found == -1);
                    start2 = found + 2;  // move start up for next iteration
                    Log.i("LF", "Problem");
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });


        //logica para buscar emails da string
/*     String str = "Teste";
        int start = 0;
        while (true) {
            int found = str.indexOf("email=", start);
            if (found != -1) {
                Log.i("LF", "Found Hello at index " + found);
            }
            if (found == -1) break;
            start = found + 2;  // move start up for next iteration
        }*/

        //fim

        //String[] ar=usersValue.split("[email]");
        //Log.i("LF", ar.toString());

        // Find the ListView resource.
        ListView mainListView = (ListView) findViewById(R.id.mainListView);

        // When item is tapped, toggle checked properties of CheckBox and Planet.
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View item,
                                    int position, long id) {
                Planet planet = listAdapter.getItem(position);
                planet.toggleChecked();
                PlanetViewHolder viewHolder = (PlanetViewHolder) item.getTag();
                viewHolder.getCheckBox().setChecked(planet.isChecked());
            }
        });

        // Create and populate planets.
        planets = (Planet[]) getLastCustomNonConfigurationInstance();
        if (planets == null) {
            planets = new Planet[]{
                    new Planet("Mercury"), new Planet("Venus"), new Planet("Earth"),
                    new Planet("Mars"), new Planet("Jupiter"), new Planet("Saturn"),
                    new Planet("Uranus"), new Planet("Neptune"), new Planet("Ceres"),
                    new Planet("Pluto"), new Planet("Haumea"), new Planet("Makemake"),
                    new Planet("Eris"), new Planet("Epsilon Eridani"), new Planet("Gliese 876 b"),
                    new Planet("HD 209458 b")
            };
        }
        ArrayList<Planet> planetList = new ArrayList<Planet>();
        planetList.addAll(Arrays.asList(planets));

        // Set our custom array adapter as the ListView's adapter.
        listAdapter = new PlanetArrayAdapter(this, planetList);
        mainListView.setAdapter(listAdapter);
    }

    public void doLogout() {
        firebaseRef.unauth();
        goLogin();
    }

    private void goLogin() {
        Intent it;
        it = new Intent(this, Login.class);
        startActivity(it);
        finish();
    }

    public void onClick_Logout(View view) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle(R.string.logout_dialog_title);
        builder1.setMessage(R.string.logout_dialog_msg);
        builder1.setCancelable(true);
        builder1.setPositiveButton(R.string.logout_dialog_ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        doLogout();
                    }
                });
        builder1.setNegativeButton(R.string.logout_dialog_cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    ///////////////////////

    /**
     * Holds planet data.
     */
    private static class Planet {
        private String name = "";
        private boolean checked = false;

        public Planet() {
        }

        public Planet(String name) {
            this.name = name;
        }

        public Planet(String name, boolean checked) {
            this.name = name;
            this.checked = checked;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }

        public String toString() {
            return name;
        }

        public void toggleChecked() {
            checked = !checked;
        }
    }

    /**
     * Holds child views for one row.
     */
    private static class PlanetViewHolder {
        private CheckBox checkBox;
        private TextView textView;

        public PlanetViewHolder() {
        }

        public PlanetViewHolder(TextView textView, CheckBox checkBox) {
            this.checkBox = checkBox;
            this.textView = textView;
        }

        public CheckBox getCheckBox() {
            return checkBox;
        }

        public void setCheckBox(CheckBox checkBox) {
            this.checkBox = checkBox;
        }

        public TextView getTextView() {
            return textView;
        }

        public void setTextView(TextView textView) {
            this.textView = textView;
        }
    }

    /**
     * Custom adapter for displaying an array of Planet objects.
     */
    private static class PlanetArrayAdapter extends ArrayAdapter<Planet> {

        private LayoutInflater inflater;

        public PlanetArrayAdapter(Context context, List<Planet> planetList) {
            super(context, R.layout.simplerow, R.id.rowTextView, planetList);
            // Cache the LayoutInflate to avoid asking for a new one each time.
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Planet to display
            Planet planet = this.getItem(position);

            // The child views in each row.
            CheckBox checkBox;
            TextView textView;

            // Create a new row view
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.simplerow, null);

                // Find the child views.
                textView = (TextView) convertView.findViewById(R.id.rowTextView);
                checkBox = (CheckBox) convertView.findViewById(R.id.CheckBox01);

                // Optimization: Tag the row with it's child views, so we don't have to
                // call findViewById() later when we reuse the row.
                convertView.setTag(new PlanetViewHolder(textView, checkBox));

                // If CheckBox is toggled, update the planet it is tagged with.
                checkBox.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        Planet planet = (Planet) cb.getTag();
                        planet.setChecked(cb.isChecked());
                    }
                });
            }
            // Reuse existing row view
            else {
                // Because we use a ViewHolder, we avoid having to call findViewById().
                PlanetViewHolder viewHolder = (PlanetViewHolder) convertView.getTag();
                checkBox = viewHolder.getCheckBox();
                textView = viewHolder.getTextView();
            }

            // Tag the CheckBox with the Planet it is displaying, so that we can
            // access the planet in onClick() when the CheckBox is toggled.
            checkBox.setTag(planet);

            // Display planet data
            checkBox.setChecked(planet.isChecked());
            textView.setText(planet.getName());

            return convertView;
        }

    }

    public Object onRetainCustomNonConfigurationInstance() {
        return planets;
    }
    ////////////////////
}

