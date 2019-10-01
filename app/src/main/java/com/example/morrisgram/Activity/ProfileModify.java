package com.example.morrisgram.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.util.Printer;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.morrisgram.DTO_Classes.Firebase.Users_Signup;
import com.example.morrisgram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileModify extends AppCompatActivity {

   //데이터베이스의 주소를 지정 필수
   private DatabaseReference mdataref = FirebaseDatabase.getInstance().getReference("UserList");
   //현재 접속중인 유저UID가져오기
   private FirebaseUser uid = FirebaseAuth.getInstance().getCurrentUser();

   private TextView email;
   private TextView phone;
   private TextView sex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 화면을 portrait(세로) 화면으로 고정하고 싶은 경우
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_profile_modify);

        email = (TextView) findViewById(R.id.emailTV_profile);
        phone = (TextView) findViewById(R.id.phonTV_profile);
        sex = (TextView) findViewById(R.id.sexTV_profile);

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
                String userUID = uid.getUid();
                //현재 로그인된 유저 정보와 일치하는 데이터를 가져오기.
                String EmailVal = (String) dataSnapshot.child(userUID).child("Profile").child("Email_ID").getValue();
                String PhoneVal = (String) dataSnapshot.child(userUID).child("Profile").child("Phone").getValue();
                String SexVal = (String) dataSnapshot.child(userUID).child("Profile").child("Sex").getValue();

               email.setText(EmailVal);
               phone.setText(PhoneVal);
               sex.setText(SexVal);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    @Override
    public void onPause(){
        super.onPause();
        overridePendingTransition(0,0);
    }
}
