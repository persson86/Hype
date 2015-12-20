package br.com.schneider.persson.p1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class Login extends Activity {

    EditText etPassword, etEmail;
    Button btLogin;
    String password,
            email;
    boolean cancelLogin;

    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView loggedInStatusTextView;
    private ProgressDialog authProgressDialog;
    private Firebase firebaseRef;
    private AuthData authData;
    private Firebase.AuthStateListener authStateListener;

    private class AuthResultHandler implements Firebase.AuthResultHandler {
        private final String provider;

        public AuthResultHandler(String provider) {
            this.provider = provider;}

        @Override
        public void onAuthenticated(AuthData authData) {
            authProgressDialog.hide();
            setAuthenticatedUser(authData);
        }

        @Override
        public void onAuthenticationError(FirebaseError firebaseError) {
            authProgressDialog.hide();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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

        authProgressDialog = new ProgressDialog(this);
        authProgressDialog.setTitle("Loading");
        authProgressDialog.setMessage("Authenticating...");
        authProgressDialog.setCancelable(false);
        authProgressDialog.show();

        authStateListener = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                authProgressDialog.hide();
            }
        };

        firebaseRef.addAuthStateListener(authStateListener);
    }

    private void setAuthenticatedUser(AuthData authData) {
        if (authData != null) {
            String uId = null;
            uId = authData.getUid();
            if (uId != null) {
                startHello();
            }
            this.authData = authData;
        }
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void initialize() {
        etPassword = (EditText) findViewById(R.id.etPass);
        etEmail = (EditText) findViewById(R.id.etEmail);
        btLogin = (Button) findViewById(R.id.btLogin);

        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
    }

    public void onClick_Login(View view) {
        checkLogin();
        Log.i("LF", "1 " + cancelLogin);
        if (cancelLogin == false) {
            authProgressDialog.show();
            firebaseRef.authWithPassword(email, password, new AuthResultHandler("password"));
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    public void checkLogin() {
        initialize();
        cancelLogin = false;
        View focusView = null;

        if (TextUtils.isEmpty(email)) {
            etEmail.setError(getString(R.string.invalid_email));
            focusView = etEmail;
            cancelLogin = true;
        } else if (!isEmailValid(email)) {
            etEmail.setError(getString(R.string.incorrect_email));
            focusView = etEmail;
            cancelLogin = true;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getString(R.string.invalid_password));
            focusView = etPassword;
            cancelLogin = true;
        }
    }

    private void startHello() {
        Intent it;
        it = new Intent(this, Hello.class);
        startActivity(it);
        finish();
    }

    public void onClick_Register(View view) {
        Intent it;
        it = new Intent(this, Register.class);
        startActivity(it);
    }
}
