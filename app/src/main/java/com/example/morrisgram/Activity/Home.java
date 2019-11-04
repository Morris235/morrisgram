package com.example.morrisgram.Activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.morrisgram.Activity.BaseActivity.AddingPoster_BaseAct;
import com.example.morrisgram.CameraClass.GlideApp;
import com.example.morrisgram.DTOclass.FollowingDTO;
import com.example.morrisgram.DTOclass.PostingDTO;
import com.example.morrisgram.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.like.LikeButton;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.util.ArrayList;
import java.util.List;

public class Home extends AddingPoster_BaseAct implements SwipyRefreshLayout.OnRefreshListener {
    private SwipyRefreshLayout mSwipeRefreshLayout;
    private ImageButton myinfoB;
    private ImageButton searchB;
    private ImageButton addposterB;
    private ImageButton likealarmB;
    private ImageButton messageB;
    private ImageButton takephotoB;

    private FirebaseAuth firebaseAuth;
    //현재 로그인 된 유저 정보를 담을 변수
    private FirebaseUser currentUser;

    //카메라 퍼미션
    private final int MY_PERMISSIONS_REQUEST_CAMERA=1;
    //갤러리 퍼미션
    String[] permission_list = {
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_CONTACTS
    };
    //데이터베이스의 주소 지정
    private DatabaseReference mdataref = FirebaseDatabase.getInstance().getReference();

    //팔로잉 유저 UID 수집용 리스트
    private List<String> MyFollowingUIDList = new ArrayList<>();
    private List<FollowingDTO> followingDTOS = new ArrayList<>();

    //파이어베이스 리사이클러뷰
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;

    //전체 포스터키 수집용 리스트
    public List<String> PosterKeyList = new ArrayList<>();
    public List<PostingDTO> postingDTOS = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 화면을 portrait(세로) 화면으로 고정하고 싶은 경우
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_home);

        firebaseAuth = FirebaseAuth.getInstance();

        Log.i("파베","홈 크리에이트");

        recyclerView = findViewById(R.id.recyclerView_home);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        //smooth scrolling
        recyclerView.setNestedScrollingEnabled(false);

        //자신의 팔로잉 리스트 수집 = 내 UID를 자신의 팔로워 리스트에 갖고 있는 유저UID
        mdataref.child("UserList").child(userUID).child("FollowingList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                followingDTOS.clear();
                MyFollowingUIDList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    FollowingDTO followingDTO = snapshot.getValue(FollowingDTO.class);
                    String GetKey = snapshot.getKey();

                    //클래스 주소값?
                    followingDTOS.add(followingDTO);
                    MyFollowingUIDList.add(GetKey);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //전체 게시물 키 수집
        mdataref.child("PosterList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //포스터키 수집용 리스트
//                private List<String> PosterKeyList = new ArrayList<>();
//                private List<PostingDTO> postingDTOS = new ArrayList<>();

                //포스터키가 리스트에 쌓이지 않도록 클리어하기
                postingDTOS.clear();
                PosterKeyList.clear();
                //유저리스트에 있는 모든 데이터를 읽어온다. 그중에서 파베예외 발생 : Failed to convert a value of type java.util.HashMap to long
                //C#의 foreach문과 유사한 배열에 이용되는 for문 ->for(변수:배열) = 배열에 있는 값들을 하나씩 순서대로 변수에 대입시킨다. -배열의 자료형과 for문의 변수 자료형은 같아야 한다.
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    //파베 스냅샷으로 받아올때 long이나 int형태로는 못받아 오겠다. 왜냐하면 모델클래스가 해쉬맵이여서? =데이터를 모델에 맞게 받는 코드
                    PostingDTO postingDTO = snapshot.getValue(PostingDTO.class);
                    //게시물 키값 받기
                    String GetKey = snapshot.getKey();
                    Log.i("포스터키","전체 유저 게시물 키 : "+GetKey);

                    //클래스 주소값? 리스트
                    postingDTOS.add(postingDTO);
                    //키값들을 리스트형태로 저장
                    PosterKeyList.add(GetKey);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//------------------------------------------------게시물 키값 수집 데이터 스냅샷--------------------------------------

        fetch();
//-----------------------------------화면이동----------------------------------------
        //탐색화면 이동
        searchB = (ImageButton) findViewById(R.id.searchB_home);
        searchB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Search.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
        //내 프로필 화면 이동
        myinfoB = (ImageButton) findViewById(R.id.myB_home);
        myinfoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Myinfo.class);
                startActivity(intent);
//                finish();
                overridePendingTransition(0, 0);
            }
        });
        //좋아요 알람 화면 이동
        likealarmB = (ImageButton) findViewById(R.id.likeB_home);
        likealarmB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, LikeAlarm.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
        //채팅 화면 이동
        messageB = (ImageButton) findViewById(R.id.messageB);
        messageB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, MessageTerminal.class);
                startActivity(intent);
            }
        });

        //사진찍기 버튼
        takephotoB = (ImageButton) findViewById(R.id.takephotoB_home);
        takephotoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        //포스팅 화면 이동
        ImageButton addposterB = (ImageButton) findViewById(R.id.addB_home);
        addposterB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAlbum();
            }
        });
