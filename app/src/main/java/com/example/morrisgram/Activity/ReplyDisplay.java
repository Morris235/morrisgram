package com.example.morrisgram.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.morrisgram.Activity.FollowFragment.FollowerPage;
import com.example.morrisgram.CameraClass.GlideApp;
import com.example.morrisgram.DTOclass.Firebase.FollowerDTO;
import com.example.morrisgram.DTOclass.Firebase.FollowingDTO;
import com.example.morrisgram.DTOclass.Firebase.ReplyDTO;
import com.example.morrisgram.DTOclass.Firebase.Users_Signup;
import com.example.morrisgram.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
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
    private List<String> ReplyKeyList = new ArrayList<>();
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

    private TextView postingB;
    private EditText inputreply;

    private ImageView MyProfileIMG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_reply_display);

        //내 프로필 이미지
        MyProfileIMG = (ImageView) findViewById(R.id.profileIMG_reply);

        //파이어베이스 리사이클러뷰
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView = findViewById(R.id.recyclerView_reply);
        recyclerView.setLayoutManager(linearLayoutManager);

        //댓글 입력
        inputreply = (EditText) findViewById(R.id.inputreply_reply);


//        //액티비티 클릭시 키보드 닫힘
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(inputreply.getWindowToken(), 0);

        //댓글 업데이트
        postingB = (TextView) findViewById(R.id.postB_replydisplay);

        Intent intent = getIntent();
        final String PosterKey = intent.getStringExtra("PosterKey");


        //댓글 UID 수집 데이터 스냅샷
        ReplyKeyListReSizing(PosterKey,0);
