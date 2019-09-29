package com.example.morrisgram.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.morrisgram.R;

public class LikeAlarm extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_like_alarm);
//-----------------------------------화면이동----------------------------------------
//홈 화면 이동
        ImageButton homeB;
        homeB=(ImageButton)findViewById(R.id.homeB_like);
        homeB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LikeAlarm.this, Home.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
//탐색 화면 이동
        ImageButton searchB;
        searchB=(ImageButton)findViewById(R.id.searchB_like);
        searchB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LikeAlarm.this, Search.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
//내 프로필 화면 이동
        ImageButton myinfoB;
        myinfoB=(ImageButton)findViewById(R.id.myB_like);
        myinfoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LikeAlarm.this, Myinfo.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
//---------------------------------------------------------------------------------
    }
    @Override
    public void onPause(){
        super.onPause();
        overridePendingTransition(0,0);
    }
}
