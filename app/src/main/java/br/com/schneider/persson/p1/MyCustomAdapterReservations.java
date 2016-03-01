package br.com.schneider.persson.p1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by LFSPersson on 2/17/16.
 */
public class MyCustomAdapterReservations extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list = new ArrayList<String>();
    private Context context;


    public MyCustomAdapterReservations(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        //return list.get(pos).getId();
        //just return 0 if your list items do not have an Id variable.
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.activity_my_reservations_item, null);
        }

        //Handle TextView and display string from your list
        TextView listItemText = (TextView) view.findViewById(R.id.list_item_string);
        listItemText.setText(list.get(position));

        //Handle buttons and add onClickListeners
        Button deleteBtn = (Button) view.findViewById(R.id.delete_btn);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Firebase firebaseRef = new Firebase("https://perssomobappfirebase.firebaseio.com");
                String userId = firebaseRef.getAuth().getUid();
                MyReservations reservations = new MyReservations();
                String eventLine = list.get(position).toString();
                eventLine = eventLine.concat(" at " + userId);
                reservations.deleteHourBD(eventLine);

                list.remove(position); //or some other task
                notifyDataSetChanged();
            }
        });

        return view;
    }

}
