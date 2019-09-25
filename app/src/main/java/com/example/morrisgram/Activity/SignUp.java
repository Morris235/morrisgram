package com.example.morrisgram.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
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

import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {
    private EditText email_join;
    private EditText pwd_join;
    private TextView gotologin;
    private Button btn;

    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        email_join = (EditText) findViewById(R.id.email_signup);
        pwd_join = (EditText) findViewById(R.id.password_signup);
        btn = (Button) findViewById(R.id.fillSignupB);
        gotologin = (TextView) findViewById(R.id.gotologin);

        //firebaseAuth의 인스턴스를 가져온다.
        firebaseAuth = FirebaseAuth.getInstance();

        //회원가입 완료
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = email_join.getText().toString().trim();
                String pwd = pwd_join.getText().toString().trim();

                //이메일 형식 체크
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(SignUp.this,"이메일 형식이 아닙니다.",Toast.LENGTH_SHORT).show();
                }
                //비밀번호 유효성 체크
                if(!Pattern.matches("^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-zA-Z]).{8,20}$", pwd)){
                    Toast.makeText(SignUp.this,"비밀번호 형식을 지켜주세요",Toast.LENGTH_SHORT).show();
                }

                //이메일 주소와 비밀번호를 createUserWithEmailAndPassword에 전달하여 신규 계정을 생성한다.
                firebaseAuth.createUserWithEmailAndPassword(email,pwd).addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //회원가입 성공시 메인 화면으로 전환되고
                        if(task.isSuccessful()){
                            Intent intent = new Intent(SignUp.this, MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(SignUp.this,"회원가입 완료",Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            //실패시 오류 토스트메세지를 띄어 오류알림
                            Toast.makeText(SignUp.this,"등록 에러: 이메일 형식과 비밀번호 형식을 지켜주세요",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
            }
        });
        //로그인 화면으로 이동 버튼
        gotologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
