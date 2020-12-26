package com.lastsemester.beatrun;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimerTask;

public class PaceControl extends AppCompatActivity implements SensorEventListener {

    public static Activity paceControl;

    private SimpleExoPlayer player; // 음악플레이어
    private PlayerControlView pcv;  // 플레이어 뷰
    private int currentWindow = 0;  // 플레이어 현재 음악
    private long playBackPosition = 0L; // 플레이어 현재 플레이 위치

    /**
     *  UI
     */
    private Button btUpSpd;
    private Button btNrSpd;
    private Button btDownSpd;
    private Button btGoMain;

    private TextView tvCount;
    private TextView tvHeart;
    private TextView tvStep;

    private ProgressBar progressBar;

    /**
     * 파이어베이스 실시간 데이터베이스
     */

    private DatabaseReference mDatabase;

    /**
     * Step Counter Sensor
     */


    private SensorManager sensorManager;
    private Sensor stepDector;
    int mStepDector;

    /**
     * Judgement
     */

    Judgement judgement;
    int currentHBR;
    long totalHBR;



    /**
     * ChangeSpeepd
     */
    private Thread changeSpeed;
    private Handler speedHandler = null;
    private boolean isJogging = true;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pacecontrol);

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

        /**
         * SharedPreference
         *
         */
        SharedPreferences pref = getSharedPreferences("UserInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        int weight = pref.getInt("weight", 0);
        int height = pref.getInt("height", 0);
        int age = pref.getInt("age", 0);
        Log.e("weight, height, age" , weight+" "+height+" "+age);

        Intent setData = getIntent();
        int valueTime = setData.getIntExtra("valueTime", 0);
        int valueDist = setData.getIntExtra("valueDist", 0);
        int strength = setData.getIntExtra("strength", 0);
        Log.e("Intent", valueTime + " " + valueDist + " " + strength);

        /**
         * Judgement 생성
         */
        judgement = new Judgement(65, 180, 25, strength);
        int ceilHBR = judgement.getCeilHBR();
        int floorHBR = judgement.getFloorHBR();

        pcv = (PlayerControlView) findViewById(R.id.exoPlayerView);

        ProgInit();
        progressBar.setVisibility(View.VISIBLE);

        tvCount = (TextView) findViewById(R.id.tv_timer);

        btUpSpd = (Button) findViewById(R.id.bt_upspd);
        btNrSpd = (Button) findViewById(R.id.bt_nrspd);
        btDownSpd = (Button) findViewById(R.id.bt_downspd);
        btGoMain = (Button) findViewById(R.id.bt_gomain);

        paceControl = PaceControl.this;

        btUpSpd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                player.setPlaybackParameters(new PlaybackParameters(1.1f));
            }
        });

        btNrSpd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                player.setPlaybackParameters(new PlaybackParameters(1.0f));
            }
        });

        btDownSpd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                player.setPlaybackParameters(new PlaybackParameters(0.9f));
            }
        });

        btGoMain.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), Complete.class);

                /**
                 * intent에 넣어 보낼 요소들
                 */
                intent.putExtra("time", tvCount.getText());
                intent.putExtra("dist", tvStep.getText());
                intent.putExtra("heartrate", totalHBR);
                startActivity(intent);
            }
        });

        timeThread tt = new timeThread();
        tt.start();

        tvHeart = (TextView)findViewById(R.id.tv_heartbeat);

        /**
         * 노래 속도 조절 알고리즘 및 심장박동 수 끌어오기;
         */

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FirebasePost post = snapshot.getValue(FirebasePost.class);
                tvHeart.setText(post.BPM_RATE);
                String temp = post.BPM_RATE;
                currentHBR = Integer.parseInt(temp.substring(0,2));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast myToast = Toast.makeText(getApplicationContext(),"실패", Toast.LENGTH_LONG);
            }
        });


        /**
         * Detector
         */
        tvStep = (TextView) findViewById(R.id.tv_step);

        stepDector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if(stepDector ==null){
            Toast.makeText(this, "No Step Detect Sensor", Toast.LENGTH_SHORT).show();
        }


        Log.e("floorHBR", String.valueOf(floorHBR));
        Log.e("ceilHBR", String.valueOf(ceilHBR));

        speedHandler = new Handler();

        changeSpeed = new Thread(){
          @Override
          public void run(){
              while(isJogging){
                  try {
                      speedHandler.post(new Runnable() {
                          @Override
                          public void run() {
                              if (currentHBR < floorHBR) {
                                  Log.e("실시간 페이스조절", "노래 속도 상향 변주 중");
                                  player.setPlaybackParameters(new PlaybackParameters(1.1f));
                              } else if (currentHBR > ceilHBR) {
                                  Log.e("실시간 페이스조절", "노래 속도 하향 변주 중");
                                  player.setPlaybackParameters(new PlaybackParameters(0.9f));
                              }

                          }
                      });
                      Thread.sleep(5000);
                  }catch(InterruptedException e){
                        e.printStackTrace();
                      return;
                  }
              }
          }
        };

        changeSpeed.start();
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    protected void onResume(){
        super.onResume();
        initializePlayer();
        Log.e("onResume", "재개 활동");
        sensorManager.registerListener(this, stepDector, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        initializePlayer();
        Log.e("onRestart", "리스타트 활동");
    }

    @Override
    protected void onStop(){
        super.onStop();
        releasePlayer();
        Log.e("onStop", "정지 활동");
        /*
        try {
            changeSpeed.join();
        } catch(InterruptedException e){
            e.printStackTrace();
        }

         */
    }

    @Override
    protected void onPause(){
        super.onPause();
        releasePlayer();
        Log.e("onPause", "일시정지 활동");
        sensorManager.unregisterListener(this);
        threadStop();
    }

    private void initializePlayer(){
        if(player == null){
            player = new SimpleExoPlayer.Builder(PaceControl.this).build();
            pcv.setPlayer(player);

            MediaItem mediaitem;
            List<MediaItem> mediaItems = new ArrayList<>();

            makePlayList(mediaItems);

            player.setMediaItems(mediaItems);
            player.prepare();
            player.seekTo(currentWindow, playBackPosition);
            player.setPlayWhenReady(true);
        }
    }

    private void threadStop(){
        isJogging=false;
    }

    private void releasePlayer(){
        if(player != null){
            currentWindow = player.getCurrentWindowIndex();
            playBackPosition = player.getCurrentPosition();
            player.setPlayWhenReady(true);
            player.stop();
            player.release();
            player = null;
        }
    }

    private void makePlayList(List<MediaItem> mediaitems){
        MediaItem mediaitem;
        Field[] raws = R.raw.class.getFields();
        for(int count=0; count < raws.length; count++){
            Log.e("Raw Asset", raws[count].getName());
            int musicID = this.getResources().getIdentifier(raws[count].getName(), "raw", this.getPackageName());
            Uri musicUri = RawResourceDataSource.buildRawResourceUri(musicID);
            mediaitem = MediaItem.fromUri(musicUri);
            mediaitems.add(mediaitem);
        }
    }


    /**
     * 초시계 구현
     */

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
        int mSec = msg.arg1 % 100;
        int sec = (msg.arg1 / 100) % 60;
        int min = (msg.arg1 / 100) / 60;
        int hour = (msg.arg1 % 3600) % 24;
        String result = String.format("%02d:%02d:%02d", min,sec,mSec);
        tvCount.setText(result);
        }
    };

    public class timeThread extends Thread{
        @Override
        public void run(){
            int i = 0;
            while(true){
                Message msg = new Message();
                msg.arg1 = i++;
                handler.sendMessage(msg);
                try{
                    Thread.sleep(10);
                } catch(InterruptedException e){
                    e.printStackTrace();
                    return;
                }
            }
        }
    }


    private void ProgInit(){
        this.progressBar = findViewById(R.id.progressBar);
    }


    /**
     * 거리계산 구현
     *
     */

    @Override
    public void onSensorChanged(SensorEvent event){
        if(event.sensor.getType()== Sensor.TYPE_STEP_DETECTOR){
            if(event.values[0] == 1.0f){
                mStepDector += event.values[0];
                tvStep.setText(String.valueOf(mStepDector));
                Log.e("One step", "Count");
            }
        } else if(event.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
            tvCount.setText(String.valueOf(event.values[0]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){}
}
