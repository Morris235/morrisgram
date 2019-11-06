package com.example.morrisgram.Activity;

import android.content.Intent;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.morrisgram.Activity.BaseActivity.AddingPoster_BaseAct;
import com.example.morrisgram.CameraClass.GlideApp;
import com.example.morrisgram.DTOclass.AlarmDTO.AlarmDTO;
import com.example.morrisgram.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
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

//알람DB내용을 수신하는 클래스
public class Alarm extends AddingPoster_BaseAct implements SwipeRefreshLayout.OnRefreshListener{
    private SwipeRefreshLayout mSwipeRefreshLayout;

    //데이터베이스의 주소를 지정
    private DatabaseReference mdataref = FirebaseDatabase.getInstance().getReference();

    //현재 접속중인 유저UID가져오기
    private FirebaseUser uid = FirebaseAuth.getInstance().getCurrentUser();
    private String userUID = uid.getUid();

    //스토리지
    private StorageReference mstorageRef = FirebaseStorage.getInstance().getReference();

    //파이어베이스 리사이클러뷰
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 화면을 portrait(세로) 화면으로 고정하고 싶은 경우
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_alarm);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_alarm);
        mSwipeRefreshLayout.setOnRefreshListener(this);
//-----------------------------------화면이동----------------------------------------
//홈 화면 이동
        ImageButton homeB;
        homeB=(ImageButton)findViewById(R.id.homeB_like);
        homeB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Alarm.this, Home.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
//탐색 화면 이동
        ImageButton searchB;
        searchB=(ImageButton)findViewById(R.id.searchB_like);
        searchB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Alarm.this, Search.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
//내 프로필 화면 이동
        ImageButton myinfoB;
        myinfoB=(ImageButton)findViewById(R.id.myB_like);
        myinfoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Alarm.this, Myinfo.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });

        //포스팅 화면 이동
        ImageButton addposterB = (ImageButton) findViewById(R.id.addB_like);
        addposterB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAlbum();
            }
        });
//---------------------------------------------------------------------------------

        //파이어베이스 리사이클러뷰
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView = findViewById(R.id.recyclerView_alarm);
        recyclerView.setLayoutManager(linearLayoutManager);

        fetch();
    }//---------------------크리에이트----------------------------


    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
        fetch();
        adapter.startListening();
    }

    //------------------------뷰홀더 클래스------------------------------
    public class ViewHolder extends RecyclerView.ViewHolder {
        //아이템 레이아웃 뷰 변수 선언
        public ConstraintLayout root;
        public TextView UserNickName;
        public ImageView AlarmUserIMG;
        public TextView AlarmBody;

//        public ImageButton delete;


        //알람을 보낸 유저의 UID받는 변수
        public String UID;


        public ViewHolder(@NonNull View itemView) {
            //선언한 변수와 아이템 레이아웃의 뷰를 바인드
            super(itemView);
            root = itemView.findViewById(R.id.alarmitem);
            UserNickName = itemView.findViewById(R.id.idtv_alarm);
            AlarmUserIMG = itemView.findViewById(R.id.profileIMG_alarm);
//            delete = itemView.findViewById(R.id.deleteB_reply);
            AlarmBody = itemView.findViewById(R.id.alarmbody_alarm);

        }


        //알람을 유저들의 닉네임 받기
        public void setUserNickName(final String path){

            mdataref.child("UserList").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        String nickname = dataSnapshot.child(path).child("UserInfo").child("NickName").getValue().toString();
                        Log.i("알람","유저 닉네임 확인 : "+nickname);
                        UserNickName.setText(nickname);
                    }catch (NullPointerException e){
                        e.getStackTrace();
                        Log.i("알람","유저 닉네임 널포인트");
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        //스토리지에서 알람을 보낸 유저의 프로필 이미지 받기
        public void setAlarmUserIMG(String uid){
            Log.i("알람","알람 유저 UID 확인 : "+uid);
            StorageReference imageRef = mstorageRef.child(uid).child("ProfileIMG").child("ProfileIMG");
            GlideApp.with(Alarm.this)
                    .load(imageRef)
                    .skipMemoryCache(false)
                    .thumbnail()
                    .centerCrop()
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate()
                    .placeholder(R.drawable.noimage)
                    .into(AlarmUserIMG);
        }

        //알람 내용
        public void setAlarmBody(String body){
            AlarmBody.setText(body);
            Log.i("댓글","바디 확인 : "+body);
        }
        public String getUserUID(String uid){
            UID = uid;
            return UID;
        }
    }//------------------------뷰홀더 클래스------------------------------

    private void fetch(){
        //BaseQuery - 알람 쿼리
        Query query = FirebaseDatabase.getInstance()
                //BaseQuery
                .getReference()
                .child("AlarmList")
                .child(userUID);

        Log.i("알람","쿼리 경로 : "+query.toString());

        //query를 사용해서 DB의 모든 해당 정보를 받아서 가져오는 스냅샷 - 스트링형식으로 받아와야함 - 팔로잉 유저의 UID만 받아와서 모든걸 해결하자!
        FirebaseRecyclerOptions<AlarmDTO> options =
                new FirebaseRecyclerOptions.Builder<AlarmDTO>()
                        .setQuery(query, new SnapshotParser<AlarmDTO>() {
                            @NonNull
                            @Override
                            public AlarmDTO parseSnapshot(@NonNull DataSnapshot snapshot) {
                                Log.i("댓글","댓글 내용 스냅샷 : "+snapshot.child("ReplyBody").getValue().toString());
                                Log.i("댓글","댓글 유저UID 스냅샷 : "+snapshot.child("ReplyUserUid").getValue().toString());
                                return new AlarmDTO(
                                        snapshot.child("AlarmUserUID").getValue().toString(),
                                        snapshot.child("AlarmBody").getValue().toString(),
                                        snapshot.child("AlarmPosterUID").getValue().toString());  //게시물 이미지
                            }
                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<AlarmDTO,ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull AlarmDTO alarmDTO_set) {
                //알람 유저 닉네임
                holder.setUserNickName(alarmDTO_set.getAlarmUserUID());
                //알람 유저 프사
                holder.setAlarmUserIMG(alarmDTO_set.getAlarmUserUID());
                //알람 내용
                holder.setAlarmBody(alarmDTO_set.getAlarmBody());


                //알람 클릭
                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.alarm_item, parent, false);
                return new ViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onPause(){
        super.onPause();
        overridePendingTransition(0,0);
    }
}
