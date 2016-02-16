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
import android.widget.CalendarView;
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
    Intent it;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);
        setConfigScreen();
        setConfigFirebase();
        //getUsers();
    }

    public void setConfigScreen() {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void setConfigFirebase() {
        Firebase.setAndroidContext(this);
        firebaseRef = new Firebase(getResources().getString(R.string.firebase_url));
        firebaseUsersRef = new Firebase(getResources().getString(R.string.firebase_url));
    }

    public void doLogout() {
        firebaseRef.unauth();
        goLogin();
    }

    private void goLogin() {
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

    public void onClick_Calendar(View view) {
        it = new Intent(this, Booking.class);
        startActivity(it);
    }

    public void onClick_Reservations(View view) {
        it = new Intent(this, MyReservations.class);
        startActivity(it);
    }

}

