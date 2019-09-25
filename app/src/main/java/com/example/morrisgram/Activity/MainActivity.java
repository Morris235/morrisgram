package com.example.morrisgram.Activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
   private EditText email_login;
   private EditText pwd_login;

    //현재 로그인 된 유저 정보를 담을 변수
   private FirebaseUser currentUser;
    //이메일 비밀번호 로그인 모듈 변수
   private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        email_login = (EditText) findViewById(R.id.email_login);
        pwd_login = (EditText) findViewById(R.id.password_login);

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
        //로그인 완료 버튼 ->홈 화면으로 이동
        loginB=(Button)findViewById(R.id.fillLogB);
        loginB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = email_login.getText().toString().trim();
                String pwd = pwd_login.getText().toString().trim();

                firebaseAuth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(MainActivity.this, MainActivity.class);
                            Toast.makeText(MainActivity.this, "환영합니다 로그인 되었습니다.", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "로그인 오류", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                Intent intent = new Intent(MainActivity.this,Home.class);
                startActivity(intent);
            }
        });
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
    //로그인 되어있으면 메인페이지로 이동 <자동로그인>
    @Override
    public void onStart(){
        super.onStart();
        //check if user is signed in (non-null) and update UI accordingly.
        currentUser = firebaseAuth.getCurrentUser();
        if(currentUser!=null){
            startActivity(new Intent(MainActivity.this,Home.class));
            finish();
        }

    }
}
