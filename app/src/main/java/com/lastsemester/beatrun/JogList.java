package com.lastsemester.beatrun;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class JogList extends AppCompatActivity {

    static ArrayList<String> arrayIndex = new ArrayList<String>();
    static ArrayList<String> arrayData = new ArrayList<String>();

    ArrayAdapter<String> arrayAdapter;
    private DbOpenHelper dbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joglist);

        ListView listview = (ListView)findViewById(R.id.listview);

        dbOpenHelper = new DbOpenHelper(this);
        dbOpenHelper.open();
        dbOpenHelper.create();
        showDatabases();

        arrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, arrayData);

        listview.setAdapter(arrayAdapter);
    }

    public void showDatabases() {
        Cursor iCursor = dbOpenHelper.selectColumns();
        while (iCursor.moveToNext()) {
            String tempDate = iCursor.getString(iCursor.getColumnIndex("date"));
            String tempDist = iCursor.getString(iCursor.getColumnIndex("dist"));
            String tempTime = iCursor.getString(iCursor.getColumnIndex("time"));
            String tempAvgspd = iCursor.getString(iCursor.getColumnIndex("avgspeed"));
            String tempAvgbpm = iCursor.getString(iCursor.getColumnIndex("avgbpm"));

            String Result = "날짜: "+tempDate + ", 거리(m): " + tempDist
                    + ", 뛴 시간(초): " + tempTime + ", 평균속력(km/h): " + tempAvgspd
                    + ", 평균심장박동수(BPM): " + tempAvgbpm;
            Log.e("내용: ",Result);
            arrayData.add(Result);
        }
    }
}
