package com.example.morrisgram.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.Printer;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.morrisgram.DTO_Classes.Firebase.Users_ProfileModify;
import com.example.morrisgram.DTO_Classes.Firebase.Users_Signup;
import com.example.morrisgram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ProfileModify extends AppCompatActivity {

   //데이터베이스의 주소를 지정 필수
   private DatabaseReference mdataref = FirebaseDatabase.getInstance().getReference("UserList");
   //현재 접속중인 유저UID가져오기
   private FirebaseUser uid = FirebaseAuth.getInstance().getCurrentUser();

   private ImageButton ProfileModifyB;

   private TextView email;
   private TextView phone;
   private TextView sex;

   private EditText edname;
   private EditText edwebsite;
   private EditText edintroduce;

   private String userUID = uid.getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 화면을 portrait(세로) 화면으로 고정하고 싶은 경우
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_profile_modify);

       //유저 UID 스트링


        //프로필 수정완료 버튼
        ProfileModifyB = (ImageButton) findViewById(R.id.compB);

        //개인정보 표시 변수
        email = (TextView) findViewById(R.id.emailTV_profile);
        phone = (TextView) findViewById(R.id.phonTV_profile);
        sex = (TextView) findViewById(R.id.sexTV_profile);

        //프로필 수정 인풋 텍스트
        edname = (EditText) findViewById(R.id.inputname);
        edwebsite = (EditText) findViewById(R.id.inputwebsite);
        edintroduce = (EditText) findViewById(R.id.inputintro);

        //취소버튼
        ImageButton cancelB;
        cancelB=(ImageButton)findViewById(R.id.cancelB);
        cancelB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //처음 앱을 실행하고 버튼을 눌렀을 때만 값을 읽어옴 addListenerForSingleValueEvent
        //수시로 해당 디비의 하위값들이 변화를 감지하고 그떄마다 값을 불러오려면 addValueEventListener를 사용
        mdataref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //현재 로그인된 유저 정보와 일치하는 데이터를 가져오기.
                String EmailVal = (String) dataSnapshot.child(userUID).child("UserInfo").child("Email_ID").getValue();
                String PhoneVal = (String) dataSnapshot.child(userUID).child("UserInfo").child("Phone").getValue();
                String SexVal = (String) dataSnapshot.child(userUID).child("UserInfo").child("Sex").getValue();
                String NameVal = (String) dataSnapshot.child(userUID).child("UserInfo").child("NickName").getValue();

                //유저프로필
                String WebsiteVal = (String) dataSnapshot.child(userUID).child("Profile").child("Website").getValue();
                String IntroVal = (String) dataSnapshot.child(userUID).child("Profile").child("Introduce").getValue();

               //개인정보 표시
               email.setText(EmailVal);
               phone.setText(PhoneVal);
               sex.setText(SexVal);

               //수정용 프로필 정보 표시 프로필 차일드와 중복
               edname.setText(NameVal);

               //프로필 읽고 인풋텍스트에 세팅
               edwebsite.setText(WebsiteVal);
               edintroduce.setText(IntroVal);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //프로필 정보 변경 완료
        ProfileModifyB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //프로필 정보
               String upname = edname.getText().toString();
               String upwebsite = edwebsite.getText().toString();
               String upintro = edintroduce.getText().toString();

                FirebaseDatabase(true,upwebsite,upintro,upname);
                finish();
            }
        });
    }
    //애니메이션 효과 지우기
    @Override
    public void onPause(){
        super.onPause();
        overridePendingTransition(0,0);
    }

    //파이어 베이스 업데이트 메소드 - 프로필 웹사이트,소개
    public void FirebaseDatabase(boolean add, String website, String intro, String upname){
        //해쉬맵 생성
        Map<String,Object> childUpdates = new HashMap<>();
        Map<String,Object> PostValues = null;

        if(add){
            Users_ProfileModify posting = new Users_ProfileModify(website,intro);
            PostValues = posting.toMap();
        }

        //새로운 차일드 목록 만들기
        childUpdates.put("Profile" ,PostValues);
        mdataref.child(userUID).updateChildren(childUpdates);

        //유저이름 업데이트 - UserInfo child
        mdataref.child(userUID).child("UserInfo").child("NickName").setValue(upname);
    }
}
