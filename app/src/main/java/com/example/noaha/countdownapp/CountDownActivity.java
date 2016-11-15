package com.example.noaha.countdownapp;

import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class CountDownActivity extends AppCompatActivity {


    TextView textDay;
    TextView textHour;
    TextView textMin;
    TextView textSec;
    Button btnPause;
    Button btnBack;
    Button btnMusic;
    ProgressBar PB;


    int days;
    int hours;
    int mins;
    int sec;
    int sumTime;

    private boolean flagPause = false;//记录暂停
    private boolean flagStop = false;//记录取消
    private Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
    private MediaPlayer MP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_down);

        textDay = (TextView) findViewById(R.id.tv_day);
        textHour = (TextView) findViewById(R.id.tv_hour);
        textMin = (TextView) findViewById(R.id.tv_min);
        textSec = (TextView) findViewById(R.id.tv_sec);
        btnPause = (Button) findViewById(R.id.bt_pause);
        btnBack = (Button) findViewById(R.id.bt_cancel);
        btnMusic = (Button) findViewById(R.id.bt_music);

        PB = (ProgressBar) findViewById(R.id.pb);

        Intent intent = getIntent();
        days = intent.getIntExtra("day", 0);
        hours = intent.getIntExtra("hour", 0);
        mins = intent.getIntExtra("minute", 0);
        sec = intent.getIntExtra("second", 0);

        sumTime = hours * 60 * 60 + mins * 60 + sec;

        setFormattedText(textDay, days);
        setFormattedText(textHour, hours);
        setFormattedText(textMin, mins);
        setFormattedText(textSec, sec);

        final CountThread thread = new CountThread(sumTime, days);
        thread.start();

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!flagPause) {
                    flagPause = true;
                    btnPause.setText("继续");
                } else {
                    flagPause = false;
                    btnPause.setText("暂停");
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MP != null){
                    flagStop=true;
                    MP.stop();
                    MP.release();
                }

                finish();
            }
        });

        btnMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, false);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "选择铃声");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL);
                Uri pickedUri = RingtoneManager.getActualDefaultRingtoneUri(CountDownActivity.this, RingtoneManager.TYPE_ALARM);
                if (pickedUri != null) {
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, pickedUri);
                    ringUri = pickedUri;
                }
                startActivityForResult(intent, 1);
            }
        });
    }

    protected void onDestroy() {

        super.onDestroy();
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case 1:
                //获取选中的铃声的URI
                Uri pickedURI = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                ringUri = pickedURI;
                break;

            default:
                break;
        }
    }


    public void setFormattedText(TextView tv, int num) {
        if (num >= 0 && num < 10)
            tv.setText("0" + num);
        else
            tv.setText(num + "");
    }



    public class CountThread extends Thread {
        private int sec=0, days=0;

        CountThread(){

        }

        CountThread(int sec, int days) {
            this.sec = sec;
            this.days = days;
        }

        @Override
        public void run() {

            int d = days;
            int s = sec;
            int c = 0;

            if(d==0&&s==0){
                Looper.prepare();
                Toast.makeText(CountDownActivity.this, "倒计时不能为空。",Toast.LENGTH_SHORT).show();
                Looper.loop();
            }else{
                while (sec >= 0 || days > 0) {


                    int count = 1000/(d*24*60*60+s);


                    try {
                        while (flagPause) {  //判断暂停
                            sleep(1000);
                        }

                        Message msg = new Message();
                        msg.arg1 = sec;
                        msg.arg2 = days;

                        CountDownActivity.this.handler.sendMessage(msg);


                        if (sec == 0 && days == 0) {
                            try {
                                if (ringUri != null) {
                                    MP = MediaPlayer.create(CountDownActivity.this, ringUri);

                                    if (MP != null) {
                                        MP.stop();
                                    }

                                    MP.prepare();
                                    PB.setProgress(1000);
                                    MP.start();

                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        }

                        if (sec == 0) {
                            days--;
                            sec = 24 * 60 * 60;
                        }
                        sec -= 1;


                        PB.setProgress(c);
                        c += count;
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    //点击返回键不退出程序，程序后台运行
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        PackageManager pm = getPackageManager();
        ResolveInfo homeInfo =
                pm.resolveActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME), 0);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ActivityInfo ai = homeInfo.activityInfo;
            Intent startIntent = new Intent(Intent.ACTION_MAIN);
            startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            startIntent.setComponent(new ComponentName(ai.packageName, ai.name));
            startActivitySafely(startIntent);
            return true;
        } else
            return super.onKeyDown(keyCode, event);
    }

    private void startActivitySafely(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException | SecurityException e) {
            Toast.makeText(this, "null",
                    Toast.LENGTH_SHORT).show();
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            setFormattedText(textDay, msg.arg2);
            setFormattedText(textHour, msg.arg1 / 3600);
            setFormattedText(textMin, msg.arg1 % 3600 / 60);
            setFormattedText(textSec, msg.arg1 % 3600 % 60);
        }
    };

}