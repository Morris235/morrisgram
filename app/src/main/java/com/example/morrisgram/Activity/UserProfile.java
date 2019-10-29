package com.example.morrisgram.Activity;

import android.content.Intent;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.morrisgram.Activity.BaseActivity.AddingPoster_BaseAct;
import com.example.morrisgram.CameraClass.GlideApp;
import com.example.morrisgram.DTOclass.Firebase.FollowerDTO;
import com.example.morrisgram.DTOclass.Firebase.FollowingDTO;
import com.example.morrisgram.DTOclass.Firebase.PostingDTO;
import com.example.morrisgram.DTOclass.Firebase.PreView;
import com.example.morrisgram.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
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

public class UserProfile extends AddingPoster_BaseAct implements SwipeRefreshLayout.OnRefreshListener{
    private SwipeRefreshLayout mSwipeRefreshLayout;

    //Count
    private TextView posternumTV;
    private TextView followernumTV;
    private TextView forllowingnumTV;

    //데이터베이스의 주소를 지정 필수
    private DatabaseReference mdataref = FirebaseDatabase.getInstance().getReference();
    //스토리지 레퍼렌스
    private StorageReference mstorageRef = FirebaseStorage.getInstance().getReference();

    //유저의 포스터키 수집용 리스트
    private List<String> PosterKeyList = new ArrayList<>();
    private List<PostingDTO> postingDTOS = new ArrayList<>();

    //파이어베이스 리사이클러뷰
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private FirebaseRecyclerAdapter adapter;

    //유저 정보표시
    private TextView pname;
    private TextView idtv;
    private TextView intro;
    private TextView website;
    private ImageView profileimg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 화면을 portrait(세로) 화면으로 고정하고 싶은 경우
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_userprofile);


        pname = (TextView) findViewById(R.id.name_user);
        idtv = (TextView) findViewById(R.id.idtv_user);
        website = (TextView) findViewById(R.id.website_user);
        intro = (TextView) findViewById(R.id.introduce_user);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_user);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        //CountTV
        posternumTV = (TextView) findViewById(R.id.posterNum_user);
        followernumTV = (TextView) findViewById(R.id.FollowersNum_user);
        forllowingnumTV = (TextView) findViewById(R.id.followingsnum_user);

        //프로필 이미지 바인드
        profileimg = (ImageView) findViewById(R.id.profileIMG_user);
        //리사이클러뷰 바인드
        recyclerView = findViewById(R.id.recyclerView_user);

        //파이어베이스 리사이클러뷰
        gridLayoutManager = new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);

        //팔로우 & 팔로잉 버튼 바인드
        final Button followB = findViewById(R.id.followB_user);
        final Button followingB = findViewById(R.id.followingB_user);


        //인텐트로 게시물의 유저UID받기
        Intent intent = getIntent();
        final String PosterUserUID = intent.getStringExtra("PosterUserUID");
        Log.i("게시물 유저","게시물 유저 UID : "+PosterUserUID);

        //상대 유저의 정보 스냅샷
        mdataref.child("UserList").child(PosterUserUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //현재 로그인된 유저 정보와 일치하는 데이터를 가져오기.
                String NameVal = (String) dataSnapshot.child("UserInfo").child("NickName").getValue();
                String WebsiteVal = (String) dataSnapshot.child("Profile").child("Website").getValue();
                String IntroVal = (String) dataSnapshot.child("Profile").child("Introduce").getValue();
                pname.setText(NameVal);
                idtv.setText(NameVal);
                website.setText(WebsiteVal);
                intro.setText(IntroVal);

                //게시물,팔로워,팔로잉 카운트
                String posternum = String.valueOf( (int) dataSnapshot.child("UserPosterList").getChildrenCount());
                String followernum = String.valueOf((int)dataSnapshot.child("FollowerList").getChildrenCount());
                String followingnum = String.valueOf((int)dataSnapshot.child("FollowingList").getChildrenCount());
                posternumTV.setText(posternum);
                followernumTV.setText(followernum);
                forllowingnumTV.setText(followingnum);



                //팔로워 리스트에서 나의 계정UID가 있다면 팔로잉버튼 처리
                // 경로 : root/ UserList/ userUID/ FollowerList/ FollowerUserUID/  UID : "UID"
                try {
                    String FollowerUID = dataSnapshot.child("FollowerList").child(userUID).child("UID").getValue().toString();
                    if(userUID.equals(FollowerUID)){
                        //팔로잉
                        followB.setVisibility(View.INVISIBLE);
                        followingB.setVisibility(View.VISIBLE);
                    }else {
                        //팔로우
                        followB.setVisibility(View.VISIBLE);
                        followingB.setVisibility(View.INVISIBLE);
                    }
                }catch (NullPointerException e){
                    e.getStackTrace();
                }


                //포스터키 수집용 리스트
//                private List<String> PosterKeyList = new ArrayList<>();
//                private List<PostingDTO> postingDTOS = new ArrayList<>();
                //포스터키가 리스트에 쌓이지 않도록 클리어하기
                postingDTOS.clear();
                PosterKeyList.clear();
                //유저리스트에 있는 모든 데이터를 읽어온다. 그중에서 파베예외 발생 : Failed to convert a value of type java.util.HashMap to long
                //C#의 foreach문과 유사한 배열에 이용되는 for문 ->for(변수:배열) = 배열에 있는 값들을 하나씩 순서대로 변수에 대입시킨다. -배열의 자료형과 for문의 변수 자료형은 같아야 한다.
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    //클래스 타입 - 해쉬맵 문제? - PostingDTO라는 모델클래스의 틀에 맞춰서 파베의 데이터를 읽어오는데 그중 long타입을 해쉬맵으로 치환해서 읽어 올 수 없다?
                    PostingDTO postingDTO = snapshot.getValue(PostingDTO.class);
                    String GetKey = snapshot.getKey();

                    //클래스 주소값?
                    postingDTOS.add(postingDTO);
                    PosterKeyList.add(GetKey);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        //파이어베이스 리사이클러뷰 어댑터 클래스
        fetch(PosterUserUID);
//-----------------------------------화면이동----------------------------------------
//홈 화면 이동
        ImageButton homeB;
        homeB=(ImageButton)findViewById(R.id.homeB_user);
        homeB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfile.this, Home.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
//탐색 화면 이동
        ImageButton searchB;
        searchB=(ImageButton)findViewById(R.id.searchB_user);
        searchB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfile.this, Search.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
//좋아요 알람 화면 이동
        ImageButton likealarmB;
        likealarmB=(ImageButton)findViewById(R.id.likeB_user);
        likealarmB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfile.this, LikeAlarm.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
//내 프로필 화면 이동
        ImageButton myinfoB;
        myinfoB=(ImageButton)findViewById(R.id.myB_user);
        myinfoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfile.this, Myinfo.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
        //바로 해당유저에게 메세지를 보낼 수 있는 메세지창으로 이동
        final Button messageB;
        messageB=(Button)findViewById(R.id.messageB_user);
        messageB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfile.this,MessageWindow.class);
            }
        });
        //포스팅 화면 이동
        ImageButton addposterB = (ImageButton) findViewById(R.id.addB_user);
        addposterB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAlbum();
            }
        });
