package com.example.rlakkh.pdr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by RLAKKH on 2018-04-06.
 */

public class Sub1Activity extends AppCompatActivity {
    TextView textView;
    ListView bluetoothListView;
    ArrayList list;
    ArrayAdapter arrayAdapter;
    ApplicationClass applicationClass;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub1);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        applicationClass = (ApplicationClass) getApplication();
        textView = (TextView) findViewById(R.id.textView);
        bluetoothListView =  (ListView) findViewById(R.id.BluetoothList);

        list = applicationClass.getList();
        arrayAdapter = new ArrayAdapter(getApplicationContext(),R.layout.support_simple_spinner_dropdown_item, list);
        bluetoothListView.setAdapter(arrayAdapter);

        bluetoothListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String tmp = (String) adapterView.getItemAtPosition(i);
                String[] tmpsplit = tmp.split("\n");
                String[] nametmp = tmpsplit[0].split(" ");
                String[] addresstmp = tmpsplit[1].split(" ");
                String name = nametmp[1];
                String address = addresstmp[1];
                applicationClass.setAddress(address);
                applicationClass.setName(name);
                list.clear();
                finish();
            }
        });
    }
}