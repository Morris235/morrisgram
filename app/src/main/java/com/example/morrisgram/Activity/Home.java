package com.example.morrisgram.Activity;

import android.Manifest;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.morrisgram.Activity.BaseActivity.AddingPoster_BaseAct;
import com.example.morrisgram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

public class Home extends AddingPoster_BaseAct implements SwipyRefreshLayout.OnRefreshListener {
    SwipyRefreshLayout mSwipeRefreshLayout;
    private ImageButton myinfoB;
    private ImageButton searchB;
    private ImageButton addposterB;
    private ImageButton likealarmB;
    private ImageButton messageB;

    private FirebaseAuth firebaseAuth;
    //현재 로그인 된 유저 정보를 담을 변수
    private FirebaseUser currentUser;

    //카메라 퍼미션
    private final int MY_PERMISSIONS_REQUEST_CAMERA=1;
    //갤러리 퍼미션
    String[] permission_list = {
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_CONTACTS
    };

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
                startActivity(intent);
//                finish();
                overridePendingTransition(0, 0);
            }
        });
        //좋아요 알람 화면 이동
        likealarmB = (ImageButton) findViewById(R.id.likeB_home);
        likealarmB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, LikeAlarm.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
        //채팅 화면 이동
        messageB = (ImageButton) findViewById(R.id.messageB);
        messageB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, MessageTerminal.class);
                startActivity(intent);
            }
        });

        //포스팅 화면 이동
        ImageButton addposterB = (ImageButton) findViewById(R.id.addB_home);
        addposterB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAlbum();
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
    // 권한 요청
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("사진", "onRequestPermissionsResult");
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Log.d("사진", "Permission: " + permissions[0] + "was " + grantResults[0]);
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.i("파베","홈 스타트");
        }
    public void onResume(){
        super.onResume();
        Log.i("파베","홈 리즈메");
        // 6.0 마쉬멜로우 이상일 경우에는 권한 체크 후 권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d("", "권한 설정 완료");
            } else {
                Log.d("", "권한 설정 요청");
                ActivityCompat.requestPermissions(Home.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        int permssionCheck = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA);

        if (permssionCheck!= PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this,"권한 승인이 필요합니다",Toast.LENGTH_LONG).show();

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                Toast.makeText(this,"사용을 위해 카메라 권한이 필요합니다.",Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
                Toast.makeText(this,"사용을 위해 카메라 권한이 필요합니다.",Toast.LENGTH_LONG).show();
            }
        }
    }
    public void onPause(){
        super.onPause();
        Log.i("파베","홈 포즈");
//        overridePendingTransition(0,0);
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