//        //------------------------------------------------댓글 UID 수집 데이터 스냅샷--------------------------------------------------
//        mdataref.child("Reply").child(PosterKey).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                //포스터키가 리스트에 쌓이지 않도록 클리어하기
//                ReplyDTOS.clear();
//                ReplyKeyList.clear();
//                //C#의 foreach문과 유사한 배열에 이용되는 for문 ->for(변수:배열) = 배열에 있는 값들을 하나씩 순서대로 변수에 대입시킨다. -배열의 자료형과 for문의 변수 자료형은 같아야 한다.
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    //파베 스냅샷으로 받아올때 long이나 int형태로는 못받아 오겠다. 왜냐하면 모델클래스가 해쉬맵이여서? = 데이터를 모델에 맞게 받는 코드
//                    ReplyDTO  replyDTO = snapshot.getValue(ReplyDTO.class);
//                    //팔로잉 유저 UID 받기
//                    String GetKey = snapshot.getKey();
//                    Log.i("댓글","댓글 키값 : "+GetKey);
//
//                    //클래스 주소값? 리스트
//                    ReplyDTOS.add(replyDTO);
//
//                    //키값들을 리스트형태로 저장
//                    ReplyKeyList.add(GetKey);
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//        //------------------------------------------------댓글 UID 수집 데이터 스냅샷--------------------------------------------------

        //댓글 업로드 클릭
        postingB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ReplyText = inputreply.getText().toString();
                if (ReplyText.equals("")){
                    Toast.makeText(getApplicationContext(),"내용을 입력해주세요",Toast.LENGTH_SHORT).show();
                }else {
                    Log.i("댓글","댓글 입력 클릭 확인");
                    ReplyUpDatabase(true,PosterKey,ReplyText);
                    inputreply.setText("");
                }

            }
        });

        //댓글 업로드 엔터키 클릭
        inputreply.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    String ReplyText = inputreply.getText().toString();
                    if (ReplyText.equals("")){
                        Toast.makeText(getApplicationContext(),"내용을 입력해주세요",Toast.LENGTH_SHORT).show();
                    }else {
                        Log.i("댓글","댓글 엔터키 입력 클릭 확인");
                        ReplyUpDatabase(true,PosterKey,ReplyText);
                        inputreply.setText("");
                    }
                }
                return true;
            }
        });

        fetch(PosterKey);
    }//-------------------크리에이트-----------------------------
    public void onStart() {
        super.onStart();
        //Glide를 통한 프로필 이미지 바인딩
        StorageReference imageRef = mstorageRef.child(userUID + "/ProfileIMG/ProfileIMG");
        GlideApp.with(ReplyDisplay.this)
                .load(imageRef)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .dontAnimate()
                .placeholder(R.drawable.noimage)
                .centerCrop()
                .into(MyProfileIMG);
        adapter.startListening();
    }

    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
    //------------------------뷰홀더 클래스------------------------------
    public class ViewHolder extends RecyclerView.ViewHolder {
        //아이템 레이아웃 뷰 변수 선언
        public ConstraintLayout root;
        public TextView UserNickName;
        public ImageView ReplyUserIMG;

        public ImageButton delete;
        public TextView ReplyBody;

        //댓글단 유저의 UID받는 변수
        public String UID;


        public ViewHolder(@NonNull View itemView) {
            //선언한 변수와 아이템 레이아웃의 뷰를 바인드
            super(itemView);
            root = itemView.findViewById(R.id.reply_root);
            UserNickName = itemView.findViewById(R.id.nicknameTV_reply);
            ReplyUserIMG = itemView.findViewById(R.id.profileIMG_replyitem);
            delete = itemView.findViewById(R.id.deleteB_reply);
            ReplyBody = itemView.findViewById(R.id.reply_reply);

        }


        //댓글단 유저들의 닉네임 받기
        public void setUserNickName(final String path){

            mdataref.child("UserList").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        String nickname = dataSnapshot.child(path).child("UserInfo").child("NickName").getValue().toString();
                        Log.i("댓글","유저 닉네임 확인 : "+nickname);
                        UserNickName.setText(nickname);
                    }catch (NullPointerException e){
                        e.getStackTrace();
                        Log.i("댓글","유저 닉네임 널포인트");
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        //스토리지에서 팔로잉 유저의 프로필 이미지 받기
        public void setReplyUserIMG(String uid){
            Log.i("댓글","댓글 유저 UID 확인 : "+uid);
            StorageReference imageRef = mstorageRef.child(uid).child("ProfileIMG").child("ProfileIMG");
            GlideApp.with(ReplyDisplay.this)
                    .load(imageRef)
                    .skipMemoryCache(false)
                    .thumbnail()
                    .centerCrop()
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate()
                    .placeholder(R.drawable.noimage)
                    .into(ReplyUserIMG);
        }

        //댓글 내용
        public void setReplyBody(String body){
            ReplyBody.setText(body);
            Log.i("댓글","바디 확인 : "+body);
        }
        public String getUserUID(String uid){
            UID = uid;
            return UID;
        }
    }//------------------------뷰홀더 클래스------------------------------

    //----------------------------파이어베이스 어댑터---------------------------------------
    private void fetch(final String PosterKey) {
        //BaseQuery - 댓글 쿼리
        Query query = FirebaseDatabase.getInstance()
                //BaseQuery
                .getReference()
                .child("Reply")
                .child(PosterKey);

        Log.i("댓글","쿼리 경로 : "+query.toString());
        Log.i("댓글","게시물 키값 : "+PosterKey);

        //orderByChild()	지정된 하위 키의 값에 따라 결과를 정렬합니다.
        //orderByKey()	    하위 키에 따라 결과를 정렬합니다.
        //orderByValue()	하위 값에 따라 결과를 정렬합니다.

        //query를 사용해서 DB의 모든 해당 정보를 받아서 가져오는 스냅샷 - 스트링형식으로 받아와야함 - 팔로잉 유저의 UID만 받아와서 모든걸 해결하자!
        FirebaseRecyclerOptions<ReplyDTO> options =
                new FirebaseRecyclerOptions.Builder<ReplyDTO>()
                        .setQuery(query, new SnapshotParser<ReplyDTO>() {
                            @NonNull
                            @Override
                            public ReplyDTO parseSnapshot(@NonNull DataSnapshot snapshot) {
                                Log.i("댓글","댓글 내용 스냅샷 : "+snapshot.child("ReplyBody").getValue().toString());
                                Log.i("댓글","댓글 유저UID 스냅샷 : "+snapshot.child("ReplyUserUid").getValue().toString());
                                return new ReplyDTO(
                                        snapshot.child("ReplyBody").getValue().toString(),
                                        snapshot.child("ReplyUserUid").getValue().toString());  //게시물 이미지
                            }
                        })
                        .build();


        //어댑터
        adapter = new FirebaseRecyclerAdapter<ReplyDTO, ReplyDisplay.ViewHolder>(options) {
            //리사이클러뷰 아이템 생성
            @Override
            public ReplyDisplay.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.reply_item, parent, false);
                return new ReplyDisplay.ViewHolder(view);
            }


            @Override                                                                                           //DB 데이터 틀 = DTO 클래스
            protected void onBindViewHolder(@NonNull final ReplyDisplay.ViewHolder holder, final int position, @NonNull final ReplyDTO replyDTO_set) {
                //팔로잉 유저닉네임
                holder.setUserNickName(replyDTO_set.getReplyUseruid());
                //팔로잉 유저프로필 이미지
                holder.setReplyUserIMG(replyDTO_set.getReplyUseruid());
                //댓글 내용
                holder.setReplyBody(replyDTO_set.getReplyBody());

                //자신의 댓글만 선택해서 지우기
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //IndexOutOfBoundsException
                        try {
                            //삭제할 때마다 댓글 키 리스트를 재설정
                            ReplyKeyListReSizing(PosterKey,position);
                            mdataref.child("Reply").child(PosterKey).child(ReplyKeyList.get(position)).removeValue();
                        }catch (IndexOutOfBoundsException e){
                            e.getStackTrace();
                            Log.i("댓글","에러발생 : "+e.getStackTrace());
                        }
                    }
                });


