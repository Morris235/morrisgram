package com.example.morrisgram.Activity;

import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.morrisgram.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
   private TextView signupB;
   private Button loginB;
   private Button unfillB;

   private EditText email_login;
   private EditText pwd_login;

    //현재 로그인 된 유저 정보를 담을 변수
   private FirebaseUser currentUser;
    //이메일 비밀번호 로그인 모듈 변수
   private FirebaseAuth firebaseAuth;
   //로딩 프로그래스바 변수
   private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 화면을 portrait(세로) 화면으로 고정하고 싶은 경우
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        Log.i("파베","로그인 크리에이트");
        unfillB = (Button)findViewById(R.id.UnfillLoginB);
        loginB=(Button)findViewById(R.id.fillLogB);

        //버튼 기본 상태
        unfillB.setVisibility(View.VISIBLE);
        loginB.setVisibility(View.INVISIBLE);

        //아이디 공란 검사
        email_login = (EditText) findViewById(R.id.email_login);
        email_login.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //입력되는 텍스트에 변화가 있을 때
                if(!email_login.getText().toString().equals("") && !pwd_login.getText().toString().equals("")){
                    unfillB.setVisibility(View.INVISIBLE);
                    loginB.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //입력이 끝났을 때
                if(email_login.getText().toString().equals("") || pwd_login.getText().toString().equals("")){
                    unfillB.setVisibility(View.VISIBLE);
                    loginB.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //입력하기 전에
            }
        });

        //비밀번호 공란 검사
        pwd_login = (EditText) findViewById(R.id.password_login);
        pwd_login.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //입력되는 텍스트에 변화가 있을 때
                if(!email_login.getText().toString().equals("") && !pwd_login.getText().toString().equals("")){
                    unfillB.setVisibility(View.INVISIBLE);
                    loginB.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //입력이 끝났을 때
                if(email_login.getText().toString().equals("") || pwd_login.getText().toString().equals("")){
                    unfillB.setVisibility(View.VISIBLE);
                    loginB.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //입력하기 전에
            }
        });

        //회원가입 페이지로 이동
        signupB=(TextView)findViewById(R.id.signupB);
        signupB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SignUp.class);
                startActivity(intent);
            }
        });

        //파이어 베이스 인스턴스
        firebaseAuth = firebaseAuth.getInstance();

        //로그인 버튼
        loginB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 String email = email_login.getText().toString().trim();
                 String pwd = pwd_login.getText().toString().trim();

                    firebaseAuth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(MainActivity.this, Home.class);
                                Toast.makeText(MainActivity.this, "환영합니다 로그인 되었습니다.", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(intent);
                            } else {
                                Toast.makeText(MainActivity.this, "존재하지 않는 계정입니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            }
        });
        //더블탭
//        final GestureDetector gd = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
//            @Override
//            public boolean onDoubleTap(MotionEvent e) {
//
//                Log.d("OnDoubleTapListener", "onDoubleTap");
//                return true;
//            }
//
//            @Override
//            public void onLongPress(MotionEvent e) {
//                super.onLongPress(e);
//            }
//
//            @Override
//            public boolean onDoubleTapEvent(MotionEvent e) {
//                //더블탭 했을 때의 동작
//                return true;
//            }
//
//            @Override
//            public boolean onDown(MotionEvent e) {
//                return true;
//            }
//            });
//
//        signupB.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return gd.onTouchEvent(event);
//            }
//        });
    }
    public void loading(long time) {
        //로딩
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        progressDialog = new ProgressDialog(MainActivity.this);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("잠시만 기다려 주세요");
                        progressDialog.show();
                    }
                }, time);
    }

    public void loadingEnd() {
        new android.os.Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                }, 0);
    }
    //로그인 되어있으면 메인페이지로 이동 <자동로그인>
    //조건문 보완필요
    @Override
    public void onStart(){
        super.onStart();
        //check if user is signed in (non-null) and update UI accordingly.
        //파베에 값은 있으니까 미리 실행이 되어버림 그래서 처음 로그인 할 때 홈 액티비티가 두개가 뜬다.
        currentUser = firebaseAuth.getCurrentUser();
        if(currentUser!=null){
            Log.i("파베","스타트 로그인 확인");
            startActivity(new Intent(MainActivity.this,Home.class));
            finish();
        }else {
            //로그인된 유저가 없음
            Log.i("파베","스타트 로그인 없음");
        }
    }
    public void onResume(){
        super.onResume();
        Log.i("파베","로그인 리즈메");
    }
    public void onPause(){
        super.onPause();
        overridePendingTransition(0,0);
        Log.i("파베","로그인 포즈");
    }
    public void onStop(){
        super.onStop();
        Log.i("파베","로그인 스탑");
    }
    public void onDestroy(){
        super.onDestroy();
        Log.i("파베","로그인 디스트로이");
    }
    public void onRestart(){
        super.onRestart();
        Log.i("파베","로그인 리스타트");
    }
}
