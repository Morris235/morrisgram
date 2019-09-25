package com.example.morrisgram.Activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.morrisgram.R;

public class Myinfo extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    SwipeRefreshLayout mSwipeRefreshLayout;
    ImageButton homeB;
    ImageButton optionB;
    Button profilemodifyB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfo);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_my);
        mSwipeRefreshLayout.setOnRefreshListener(this);
//-----------------------------------화면이동----------------------------------------
        homeB = (ImageButton)findViewById(R.id.homeB_my);
        homeB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Myinfo.this,Home.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
        //탐색 화면 이동
        ImageButton searchB;
        searchB=(ImageButton)findViewById(R.id.searchB_my);
        searchB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Myinfo.this, Search.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
//좋아요 알람 화면 이동
        ImageButton likealarmB;
        likealarmB=(ImageButton)findViewById(R.id.likeB_my);
        likealarmB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Myinfo.this, LikeAlarm.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
        //팔로워 버튼 페이지 이동
        ViewGroup followersB = (ViewGroup) findViewById(R.id.FollowerTV_my);
        followersB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Myinfo.this,Followers_AND_Following.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });

        //팔로잉 버튼 페이지 이동
        ViewGroup followingsB = (ViewGroup) findViewById(R.id.FollowingTV_my);
        followingsB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Myinfo.this,Followers_AND_Following.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
//---------------------------------------------------------------------------------

        profilemodifyB = (Button)findViewById(R.id.profileB);
        profilemodifyB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Myinfo.this,ProfileModify.class);
                startActivity(intent);
            }
        });
        //네비게이션바 테스트
        optionB = (ImageButton)findViewById(R.id.optionB);
        optionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onPause(){
        super.onPause();
        overridePendingTransition(0,0);
    }
}
