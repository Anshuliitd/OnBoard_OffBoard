package com.example.adityashekhar.onboardbeacon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class DisplayMessageActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    public static final String EXTRA_MESSAGE1 = "com";
    public String NextSTOPname;
    public static BusList b;
    public Bus ee;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        b = new BusList();
        b.addBus("123");
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity2.EXTRA_MESSAGE);
        String message2 = intent.getStringExtra(MainActivity.EXTRA_MESSAGE2);
        if(message==null)
        {
            message = message2;
        }
        int check=0;
        ArrayList<Bus> t = b.getList();
        ee = null;
        for(int i=0;i<t.size();i++){
            ee = t.get(i);
            if(ee.getName().equals(message)){
                check = 1;
                break;
            }
        }
        NextSTOPname = "BUS NOT FOUND";
        if(check==0)
        {
            TextView textView = (TextView) findViewById(R.id.textView);
            textView.setText("No Bus found");
        }
        else
        {
            TextView textView = (TextView) findViewById(R.id.textView);
            textView.setText("STOPS LIST");
            ArrayList<stop> w = ee.getStopList();
            ArrayList<String> ww = new ArrayList<String>();
            for(int i=0;i<w.size();i++)
            {
                stop ss = w.get(i);
                String tt = ss.getName();
                ww.add(i,tt);
            }
            ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,ww);
            ListView listView = (ListView) findViewById(R.id.listView);
            listView.setAdapter(adapter);
        }
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, NextStopActivity.class);
        String message = NextSTOPname;
        intent.putExtra(EXTRA_MESSAGE1, ee.getName());
        startActivity(intent);
    }
}
