package com.example.morrisgram.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.morrisgram.R;

public class Search extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
//-----------------------------------화면이동----------------------------------------
//홈 화면 이동
        ImageButton homeB;
        homeB=(ImageButton)findViewById(R.id.homeB_search);
        homeB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Search.this, Home.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
//좋아요 알람 화면 이동
        ImageButton likealarmB;
        likealarmB=(ImageButton)findViewById(R.id.likeB_search);
        likealarmB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Search.this, LikeAlarm.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });

//내 프로필 화면 이동
        ImageButton myinfoB;
        myinfoB=(ImageButton)findViewById(R.id.myB_search);
        myinfoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Search.this, Myinfo.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
//---------------------------------------------------------------------------------
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_search);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }
    //새로고침
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
