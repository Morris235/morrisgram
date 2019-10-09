package com.example.morrisgram.Activity;

import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.morrisgram.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Myinfo extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,NavigationView.OnNavigationItemSelectedListener{
   private SwipeRefreshLayout mSwipeRefreshLayout;
   private ImageButton homeB;
   private ImageButton optionB;
   private Button profilemodifyB;
   private TextView hname;
   private TextView pname;
   private TextView idtv;
   private TextView intro;
   private TextView website;

   private DrawerLayout mdrawerLayout;
   private ActionBarDrawerToggle mtoggle;

    //데이터베이스의 주소를 지정 필수
    private DatabaseReference mdataref = FirebaseDatabase.getInstance().getReference("UserList");
    //현재 접속중인 유저UID가져오기
    private FirebaseUser uid = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseAuth firebaseAuth;
    private String userUID = uid.getUid();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 화면을 portrait(세로) 화면으로 고정하고 싶은 경우
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_myinfo);

        //네비게이션 드로우바
        mdrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mtoggle = new ActionBarDrawerToggle(this,mdrawerLayout,R.string.open,R.string.close);
        mdrawerLayout.addDrawerListener(mtoggle);
        mtoggle.syncState();

        //내 정보 화면에 표시할 텍스트들
        optionB = (ImageButton) findViewById(R.id.optionB_my);
        hname = (TextView) findViewById(R.id.username_header);
        pname = (TextView) findViewById(R.id.name);
        idtv = (TextView) findViewById(R.id.idtv_my);
        website = (TextView) findViewById(R.id.website_my);
        intro = (TextView) findViewById(R.id.introduce_my);

        //네비게이션뷰 리스너
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationview);
        navigationView.setNavigationItemSelectedListener(this);
        //네비게이션바 버튼
        optionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdrawerLayout.openDrawer(GravityCompat.END);
            }
        });

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

        //처음 앱을 실행하고 버튼을 눌렀을 때만 값을 읽어옴 addListenerForSingleValueEvent
        //수시로 해당 디비의 하위값들이 변화를 감지하고 그떄마다 값을 불러오려면 addValueEventListener를 사용
        mdataref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //현재 로그인된 유저 정보와 일치하는 데이터를 가져오기.
                String NameVal = (String) dataSnapshot.child(userUID).child("UserInfo").child("NickName").getValue();
                String WebsiteVal = (String) dataSnapshot.child(userUID).child("Profile").child("Website").getValue();
                String IntroVal = (String) dataSnapshot.child(userUID).child("Profile").child("Introduce").getValue();

                pname.setText(NameVal);
                idtv.setText(NameVal);
                website.setText(WebsiteVal);
                intro.setText(IntroVal);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(mtoggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.logout_navi){
            //로그아웃
            Toast.makeText(Myinfo.this,"로그아웃 되었습니다.",Toast.LENGTH_LONG).show();
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signOut(); finish(); Intent intent1 = new Intent(getApplicationContext(),MainActivity.class); startActivity(intent1);
        }

        if(id == R.id.leave_navi){
            //회원탈퇴
            AlertDialog.Builder alert_confirm = new AlertDialog.Builder(Myinfo.this);
            alert_confirm.setMessage("계정을 삭제 하시겠습니까?").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    uid = FirebaseAuth.getInstance().getCurrentUser();
                    uid.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(Myinfo.this,"계정이 삭제 되었습니다.",Toast.LENGTH_LONG).show();
                            //데이터 삭제
                            mdataref.child(userUID).setValue(null);
                            //로그아웃 처리 & 회원탈퇴 처리
                            uid.delete();

                            firebaseAuth = FirebaseAuth.getInstance();
                            firebaseAuth.signOut();
                            finish();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }
                    });
                }
            });

            alert_confirm.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(Myinfo.this,"취소",Toast.LENGTH_LONG).show();
                }
            });
            alert_confirm.show();
        }

        if(id == R.id.setting_navi){
            //설정
        }

        return false;
    }
    //뒤로가기 버튼으로 네비게이션 닫기
    @Override
    public void onBackPressed(){
        if(mdrawerLayout.isDrawerOpen(GravityCompat.END)){
            mdrawerLayout.closeDrawers();
        }else{
            super.onBackPressed();
        }
    }
    //네비게이션 드로어 메소드
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    public void onStart(){
        super.onStart();
        Log.i("파베","마이 스타트");
    }
    public void onResume(){
        super.onResume();
        Log.i("파베","마이 리즈메");
    }
    //애니메이션 효과 지우기
    @Override
    public void onPause(){
        super.onPause();
        overridePendingTransition(0,0);
        Log.i("파베","마이 포즈");
    }
    public void onStop(){
        super.onStop();
        Log.i("파베","마이 스탑");
    }
    public void onDestroy(){
        super.onDestroy();
        Log.i("파베","마이 디스트로이");
    }
    public void onRestart(){
        super.onRestart();
        Log.i("파베","마이 리스타트");
    }
}
