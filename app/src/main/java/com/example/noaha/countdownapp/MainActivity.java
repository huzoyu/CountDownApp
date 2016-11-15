package com.example.noaha.countdownapp;

import android.content.DialogInterface;
import android.content.Intent;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String[] PLANETS_FOR_DAY;
    private String[] PLANETS_FOR_HOUR;
    private String[] PLANETS_FOR_MIN;
    private String[] PLANETS_FOR_SEC;

    Button btn1;
    Button btn2;
    Button btn3;

    Button btnCus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PLANETS_FOR_DAY = setNum(31);
        PLANETS_FOR_HOUR = setNum(24);
        PLANETS_FOR_MIN = setNum(60);
        PLANETS_FOR_SEC = setNum(60);

        btn1 = (Button) findViewById(R.id.bt_1);
        btn2 = (Button) findViewById(R.id.bt_2);
        btn3 = (Button) findViewById(R.id.bt_3);
        btnCus = (Button) findViewById(R.id.bt_cus);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);

        btnCus.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {



            case R.id.bt_1:
                sendtosecond(0, 0, 1, 0);
                break;
            case R.id.bt_2:
                sendtosecond(0, 0, 10, 0);
                break;
            case R.id.bt_3:
                sendtosecond(0, 1, 0, 0);
                break;

            case R.id.bt_cus: {


                LinearLayout outerView = (LinearLayout) getLayoutInflater().inflate(R.layout.wheelview_dialog, null);

                final WheelView wheelDay = (WheelView) outerView.findViewById(R.id.wv_day);
                final WheelView wheelHour = (WheelView) outerView.findViewById(R.id.wv_hour);
                final WheelView wheelMin = (WheelView) outerView.findViewById(R.id.wv_min);
                final WheelView wheelSec = (WheelView) outerView.findViewById(R.id.wv_sec);

                wheelDay.setOffset(1);
                wheelDay.setItems(Arrays.asList(PLANETS_FOR_DAY));
                wheelDay.setSeletion(0);

                wheelHour.setOffset(1);
                wheelHour.setItems(Arrays.asList(PLANETS_FOR_HOUR));
                wheelHour.setSeletion(0);

                wheelMin.setOffset(1);
                wheelMin.setItems(Arrays.asList(PLANETS_FOR_MIN));
                wheelMin.setSeletion(0);

                wheelSec.setOffset(1);
                wheelSec.setItems(Arrays.asList(PLANETS_FOR_SEC));
                wheelSec.setSeletion(0);

                new AlertDialog.Builder(this)
                        .setTitle("自定义")
                        .setView(outerView)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                int day = wheelDay.getSeletedIndex();
                                int hour = wheelHour.getSeletedIndex();
                                int min = wheelMin.getSeletedIndex();
                                int sec = wheelSec.getSeletedIndex();
                                sendtosecond(day, hour, min, sec);
                            }
                        })
                        .show();
            }
            default:
        }

    }

    public void sendtosecond(int d, int h, int min, int s) {
        Intent intent = new Intent(MainActivity.this, CountDownActivity.class);
        intent.putExtra("day", d);
        intent.putExtra("hour", h);
        intent.putExtra("minute", min);
        intent.putExtra("second", s);
        startActivity(intent);
    }

    public String[] setNum(int n){
        String[] s = new String[n];
        for (int i = 0; i < n; i++) {
            s[i] = i + "";
        }
        return s;
    }
}
