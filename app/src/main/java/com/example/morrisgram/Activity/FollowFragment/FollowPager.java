package com.example.morrisgram.Activity.FollowFragment;

import android.content.Intent;

import com.example.morrisgram.Activity.BaseActivity.AddingPoster_BaseAct;
import com.example.morrisgram.Activity.Home;
import com.example.morrisgram.Activity.LikeAlarm;
import com.example.morrisgram.Activity.Myinfo;
import com.example.morrisgram.Activity.Search;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.morrisgram.R;
import com.example.morrisgram.Adapter.ViewPagerAdapter.ViewPagerAdapter;

public class FollowPager extends AddingPoster_BaseAct {
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 화면을 portrait(세로) 화면으로 고정하고 싶은 경우
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_follow_pager);



//-----------------------------------화면이동----------------------------------------
//홈 화면 이동
        ImageButton homeB;
        homeB=(ImageButton)findViewById(R.id.homeB_follow);
        homeB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FollowPager.this, Home.class);
                startActivity(intent);
            }
        });
//탐색 화면 이동
        ImageButton searchB;
        searchB=(ImageButton)findViewById(R.id.searchB_follow);
        searchB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FollowPager.this, Search.class);
                startActivity(intent);
            }
        });
//좋아요 알람 화면 이동
        ImageButton likealarmB;
        likealarmB=(ImageButton)findViewById(R.id.likeB_follow);
        likealarmB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FollowPager.this, LikeAlarm.class);
                startActivity(intent);
            }
        });

//내 프로필 화면 이동
        ImageButton myinfoB;
        myinfoB=(ImageButton)findViewById(R.id.myB_follow);
        myinfoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FollowPager.this, Myinfo.class);
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

        //뷰페이저 바인드
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        //탭메뉴 바인드
        final TabLayout mTab = (TabLayout) findViewById(R.id.tabs);
        mTab.addTab(mTab.newTab().setText("팔로워 0명"));
        mTab.addTab(mTab.newTab().setText("팔로잉 0명"));
        mTab.setTabGravity(TabLayout.GRAVITY_FILL);


        //뷰페이저에 어댑터 지정 deprected
        mTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(),mTab.getTabCount());
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTab));
        viewPager.setAdapter(adapter);
//        //뷰페이저 어댑터
//        mTab.setupWithViewPager(viewPager);
    }


    @Override
    public void onPause(){
        super.onPause();
        overridePendingTransition(0,0);
    }
}
