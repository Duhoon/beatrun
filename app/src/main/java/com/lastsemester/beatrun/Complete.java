package com.lastsemester.beatrun;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EventListener;

public class Complete extends AppCompatActivity {
    TextView tvTime; TextView tvDist; TextView tvSpeed; TextView tvHBR;
    Button btConfirm;
    PaceControl paceControl;
    private DbOpenHelper dbOpenHelper;

    String date;
    String dist;
    String time;
    String avgspd;
    String avgbpm;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete);

        Intent completeData = getIntent();
        String stTime = completeData.getStringExtra("time");
        String stDist = completeData.getStringExtra("dist");
        int heartrate = completeData.getIntExtra("heartrate", 0);

        Log.e("Time: ", stTime);
        Log.e("Dist: ", stDist);


        tvDist = (TextView) findViewById(R.id.tv_c_dist);
        tvTime = (TextView) findViewById(R.id.tv_c_time);
        tvSpeed = (TextView) findViewById(R.id.tv_c_speed);
        tvHBR = (TextView) findViewById(R.id.tv_c_heartrate);
        btConfirm = (Button) findViewById(R.id.bt_confirm);

        SimpleDateFormat s = new SimpleDateFormat("yyyyMMddhhmm");
        date = s.format(new Date());
        dist = stDist;
        int tempmin = Integer.parseInt(stTime.substring(0,2));
        int tempsec = Integer.parseInt(stTime.substring(3,5));
        time = String.valueOf(tempmin * 60 + tempsec);
        avgspd = speedCal(stTime, stDist);
        Log.e("어디서 에러야", String.valueOf(avgHeartCal(stTime, heartrate)));
        avgbpm = avgHeartCal(stTime, heartrate);

        Log.e("date", date);


        tvDist.setText("주행거리: "+stDist+" m");
        tvTime.setText("시간: "+stTime);
        tvSpeed.setText("평균속력: "+avgspd+" km/h");
        tvHBR.setText("평균 심박수: "+avgbpm+" bpm");

        dbOpenHelper = new DbOpenHelper(this);
        dbOpenHelper.open();
        dbOpenHelper.create();


        btConfirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                dbOpenHelper.open();
                dbOpenHelper.insertColumn(date,dist,time,avgspd,avgbpm);
            }
        });
    }

    private String speedCal(String time, String dist){
        double speed = 0;

        String sMin = time.substring(0,2);
        String sSec = time.substring(3,5);

        int min = Integer.parseInt(sMin);
        int sec = Integer.parseInt(sSec);
        Log.e("sec", String.valueOf(sec));
        Log.e("min", String.valueOf(min));

        int iDist = Integer.parseInt(dist);
        Log.e("Dist", String.valueOf(iDist));

        Log.e("km 환산", String.valueOf((double)iDist/1000));
        Log.e("시간 환산", String.valueOf((double)(min*60 + sec)/3600));

        if(min > 0 || sec > 0) {
            speed = ((double)iDist/1000) / ((double)(min*60 + sec)/3600);
            Log.e("Speed", String.valueOf(speed));
            if(iDist == 0){
                return "0";
            } else return String.valueOf(speed).substring(0,4);
        }
        else {
            speed = 0f;
            Log.e("Speed", String.valueOf(speed));
            return "0";
        }

    }

    private String avgHeartCal(String time, int heartrate){
        if(heartrate>0) {
            String sMin = time.substring(0, 2);
            String sSec = time.substring(3, 5);

            int min = Integer.parseInt(sMin);
            int sec = Integer.parseInt(sSec);


            double avgHBR = (double) heartrate / (double) (min * 60 + sec);
            Log.e("HeartRate: ", String.valueOf(avgHBR));
            String avHBR = String.valueOf(avgHBR).substring(0,4);

            return avHBR;
        } else return "0";
    }

}
