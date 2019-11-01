package com.example.morrisgram.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;

import com.example.morrisgram.DTOclass.Firebase.FollowerDTO;
import com.example.morrisgram.DTOclass.Firebase.FollowingDTO;
import com.example.morrisgram.DTOclass.Firebase.ReplyDTO;
import com.example.morrisgram.DTOclass.Firebase.Users_Signup;
import com.example.morrisgram.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReplyDisplay extends AppCompatActivity {
    //데이터베이스의 주소를 지정
    private DatabaseReference mdataref = FirebaseDatabase.getInstance().getReference();

    //팔로워 유저 UID 수집용 리스트
    private List<String> ReplyList = new ArrayList<>();
    private List<ReplyDTO> ReplyDTOS = new ArrayList<>();

    //현재 접속중인 유저UID가져오기
    private FirebaseUser uid = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseAuth firebaseAuth;
    private StorageReference mstorageRef = FirebaseStorage.getInstance().getReference();
    private String userUID = uid.getUid();

    //파이어베이스 리사이클러뷰
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;

    private EditText inputreply;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_display);

        recyclerView = findViewById(R.id.recyclerView_reply);

        //파이어베이스 리사이클러뷰
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);

        inputreply = (EditText) findViewById(R.id.inputreply_reply);


    }//-------------------크리에이트-----------------------------


    //댓글 업데이트 메소드
    public void FirebaseDatabase(boolean submit,String posterkey, String InputReply){
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously

        Map<String,Object> Reply = new HashMap<>();
        Map<String,Object> PostValues = null;

        ReplyDTO followerDTO = new ReplyDTO(InputReply,userUID);
        PostValues = followerDTO.toMap();

//        Map<String,Object> childUpdates = new HashMap<>();
//        Map<String,Object> PostValues = null;
//
//        if(add){
//            Users_Signup posting = new Users_Signup(email,Pname,pwd,phone,sex);
//            PostValues = posting.toMap();
//        }

        //댓글 버튼 - 댓글 달기 버튼 누른 아이템의 게시물 키값
        if (submit) {
            // 경로 : 루트/ Reply/ PosterKey/ UserUID/ ReplyKey/  ReplyBody : "v", UID : "UserUID"
            //replykey 생성
            String ReplyKey = mdataref.push().getKey();
            Reply.put(ReplyKey, PostValues);
            mdataref.child("Reply").child(posterkey).child(userUID).updateChildren(Reply);
        }
    }
}
