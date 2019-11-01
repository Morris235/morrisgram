package com.example.morrisgram.Activity.FollowFragment;

import android.content.Intent;

import com.example.morrisgram.Activity.BaseActivity.AddingPoster_BaseAct;
import com.example.morrisgram.Activity.Home;
import com.example.morrisgram.Activity.LikeAlarm;
import com.example.morrisgram.Activity.Myinfo;
import com.example.morrisgram.Activity.Search;
import com.example.morrisgram.DTOclass.Firebase.PostingDTO;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.morrisgram.R;
import com.example.morrisgram.Adapter.ViewPagerAdapter.ViewPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FollowPager extends AddingPoster_BaseAct {
    private ViewPager viewPager;

    //데이터베이스의 주소를 지정 필수
    private DatabaseReference mdataref = FirebaseDatabase.getInstance().getReference();

    //현재 접속중인 유저UID가져오기
    private FirebaseUser uid = FirebaseAuth.getInstance().getCurrentUser();
    private String userUID = uid.getUid();

    private String followernum;
    private String followingnum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 화면을 portrait(세로) 화면으로 고정하고 싶은 경우
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_follow_pager);


        //닉네임TV
        final TextView nicknameTV = (TextView) findViewById(R.id.idtv_followpager);
        mdataref.child("UserList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //현재 로그인된 유저 정보와 일치하는 데이터를 가져오기.
                String NameVal = (String) dataSnapshot.child(userUID).child("UserInfo").child("NickName").getValue();
                nicknameTV.setText(NameVal);

                followernum = String.valueOf((int)dataSnapshot.child(userUID).child("FollowerList").getChildrenCount());
                followingnum = String.valueOf((int)dataSnapshot.child(userUID).child("FollowingList").getChildrenCount());

                //뷰페이저 바인드
                viewPager = (ViewPager) findViewById(R.id.viewpager);

                //탭메뉴 바인드
                final TabLayout mTab = (TabLayout) findViewById(R.id.tabs);
                mTab.addTab(mTab.newTab().setText("팔로워 "+""+followernum+"명"));
                mTab.addTab(mTab.newTab().setText("팔로잉 "+""+followingnum+"명"));
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

                //팔러워 & 팔로잉의 쿼리를 위한 유저UID
                Intent intent = getIntent();

                String UserUID = intent.getStringExtra("PoseterUserUID");
                int FLAG = intent.getIntExtra("FLAG",-1);
                Log.i("뷰페이저","유저UID 확인 : "+UserUID);
                Log.i("뷰페이저","플래그 확인 : "+FLAG);

                ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(),mTab.getTabCount(),UserUID,FLAG);
                viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTab));
                viewPager.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });



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
    }
    @Override
    public void onPause(){
        super.onPause();
        overridePendingTransition(0,0);
    }
}