//---------------------------------------------------------------------------------

        mSwipeRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.refresh_home);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setDirection(SwipyRefreshLayoutDirection.TOP);

    }//----------------------크리에이트-------------------------------

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        Log.d("MainActivity", "Refresh triggered at "
                + (direction == SwipyRefreshLayoutDirection.TOP ? "top" : "bottom"));
        mSwipeRefreshLayout.setRefreshing(false);
        fetch();
        adapter.startListening();
    }
    // 권한 요청
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("사진", "onRequestPermissionsResult");
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Log.d("사진", "Permission: " + permissions[0] + "was " + grantResults[0]);
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.i("파베","홈 스타트");
        adapter.startListening();
    }
    public void onResume(){
        super.onResume();
        Log.i("파베","홈 리즈메");
        // 6.0 마쉬멜로우 이상일 경우에는 권한 체크 후 권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d("", "권한 설정 완료");
            } else {
                Log.d("", "권한 설정 요청");
                ActivityCompat.requestPermissions(Home.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        int permssionCheck = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA);

        if (permssionCheck!= PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this,"권한 승인이 필요합니다",Toast.LENGTH_LONG).show();

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                Toast.makeText(this,"사용을 위해 카메라 권한이 필요합니다.",Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
                Toast.makeText(this,"사용을 위해 카메라 권한이 필요합니다.",Toast.LENGTH_LONG).show();
            }
        }
    }
    public void onPause(){
        super.onPause();
        Log.i("파베","홈 포즈");
//        overridePendingTransition(0,0);
    }
    public void onStop(){
        super.onStop();
        Log.i("파베","홈 스탑");
        adapter.stopListening();
    }
    public void onDestroy(){
        super.onDestroy();
        Log.i("파베","홈 디스트로이");
    }
    public void onRestart(){
        super.onRestart();
        Log.i("파베","홈 리스타트");
    }
    //------------------------뷰홀더------------------------------
    public class ViewHolder extends RecyclerView.ViewHolder {

        public ConstraintLayout root;
        public TextView UserNicName;
        public TextView Body;
        public TextView PostedTime;
        public TextView NickName_Reply;
        public TextView LocationData;
        public ImageView profileIMG;
        public ImageView PosterKey;
        public ImageView vetB;

        //댓글 달기 버튼
        public ImageButton replyB;
        public ViewGroup replyviewB;

        //좋아요,댓글
        public TextView LikeCount;
        public TextView ReplyCount;

        public LikeButton likeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            root = itemView.findViewById(R.id.poster_posterviewer);
            UserNicName = itemView.findViewById(R.id.nicknameTV_posteritem);
            Body = itemView.findViewById(R.id.bodyTV);
            PostedTime = itemView.findViewById(R.id.timeTV);
            LikeCount = itemView.findViewById(R.id.like_counter);
            ReplyCount = itemView.findViewById(R.id.reply_counter);
            PosterKey = itemView.findViewById(R.id.imageView_posteritem);
            profileIMG = itemView.findViewById(R.id.profileIMG_posteritem);
            NickName_Reply = itemView.findViewById(R.id.nicknameTV_posteritem_body);
            LocationData = itemView.findViewById(R.id.location_posterviewer);
            vetB = itemView.findViewById(R.id.optionB_idtv_home);

            replyB = itemView.findViewById(R.id.reply_posteritem);
            replyviewB = itemView.findViewById(R.id.reply_layout);
        }

        //사진 위치 데이터
        public void setMetadata(String uri){
            //파베 메타 데이터 다운로드
            StorageReference imageRef = mstorageRef.child("PosterPicList/"+uri+"/PosterIMG");
            imageRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                @Override
                public void onSuccess(StorageMetadata storageMetadata) {
                    final String Loction = storageMetadata.getCustomMetadata("location");
                    Log.i("파베", "포스터 뷰어 위치데이터 확인 : "+Loction);

                    LocationData.setText(Loction);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("파베", "포스터 뷰어 위치데이터 겟 실패");
                }
            });
        }

        //유저 닉네임
        public void setUserNickName(String useruid) {
            mdataref.child("UserList").child(useruid).child("UserInfo").child("NickName").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        String name = dataSnapshot.getValue().toString();
                        UserNicName.setText(name);
                    }catch (NullPointerException e){
                        e.getStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        //유저 UID - 프로필 사진
        public void setUserUID(String uri) {
            //스토리지에서 프로필 이미지 받아오기
            StorageReference imageRef = mstorageRef.child(uri+"/ProfileIMG/ProfileIMG");
            GlideApp.with(Home.this)
                    .load(imageRef)
                    .skipMemoryCache(true)
                    .thumbnail()
                    .circleCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .dontAnimate()
                    .placeholder(R.drawable.ic_insert_photo_black_24dp)
                    .into(profileIMG);
        }

        //게시물 내용
        public void setBody(String string) {
            Body.setText(string);
        }

        //포스팅한 시간
        public void setPostedTime(String string) {
            PostedTime.setText(string);
        }

        //좋아요 개수
        public void setLikeCount(String string) {
            LikeCount.setText(string);
        }

        //댓글 개수
        public void setReplyCount(String string) {
            ReplyCount.setText(string);
        }

        //스토리지에서 이미지 받아오기 - 어떻게 해야 모든 이미지를 가져올 수 있지?
        public void setPosterKey(String uri){
            StorageReference imageRef = mstorageRef.child("PosterPicList/"+uri+"/PosterIMG");
            GlideApp.with(Home.this)
                    .load(imageRef)
                    .skipMemoryCache(false)
                    .thumbnail()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate()
                    .placeholder(R.drawable.ic_insert_photo_black_24dp)
                    .into(PosterKey);
        }

        //게시물 내용 - 유저 닉네임
        public void setNickName_Reply(String useruid){
            mdataref.child("UserList").child(useruid).child("UserInfo").child("NickName").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        String name = dataSnapshot.getValue().toString();
                        NickName_Reply.setText(name);
                    }catch (NullPointerException e){
                        e.getStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
    //----------------------------파이어베이스 어댑터---------------------------------------
    private void fetch() {
        //BaseQuery
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("PosterList");

        Log.i("쿼리","메소드 리턴 팔로잉 유저 : "+FollowingQuery(MyFollowingUIDList));
        //orderByChild()	지정된 하위 키의 값에 따라 결과를 정렬합니다.
        //orderByKey()	    하위 키에 따라 결과를 정렬합니다.
        //orderByValue()	하위 값에 따라 결과를 정렬합니다.

        //query를 사용해서 DB의 모든 해당 정보를 받아서 가져오는 스냅샷 - 스트링형식으로 받아와야함
        FirebaseRecyclerOptions<PostingDTO> options =
                new FirebaseRecyclerOptions.Builder<PostingDTO>()
                        .setQuery(query, new SnapshotParser<PostingDTO>() {
                            @NonNull
                            @Override
                            public PostingDTO parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new PostingDTO(
                                        snapshot.child("UserUID").getValue().toString(),     //프로필 이미지
                                        snapshot.child("Body").getValue().toString(),        //게시물 글
                                        snapshot.child("PostedTime").getValue().toString(),  //게시물 만든 시간
//                                        (HashMap<String, Long>) snapshot.child("like").getChildren().iterator().next().child("Count").getValue(),   //좋아요 개수
                                        snapshot.child("PosterKey").getValue().toString(),
                                        null);  //게시물 이미지
                            }
                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<PostingDTO, Home.ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ViewHolder holder, final int position, @NonNull PostingDTO posting_set) {
                holder.setPosterKey(posting_set.getPosterkey());
                holder.setBody(posting_set.getBody());
                holder.setUserNickName(posting_set.getUserUID());
                holder.setUserUID(posting_set.getUserUID());

//                holder.setLikeCount(String.valueOf(posting_set.getLikeCount()));

                holder.setPostedTime(posting_set.getPostedtime());
                holder.setNickName_Reply(posting_set.getUserUID());
                //위치 메타데이터
                holder.setMetadata(posting_set.getPosterkey());




                //------------유저 피드로 이동하기 클릭---------------
                holder.profileIMG.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                            //전체 게시물DB에서 유저 UID가져오기
                            mdataref.child("PosterList").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    //전체 게시물 노드에서의 유저UID - 클릭한 전체 게시물ID
                                    String PosterUserUID = dataSnapshot.child(PosterKeyList.get(position)).child("UserUID").getValue().toString();
                                    //유저 게시물 노드에서의 유저UID - 클릭한 내(로그인한) 게시물ID
//                              String LoginedUID = dataSnapshot.child(MyPosterKeyList.get(position)).child("UserUID").getValue().toString();


                                    //** 가져온 유저UID가 자신의 UID와 같으면 자신의 프로필로 이동 ->Myinfo.class ->게시물 위치 다름 userUID 다시받기 **
                                    if (PosterUserUID.equals(userUID)){
                                        Intent intent2 = new Intent(Home.this,Myinfo.class);
                                        startActivity(intent2);
                                    }else{
                                        //유저 피드로 유저 UID보내고 이동하기 -> UserProfile.class
                                        Intent intent1 = new Intent(Home.this,UserProfile.class);
                                        intent1.putExtra("PosterUserUID",PosterUserUID);
                                        startActivity(intent1);
                                    }


                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });




                    }
                });//-------------------------------------------------




                //게시물 삭제
                holder.vetB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence info[] = new CharSequence[] {"삭제","수정" };
                        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