//                //자신의 계정이 쓴 댓글 지우기 버튼 가시화
//                if (ReplyDTOS.get(position).getReplyUseruid().equals(userUID)){
//                    holder.delete.setVisibility(View.VISIBLE);
//                }else {
//                    holder.delete.setVisibility(View.INVISIBLE);
//                }


            }
        };
        recyclerView.setAdapter(adapter);
    }    //----------------------------파이어베이스 어댑터---------------------------------------

    //댓글 키 리스트 리사이즈 메소드
    public void ReplyKeyListReSizing(String PosterKey, final int position){
        //------------------------------------------------댓글 UID 수집 데이터 스냅샷--------------------------------------------------
        mdataref.child("Reply").child(PosterKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //포스터키가 리스트에 쌓이지 않도록 클리어하기
                ReplyDTOS.clear();
                ReplyKeyList.clear();
                //C#의 foreach문과 유사한 배열에 이용되는 for문 ->for(변수:배열) = 배열에 있는 값들을 하나씩 순서대로 변수에 대입시킨다. -배열의 자료형과 for문의 변수 자료형은 같아야 한다.
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    //파베 스냅샷으로 받아올때 long이나 int형태로는 못받아 오겠다. 왜냐하면 모델클래스가 해쉬맵이여서? = 데이터를 모델에 맞게 받는 코드
                    ReplyDTO  replyDTO = snapshot.getValue(ReplyDTO.class);
                    //팔로잉 유저 UID 받기
                    String GetKey = snapshot.getKey();
                    Log.i("댓글","댓글 키값 : "+GetKey);

                    //클래스 주소값? 리스트
                    ReplyDTOS.add(replyDTO);
                    //키값들을 리스트형태로 저장
                    ReplyKeyList.add(GetKey);
                    Log.i("댓글","ReplyKeyListReSizing : "+ReplyKeyList.size());
                    Log.i("댓글","ReplyDTOS 사이즈 : "+ReplyDTOS.size());
                    Log.i("댓글","ReplyDTOS userUID : "+ReplyDTOS.get(position).getReplyUseruid());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //------------------------------------------------댓글 UID 수집 데이터 스냅샷--------------------------------------------------
    }

    //댓글 업데이트 메소드
    public void ReplyUpDatabase(boolean submit,String posterkey, String InputReply){
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously

        Map<String,Object> Reply = new HashMap<>();
        Map<String,Object> PostValues = null;

        ReplyDTO followerDTO = new ReplyDTO(InputReply,userUID);
        PostValues = followerDTO.toMap();


        //댓글 버튼 - 댓글 달기 버튼 누른 아이템의 게시물 키값
        if (submit) {
            // 경로 : 루트/ Reply/ PosterKey/ ReplyKey/  ReplyBody : "v", UID : "UserUID"
            //replykey 생성
           String ReplyKey = mdataref.push().getKey();
            Reply.put(ReplyKey, PostValues);
            Log.i("댓글","실행확인");
            mdataref.getDatabase().getReference().child("Reply").child(posterkey).updateChildren(Reply);
        }
    }
}
