package com.example.morrisgram.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.example.morrisgram.R;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

public class Home extends AppCompatActivity implements SwipyRefreshLayout.OnRefreshListener {
    SwipyRefreshLayout mSwipeRefreshLayout;
    ImageButton myinfoB;
    ImageButton searchB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
//-----------------------------------화면이동----------------------------------------
        //탐색화면 이동
        searchB = (ImageButton) findViewById(R.id.searchB_home);
        searchB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this,Search.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
        //내 프로필 화면 이동
        myinfoB=(ImageButton)findViewById(R.id.myB_home);
        myinfoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this,Myinfo.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
        //좋아요 알람 화면 이동
        ImageButton likealarmB;
        likealarmB=(ImageButton)findViewById(R.id.likeB_home);
        likealarmB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this,LikeAlarm.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
        //채팅 화면 이동
        ImageButton messageB;
        messageB=(ImageButton)findViewById(R.id.messageB);
        messageB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this,MessageTerminal.class);
                startActivity(intent);
            }
        });
//---------------------------------------------------------------------------------
        mSwipeRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.refresh_home);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTH);
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        Log.d("MainActivity", "Refresh triggered at "
                + (direction == SwipyRefreshLayoutDirection.TOP ? "top" : "bottom"));
        mSwipeRefreshLayout.setRefreshing(false);
    }
//    @Override
//  public void onPause(){
//       super.onPause();
//        overridePendingTransition(0,0);
//    }
}
