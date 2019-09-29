package com.example.morrisgram.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.morrisgram.R;

public class UserProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);
//-----------------------------------화면이동----------------------------------------
//홈 화면 이동
        ImageButton homeB;
        homeB=(ImageButton)findViewById(R.id.homeB_user);
        homeB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfile.this, Home.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
//탐색 화면 이동
        ImageButton searchB;
        searchB=(ImageButton)findViewById(R.id.searchB_user);
        searchB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfile.this, Search.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
//좋아요 알람 화면 이동
        ImageButton likealarmB;
        likealarmB=(ImageButton)findViewById(R.id.likeB_user);
        likealarmB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfile.this, LikeAlarm.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });

//내 프로필 화면 이동
        ImageButton myinfoB;
        myinfoB=(ImageButton)findViewById(R.id.myB_user);
        myinfoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfile.this, Myinfo.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });

        //바로 해당유저에게 메세지를 보낼 수 있는 메세지창으로 이동
        Button messageB;
        messageB=(Button)findViewById(R.id.messageB_user);
        messageB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfile.this,MessageWindow.class);
            }
        });
//---------------------------------------------------------------------------------
    }
}
