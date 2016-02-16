package br.com.schneider.persson.p1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.parse.Parse;
import com.parse.ParseInstallation;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Register extends Activity {

    Button btRegister;
    EditText etPassword, etEmail;
    String password, email;
    ProgressDialog progress;
    String status, msg;
    Firebase firebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setConfigScreen();
        setConfigFirebase();
    }

    public void setConfigScreen() {
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void setConfigFirebase() {
        Firebase.setAndroidContext(this);
        firebaseRef = new Firebase(getResources().getString(R.string.firebase_url));
    }

    public void initialize() {
        etPassword = (EditText) findViewById(R.id.etPass);
        etEmail = (EditText) findViewById(R.id.etEmail);
        btRegister = (Button) findViewById(R.id.btRegister);
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
        status = null;
        msg = null;

        progress = ProgressDialog.show(this, "Creating user", "Wait..", true);
    }

    public void registerParseInstallation(){
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
        ParseInstallation.getCurrentInstallation().saveInBackground();

        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("device_id", email);
        installation.saveInBackground();
    }

    public void createUserFirebase(){
        Firebase userRef = firebaseRef.child("users");
        Firebase newUserRef = userRef.push();

        Map<String, String> users = new HashMap<>();
        users.put("email", email);
        newUserRef.setValue(users);
    }

    public void execStatusOk() {
        Toast.makeText(getApplicationContext(), R.string.user_created_ok, Toast.LENGTH_LONG).show();
        finish();
    }

    public void execStatusNOk() {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    public void onClick_Register(View view) {
        initialize();

        new Thread(new Runnable() {
            @Override
            public void run() {

                firebaseRef.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> result) {
                        status = "ok";
                        createUserFirebase();
                        registerParseInstallation();
                        execStatusOk();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progress.dismiss();
                            }
                        });
                    }

                    @Override
                    public void onError(FirebaseError firebaseError) {
                        status = "nok";
                        msg = firebaseError.getMessage();
                        execStatusNOk();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progress.dismiss();
                            }
                        });
                    }
                });
            }
        }).start();
    }
}
