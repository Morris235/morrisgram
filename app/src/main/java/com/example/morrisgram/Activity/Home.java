package com.example.morrisgram.Activity;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.morrisgram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

public class Home extends AppCompatActivity implements SwipyRefreshLayout.OnRefreshListener {
    SwipyRefreshLayout mSwipeRefreshLayout;
    ImageButton myinfoB;
    ImageButton searchB;
    private FirebaseAuth firebaseAuth;
    //현재 로그인 된 유저 정보를 담을 변수
    private FirebaseUser currentUser;

    Button logoutB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 화면을 portrait(세로) 화면으로 고정하고 싶은 경우
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_home);

        firebaseAuth = FirebaseAuth.getInstance();

        Log.i("파베","홈 크리에이트");
//-----------------------------------화면이동----------------------------------------
        //탐색화면 이동
        searchB = (ImageButton) findViewById(R.id.searchB_home);
        searchB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Search.class);
                finish();
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
        //내 프로필 화면 이동
        myinfoB = (ImageButton) findViewById(R.id.myB_home);
        myinfoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Myinfo.class);
                finish();
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
        //좋아요 알람 화면 이동
        ImageButton likealarmB;
        likealarmB = (ImageButton) findViewById(R.id.likeB_home);
        likealarmB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, LikeAlarm.class);
                finish();
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
        //채팅 화면 이동
        ImageButton messageB;
        messageB = (ImageButton) findViewById(R.id.messageB);
        messageB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, MessageTerminal.class);
                finish();
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

    @Override
    public void onStart(){
        super.onStart();
        Log.i("파베","홈 스타트");
        }
    public void onResume(){
        super.onResume();
        Log.i("파베","홈 리즈메");
    }
    public void onPause(){
        super.onPause();
        overridePendingTransition(0,0);
        Log.i("파베","홈 포즈");
    }
    public void onStop(){
        super.onStop();
        Log.i("파베","홈 스탑");
    }
    public void onDestroy(){
        super.onDestroy();
        Log.i("파베","홈 디스트로이");
    }
    public void onRestart(){
        super.onRestart();
        Log.i("파베","홈 리스타트");
    }
}