//                        builder.setTitle()
                        builder.setItems(info, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which == 0){
//                                    mdataref.child(userUID).child("UserPosterList").child();
                                    Toast.makeText(Home.this, "옵션 클릭확인", Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    }
                });


                //댓글달기 화면으로 이동 - 인풋텍스트뷰에 포커스
                holder.replyB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent1 = new Intent(Home.this,ReplyDisplay.class);
                        intent1.putExtra("PosterKey",PosterKeyList.get(position));
                        startActivity(intent1);
                    }
                });


                //전체 댓글보기 - 댓글에 포커스
                holder.replyviewB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent2 = new Intent(Home.this,ReplyDisplay.class);
                        intent2.putExtra("PosterKey",PosterKeyList.get(position));
                        startActivity(intent2);
                    }
                });


                //댓글 개수 스냅샷
                mdataref.child("Reply").child(PosterKeyList.get(position)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String replycount = String.valueOf(dataSnapshot.getChildrenCount());
                        holder.setReplyCount(replycount);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(Home.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
                        //해당 포지션으로 포커스 주기 - 포스터뷰어로 이동
                    }
                });






            }
            @Override
            public Home.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.poster_item, parent, false);
                return new Home.ViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
    }

    //나의 팔로잉 리스트의 유저UID를 얻어서 쿼리 필터링하는 메소드
    public Query FollowingQuery(List<String> MyFollowingUIDList){
        Query query = null;

        for (int p = 0; p < MyFollowingUIDList.size(); p++){

            query = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("PosterList")
                    .orderByChild("UserUID")
                    .equalTo(MyFollowingUIDList.get(p));
        }

        return query;
    }
}