//-----------------------------------화면이동----------------------------------------






//-------------------------팔로우------------------------------회원가입 DTO참조
        //팔로우하기 버튼
        followB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //팔로우버튼 표시 분기문

                //나와 상대방의 팔로워,팔로잉 리스트 업데이트
                FirebaseDatabase(true,PosterUserUID);

                //버튼 표시설정
                followB.setVisibility(View.INVISIBLE);
                followingB.setVisibility(View.VISIBLE);
            }
        });
        //언팔로우하기 버튼
        followingB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //나와 상대방의 팔로워,팔로잉 리스트 업데이트
                FirebaseDatabase(false,PosterUserUID);

                //버튼 표시설정
                followB.setVisibility(View.VISIBLE);
                followingB.setVisibility(View.INVISIBLE);
            }
        });
//-------------------------팔로우------------------------------









    }//---------------------------크리에이트---------------------------

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
        //인텐트로 게시물의 유저UID받기
        Intent intent = getIntent();
        String PosterUserUID = intent.getStringExtra("PosterUserUID");
        fetch(PosterUserUID);

        adapter.startListening();
    }
    //--------------생명주기--------------
    public void onStart() {
        super.onStart();
        Log.i("파베", "마이 스타트");

        //인텐트로 게시물의 유저UID받기
       Intent intent = getIntent();
       String PosterUserUID = intent.getStringExtra("PosterUserUID");

        //Glide를 통한 프로필 이미지 바인딩
        StorageReference imageRef = mstorageRef.child(PosterUserUID + "/ProfileIMG/ProfileIMG");
        GlideApp.with(UserProfile.this)
                .load(imageRef)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .dontAnimate()
                .placeholder(R.drawable.noimage)
                .centerCrop()
                .into(profileimg);
        adapter.startListening();
    }


    //애니메이션 효과 지우기
    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
