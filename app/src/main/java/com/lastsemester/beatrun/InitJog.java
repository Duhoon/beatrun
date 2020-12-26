package com.lastsemester.beatrun;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class InitJog extends AppCompatActivity {
    private final boolean checkTime = true;
    private final boolean checkDist = false;

    private SeekBar sbStr;
    private TextView tvStr;
    private Button btToJog;
    private Switch swDistTime;
    private NumberPicker npInt;

    int strength = 1;

    private boolean choose ;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initjog);

        swDistTime = (Switch) findViewById(R.id.time_dist_switch);

        swDistTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    choose = checkTime;
                }else{
                    choose = checkDist;
                }
            }
        });

        /**
         * numberPicker
         */
        npInt = (NumberPicker) findViewById(R.id.np_dist);
        npInt.setMinValue(0);
        npInt.setMaxValue(99);
        npInt.setValue(15);
        npInt.setOnValueChangedListener(new NumberPicker.OnValueChangeListener(){
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
            }
        });



        // SeekBar 강도 상호작용
        sbStr = (SeekBar) findViewById(R.id.sbStr);
        tvStr = (TextView) findViewById(R.id.textStr);

        sbStr.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(progress<33){
                        tvStr.setText("하");
                        strength = 1;
                    } else if(progress<66){
                        tvStr.setText("중");
                        strength = 2;
                    }
                    else {
                        tvStr.setText("상");
                        strength = 3;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });




        btToJog = (Button) findViewById(R.id.initBtn);
        btToJog.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), PaceControl.class);

                String strStr = (String) tvStr.getText().toString();

                int intDist = npInt.getValue();
                int valueTime = 0;

                intent.putExtra("valueDist", intDist);
                intent.putExtra("valueTime", valueTime);
                intent.putExtra("strength", strength);

                startActivity(intent);
            }
        });
    }
}
