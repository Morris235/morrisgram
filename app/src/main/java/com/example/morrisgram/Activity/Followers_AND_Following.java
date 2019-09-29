package com.example.morrisgram.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.morrisgram.R;
import com.example.morrisgram.Adapter.ViewPagerAdapter.ViewPagerAdapter;

public class Followers_AND_Following extends AppCompatActivity {
    private ViewPager viewPager;
    private ViewPagerAdapter pagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers__and__following);

//-----------------------------------화면이동----------------------------------------
//홈 화면 이동
        ImageButton homeB;
        homeB=(ImageButton)findViewById(R.id.homeB_follow);
        homeB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Followers_AND_Following.this, Home.class);
                startActivity(intent);
            }
        });
//탐색 화면 이동
        ImageButton searchB;
        searchB=(ImageButton)findViewById(R.id.searchB_follow);
        searchB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Followers_AND_Following.this, Search.class);
                startActivity(intent);
            }
        });
//좋아요 알람 화면 이동
        ImageButton likealarmB;
        likealarmB=(ImageButton)findViewById(R.id.likeB_follow);
        likealarmB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Followers_AND_Following.this, LikeAlarm.class);
                startActivity(intent);
            }
        });

//내 프로필 화면 이동
        ImageButton myinfoB;
        myinfoB=(ImageButton)findViewById(R.id.myB_follow);
        myinfoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Followers_AND_Following.this, Myinfo.class);
                startActivity(intent);
            }
        });

        ImageButton backB;
        backB=(ImageButton)findViewById(R.id.backB_follow);
        backB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//---------------------------------------------------------------------------------

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        pagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        TabLayout mTab = (TabLayout) findViewById(R.id.tabs);
        mTab.setupWithViewPager(viewPager);
    }
    @Override
    public void onPause(){
        super.onPause();
        overridePendingTransition(0,0);
    }
}
