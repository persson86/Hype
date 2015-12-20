package br.com.schneider.persson.p1;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

public class Load extends Activity {

    private Firebase firebaseRef;
    private ProgressBar bar;

    private class ProgressTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute(){
            bar.setVisibility(View.VISIBLE);
            bar.getIndeterminateDrawable().setColorFilter(0xFFFF0000,android.graphics.PorterDuff.Mode.MULTIPLY);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            checkConnection();
            setConfigFirebase();
            checkAuthentication();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            bar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        setConfigScreen();
        loadProgressBar();
    }

    public void loadProgressBar() {
        bar = (ProgressBar) this.findViewById(R.id.progressBar);
        new ProgressTask().execute();
    }

    public void checkConnection() {

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent d) {
                boolean desconnected =
                        d.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

                String msg = (!desconnected) ? "Conectado" : "Desconectado";
                Log.i("LF", msg + desconnected);

                if (desconnected) {
                    noConnection();
                }
            }
        };
    }

    public void setConfigScreen() {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void setConfigFirebase() {
        Firebase.setAndroidContext(this);
        firebaseRef = new Firebase(getResources().getString(R.string.firebase_url));
    }

    public void checkAuthentication() {

        firebaseRef.addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (authData != null) {
                    Log.i("LF", "Logado " + authData.getUid() + " - " + authData.getExpires());
                    startHello();
                } else {
                    Log.i("LF", "Nao logado " + authData);
                    startLogin();
                }
            }
        });
    }

    private void startHello() {
        Intent it;
        it = new Intent(this, Hello.class);
        //it.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(it);
        finish();
    }

    private void startLogin() {
        Intent it;
        it = new Intent(this, Login.class);
        it.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(it);
        finish();
    }

    public void noConnection() {
        //Implementar case Vodafone
        Log.i("LF", "noConnection");
        finish();
    }

}
