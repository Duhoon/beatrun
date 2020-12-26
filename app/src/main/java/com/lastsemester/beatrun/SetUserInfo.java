package com.lastsemester.beatrun;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.SpannedString;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Pattern;


public class SetUserInfo extends AppCompatActivity {

    EditText etWeight; EditText etHeight; EditText etAge;
    Button btNext;
    Button btReset;

    public final String PREFERENCE = "UserInfo";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setuserinfo);


        etWeight = (EditText) findViewById(R.id.et_weight);
        etHeight = (EditText) findViewById(R.id.et_height);
        etAge = (EditText) findViewById(R.id.et_age);

        etWeight.setFilters(new InputFilter[]{filterNum});
        etHeight.setFilters(new InputFilter[]{filterNum});
        etAge.setFilters(new InputFilter[]{filterNum});

        btNext = (Button) findViewById(R.id.bt_next);
        btReset = (Button) findViewById(R.id.bt_reset);

        SharedPreferences pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE);

        if(pref.contains("weight")) {
            etWeight.setText(String.valueOf(pref.getInt("weight", 0)));
            etHeight.setText(String.valueOf(pref.getInt("height", 0)));
            etAge.setText(String.valueOf(pref.getInt("age", 0)));
        }

        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int weight = Integer.parseInt(etWeight.getText().toString());
                int height = Integer.parseInt(etHeight.getText().toString());
                int age = Integer.parseInt(etAge.getText().toString());

                /**
                 * Shared Reference save
                 */

                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("weight", weight);
                editor.putInt("height", height);
                editor.putInt("age", age);
                editor.commit();


                Intent intent = new Intent(getApplicationContext(), InitJog.class);
                startActivity(intent);
            }
        });

        btReset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                etWeight.setText(null);
                etHeight.setText(null);
                etAge.setText(null);
            }
        });



    }

    protected InputFilter filterNum = new InputFilter(){
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend){
            Pattern ps = Pattern.compile("^[0-9]{1,3}");
            if(!ps.matcher(source).matches()){
                return "";
            }
            return null;
        }
    };
}
