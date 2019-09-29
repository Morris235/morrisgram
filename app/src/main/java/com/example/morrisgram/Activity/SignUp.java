package com.example.morrisgram.Activity;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.morrisgram.DTO_Classes.Firebase.Users_Signup;
import com.example.morrisgram.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {
    private EditText email_join;
    private EditText pwd_join;
    private EditText name_join;

    private TextView gotologin;
    private TextView checkE;
    private TextView checkP;

    private Button signupB;
    private Button unfillsignupB;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    //데이터 베이스 리퍼런스 생성, 데이터 베이스 인스턴스의 리퍼런스 입력
    //다른 탭 메뉴에 참조하기 위해 각각의 게시물 마다 UID(토큰)를 받아서 데이터 베이스에 명시해줘야 한다.
    private DatabaseReference DBref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_up);
        gotologin = (TextView) findViewById(R.id.gotologin);
        checkE = (TextView) findViewById(R.id.checkE);
        checkP = (TextView) findViewById(R.id.checkP);

        email_join = (EditText) findViewById(R.id.email_signup);
        pwd_join = (EditText) findViewById(R.id.password_signup);
        name_join = (EditText) findViewById(R.id.name_signup);

        signupB = (Button) findViewById(R.id.fillSignupB);
        unfillsignupB = (Button) findViewById(R.id.UnfillSignupB);


        //firebaseAuth의 인스턴스를 가져온다.
        firebaseAuth = FirebaseAuth.getInstance();

        //버튼 기본 상태
        unfillsignupB.setVisibility(View.VISIBLE);
        signupB.setVisibility(View.INVISIBLE);

        //유효성 텍스트 기본상태
        checkE.setVisibility(View.INVISIBLE);
        checkP.setVisibility(View.INVISIBLE);

        //아이디 공란 검사
        email_join.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //입력되는 텍스트에 변화가 있을 때
                //모든 형식에 부합하면 가입하기 버튼 가시화
                if(!email_join.getText().toString().equals("")
                        && !pwd_join.getText().toString().equals("")
                        && !name_join.getText().toString().equals("")
                        && Patterns.EMAIL_ADDRESS.matcher(email_join.getText().toString()).matches()
                        && Pattern.matches("^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-zA-Z]).{8,20}$", pwd_join.getText().toString())){

                    unfillsignupB.setVisibility(View.INVISIBLE);
                    signupB.setVisibility(View.VISIBLE);
                }else {
                    unfillsignupB.setVisibility(View.VISIBLE);
                    signupB.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //입력이 끝났을 때
                //회원가입 버튼 가시화를 공백체크 분기문
                if(email_join.getText().toString().equals("")
                        || pwd_join.getText().toString().equals("")
                        || name_join.getText().toString().equals("")
                        || !Patterns.EMAIL_ADDRESS.matcher(email_join.getText().toString()).matches()
                        || !Pattern.matches("^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-zA-Z]).{8,20}$", pwd_join.getText().toString())){

                    unfillsignupB.setVisibility(View.VISIBLE);
                    signupB.setVisibility(View.INVISIBLE);
                }else{
                    unfillsignupB.setVisibility(View.INVISIBLE);
                    signupB.setVisibility(View.VISIBLE);
                }

                //이메일 형식 체크 텍스트 표시 분기문
                if(!Patterns.EMAIL_ADDRESS.matcher(email_join.getText().toString()).matches()){
                    checkE.setVisibility(View.VISIBLE);
                }else{
                    checkE.setVisibility(View.INVISIBLE);
                }

            }
            @Override
            public void afterTextChanged(Editable s) {
                //입력하기 전에
            }
        });

        //비밀번호 공란 검사
        pwd_join.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //입력되는 텍스트에 변화가 있을 때
                //모든 형식에 부합하면 가입하기 버튼 가시화
                if(!email_join.getText().toString().equals("")
                        && !pwd_join.getText().toString().equals("")
                        && !name_join.getText().toString().equals("")
                        && Patterns.EMAIL_ADDRESS.matcher(email_join.getText().toString()).matches()
                        && Pattern.matches("^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-zA-Z]).{8,20}$", pwd_join.getText().toString())){

                    unfillsignupB.setVisibility(View.INVISIBLE);
                    signupB.setVisibility(View.VISIBLE);
                }else {
                    unfillsignupB.setVisibility(View.VISIBLE);
                    signupB.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //입력이 끝났을 때
                //모든 형식에 부합하지 않으면 가입하기 버튼 비가시화
                if(email_join.getText().toString().equals("")
                        || pwd_join.getText().toString().equals("")
                        || name_join.getText().toString().equals("")
                        || !Patterns.EMAIL_ADDRESS.matcher(email_join.getText().toString()).matches()
                        || !Pattern.matches("^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-zA-Z]).{8,20}$", pwd_join.getText().toString())){

                    unfillsignupB.setVisibility(View.VISIBLE);
                    signupB.setVisibility(View.INVISIBLE);
                }else{
                unfillsignupB.setVisibility(View.INVISIBLE);
                signupB.setVisibility(View.VISIBLE);
            }

                //비밀번호 유효성 체크 텍스트 표시 분기문
                if(!Pattern.matches("^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-zA-Z]).{8,20}$", pwd_join.getText().toString())){
                    checkP.setVisibility(View.VISIBLE);
                }else{
                    checkP.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                //입력하기 전에
            }
        });

        //이름 공란 검사
        name_join.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //입력되는 텍스트에 변화가 있을 때
                //모든 형식에 부합하면 가입하기 버튼 가시화
                if(!email_join.getText().toString().equals("")
                        && !pwd_join.getText().toString().equals("")
                        && !name_join.getText().toString().equals("")
                        && Patterns.EMAIL_ADDRESS.matcher(email_join.getText().toString()).matches()
                        && Pattern.matches("^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-zA-Z]).{8,20}$", pwd_join.getText().toString())){

                    unfillsignupB.setVisibility(View.INVISIBLE);
                    signupB.setVisibility(View.VISIBLE);
                }else {
                    unfillsignupB.setVisibility(View.VISIBLE);
                    signupB.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //입력이 끝났을 때
                //모든 형식에 부합하지 않으면 가입하기 버튼 비가시화
                if(email_join.getText().toString().equals("")
                        || pwd_join.getText().toString().equals("")
                        || name_join.getText().toString().equals("")
                        || !Patterns.EMAIL_ADDRESS.matcher(email_join.getText().toString()).matches()
                        || !Pattern.matches("^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-zA-Z]).{8,20}$", pwd_join.getText().toString())){

                    unfillsignupB.setVisibility(View.VISIBLE);
                    signupB.setVisibility(View.INVISIBLE);
                }else{
                    unfillsignupB.setVisibility(View.INVISIBLE);
                    signupB.setVisibility(View.VISIBLE);
                }

                //비밀번호 유효성 체크 텍스트 표시 분기문
                if(!Pattern.matches("^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-zA-Z]).{8,20}$", pwd_join.getText().toString())){
                    checkP.setVisibility(View.VISIBLE);
                }else{
                    checkP.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                //입력하기 전에
            }
        });

        //----------------------------------------------------------------회원가입 완료--------------------------------------------------------------------------------
        signupB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = email_join.getText().toString().trim();
                final String pwd = pwd_join.getText().toString().trim();
                final String name = name_join.getText().toString().trim();

                    //이메일 주소와 비밀번호를 createUserWithEmailAndPassword에 전달하여 신규 계정을 생성한다.
                    firebaseAuth.createUserWithEmailAndPassword(email,pwd).addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //회원가입 성공시 메인 화면으로 전환되고 & 데이터 베이스에 아이디,비밀번호,이름을 저장
                            if(task.isSuccessful()){
                                //가입성공시 데이터베이스에 입력한 정보 저장
                                FirebaseDatabase(true,email,pwd,name);
                                Intent intent = new Intent(SignUp.this, MainActivity.class);
                                startActivity(intent);
                                Toast.makeText(SignUp.this,"회원가입 완료",Toast.LENGTH_SHORT).show();
                                finish();
                            }else{
                                //실패시 오류 토스트메세지를 띄어 오류알림
                                Toast.makeText(SignUp.this,"회원가입 에러 : 가입이 불가능한 계정입니다.",Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    });
            }
        });
//        // Read from the database
//        DBref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        //로그인 화면으로 이동 버튼
        gotologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
    //데이터 베이스 업데이트 메소드<회원가입> - 아이디,비밀번호,이름
    //회원탈퇴시 데이터 삭제 구현해야 함
    public void FirebaseDatabase(boolean add, String email, String pwd, String Pname){
        DBref = FirebaseDatabase.getInstance().getReference();
        Map<String,Object> childUpdates = new HashMap<>();
        Map<String,Object> PostValues = null;

        if(add){
            Users_Signup posting = new Users_Signup(email,pwd,Pname);
            PostValues = posting.toMap();
        }

        childUpdates.put("/User_list/"+email,PostValues);
        DBref.updateChildren(childUpdates);
    }
}
