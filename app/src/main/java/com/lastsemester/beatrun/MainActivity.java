package com.lastsemester.beatrun;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button btJog;
    private Button btData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btJog = (Button) findViewById(R.id.bt_jog);
        btData = (Button) findViewById(R.id.bt_person);

        btJog.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), SetUserInfo.class);
                startActivity(intent);
            }
        });

        btData.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), JogList.class);
                startActivity(intent);
            }
        });
    }
}