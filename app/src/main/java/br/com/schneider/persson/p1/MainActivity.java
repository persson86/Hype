package br.com.schneider.persson.p1;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.ParseQuery;
import com.parse.PushService;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Parse.enableLocalDatastore(this);
        Parse.initialize(this);

        ParseInstallation.getCurrentInstallation().saveInBackground();

        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("device_id", "email_1");
        installation.saveInBackground();

        //ParseInstallation.getCurrentInstallation().saveInBackground();

    }

    public void onClickSendPush(View view) {

        ParseQuery query = ParseInstallation.getQuery();
        query.whereEqualTo("device_id", "email_2");

        // Notification for Android users
        ParsePush androidPush = new ParsePush();
        androidPush.setMessage("Teste Push!!!");
        androidPush.setQuery(query);
        androidPush.sendInBackground();

/*        ParseQuery query = ParseInstallation.getQuery();
        query.whereEqualTo("device_id", "email2");
        query.whereEqualTo("user_obj", ParseInstallation.getCurrentInstallation().getObjectId());
        ParsePush push = new ParsePush();
        push.setMessage("Teste Push!!");
        push.setQuery(query);
        push.sendInBackground();*/

 /*       ParseQuery query = ParseInstallation.getQuery();
        query.whereEqualTo("device_id", "email2");
        ParsePush push = new ParsePush();
        push.setQuery(query);
        push.setMessage("Teste Push!!");
        push.sendInBackground();*/

   /*     ParsePush push = new ParsePush();
        String yourMessage = "Hello email_2";
        push.setChannel("email_2");
        push.setMessage(yourMessage);
        push.sendInBackground();*/

    }

/*    ParsePush push = new ParsePush();
    String message = "Teste Push message";
    push.setChannel("ch1");
    push.setMessage(message);
    push.sendInBackground();*/

}