//        Log.i("파베", "마이 포즈");
    }
    public void onStop() {
        super.onStop();
        Log.i("파베", "마이 스탑");
        adapter.stopListening();
    }

    //------------------------뷰홀더------------------------------
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout root;
        public ImageView PosterKey;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            root = itemView.findViewById(R.id.preview_userfeed_root);
            PosterKey = itemView.findViewById(R.id.preview_userfeed_IMG);
        }
        //스토리지에서 게시물 미리보기 이미지 받아오기
        public void setPosterKey(String uri) {
            StorageReference imageRef = mstorageRef.child("PosterPicList").child(uri).child("PosterIMG");
            GlideApp.with(UserProfile.this)
                    .load(imageRef)
                    .skipMemoryCache(false)
                    .thumbnail()
                    .centerCrop()
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate()
                    .placeholder(R.drawable.ic_insert_photo_black_24dp)
                    .into(PosterKey);
        }
    }//------------------------뷰홀더------------------------------



    //----------------------------파이어베이스 어댑터 클래스---------------------------------------
    private void fetch(final String PosterUserUID) {
        try {
            Query query = FirebaseDatabase.getInstance()
                    //BaseQuery
                    .getReference()
                    .child("UserList")
                    .child(PosterUserUID)
                    .child("UserPosterList")
                    .orderByChild("TimeStemp");

            //DB에 정보를 받아서 가져오는 스냅샷 - 스트링형식으로 받아와야함
            FirebaseRecyclerOptions<PreView> options =
                    new FirebaseRecyclerOptions.Builder<PreView>()
                            .setQuery(query, new SnapshotParser<PreView>() {
                                @NonNull
                                @Override
                                public PreView parseSnapshot(@NonNull DataSnapshot snapshot) {
                                    //포스터키 수집
//                                    UserPosterKeys = snapshot.child("PosterKey").getValue().toString();
//                                    //내부 DB에 로그인한 유저의 포스터키 저장
//                                    SavePosterKey(UserPosterKeys);
                                    return new PreView(
                                            snapshot.child("PosterKey").getValue().toString());
                                }
                            })
                            .build();

            adapter = new FirebaseRecyclerAdapter<PreView, ViewHolder>(options) {
                @Override
                public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.preview_userfeed_item, parent, false);
                    return new ViewHolder(view);
                }

                @Override
                protected void onBindViewHolder(final ViewHolder holder, final int position, PreView preView) {
                    holder.setPosterKey(preView.getPosterKey());



                    //클릭한 이미지의 포스트뷰어로 이동하기
                    holder.root.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(UserProfile.this, String.valueOf(position), Toast.LENGTH_SHORT).show();

                            //유저 프로필 -> 포스터뷰어
                            final int FLAG = 1;
                            Intent intent = new Intent(UserProfile.this,PosterViewer.class);
                            intent.putExtra("FOCUS",position);
                            intent.putExtra("FLAG",FLAG);

                            //유저의 UID 보내기
                            intent.putExtra("PosterUserUID",PosterUserUID);

                            startActivity(intent);
                            overridePendingTransition(0, 0);
                        }
                    });//클릭한 이미지의 포스트뷰어로 이동하기




                }
            };
            recyclerView.setAdapter(adapter);

        }catch (NullPointerException e){
            e.getStackTrace();
            Log.i("try", "NullPointerException :"+e);
        }
    }//----------------------------파이어베이스 어댑터 클래스---------------------------------------
    public void FirebaseDatabase(boolean add, String PosterUserUID){
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously

        //팔로워 리스트 맵 - 업데이트
        Map<String,Object> FollowerUserUID = new HashMap<>();
        //팔로잉 리스트 맵 - 업데이트
        Map<String,Object> FollowingUserUID = new HashMap<>();

        Map<String,Object> FollowerValues = null;
        Map<String,Object> FollowingValues = null;

        //객체 2개 선언
            FollowingDTO followingDTO = new FollowingDTO(PosterUserUID);
            FollowerDTO followerDTO = new FollowerDTO(userUID);

            //팔로잉 팔로워 맵 두개 사용
        FollowerValues = followerDTO.toMap();
        FollowingValues = followingDTO.toMap();



        //팔로우 버튼
        if (add){
            //<---상대방 팔로워 리스트---> DB에 로그인한 유저의 UID 추가
            // 경로 : 루트/ UserList/ userUID/ FollowerList/ FollowerUserUID/  UID : "UID"
            FollowerUserUID.put(userUID,FollowerValues);
            mdataref.child("UserList").child(PosterUserUID).child("FollowerList").updateChildren(FollowerUserUID);

            //<---내 팔로잉 리스트---> DB에 상대방 유저의 UID 추가
            // 경로 : 루트/ UserList/ userUID/ FollowingList/ FollowingUserUID/  UID : "UID"
            FollowingUserUID.put(PosterUserUID,FollowingValues);
            mdataref.child("UserList").child(userUID).child("FollowingList").updateChildren(FollowingUserUID);

        //언팔로우 버튼
        }else{
            //<---상대방 팔로워 리스트---> DB에 로그인한 유저의 UID 삭제
            mdataref.child("UserList").child(PosterUserUID).child("FollowerList").child(userUID).child("UID").setValue(null);

            //<---내 팔로잉 리스트---> DB에 상대방 유저의 UID 삭제
            mdataref.child("UserList").child(userUID).child("FollowingList").child(PosterUserUID).child("UID").setValue(null);
        }


    }
}
