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
import android.media.Image;
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
import com.example.morrisgram.DTO_Classes.Firebase.Posting_DTO;
import com.example.morrisgram.DTO_Classes.Firebase.PreView;
import com.example.morrisgram.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.like.LikeButton;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

public class Home extends AddingPoster_BaseAct implements SwipyRefreshLayout.OnRefreshListener {
    SwipyRefreshLayout mSwipeRefreshLayout;
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

    //파이어베이스 리사이클러뷰
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;

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

//        //아이템 역순 추가정렬 = true
//        linearLayoutManager.setReverseLayout(false);
//        linearLayoutManager.setStackFromEnd(false);
//        recyclerView.setLayoutManager(linearLayoutManager); //setLayoutManager 메소드를 사용해서 매니저를 리사이클러뷰에 설정

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

    }

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
        public TextView LikeCount;
        public TextView ReplyCount;
        public TextView NickName_Reply;
        public TextView LocationData;
        public ImageView profileIMG;
        public ImageView PosterKey;
        public ImageView vetB;

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
        public void setUserNickName(String string) {
            UserNicName.setText(string);
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
        public void setNickName_Reply(String string){
            NickName_Reply.setText(string);
        }
    }
    //----------------------------파이어베이스 어댑터---------------------------------------
    private void fetch() {
        //BaseQuery
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("PosterList");

                //orderByChild()	지정된 하위 키의 값에 따라 결과를 정렬합니다.
                //orderByKey()	    하위 키에 따라 결과를 정렬합니다.
                //orderByValue()	하위 값에 따라 결과를 정렬합니다.

        Log.i("파베", "홈 뷰어 query 경로 확인 : "+query.toString());

        //query를 사용해서 DB의 모든 해당 정보를 받아서 가져오는 스냅샷 - 스트링형식으로 받아와야함
        FirebaseRecyclerOptions<Posting_DTO> options =
                new FirebaseRecyclerOptions.Builder<Posting_DTO>()
                        .setQuery(query, new SnapshotParser<Posting_DTO>() {
                            @NonNull
                            @Override
                            public Posting_DTO parseSnapshot(@NonNull DataSnapshot snapshot) {
                                Log.i("파베", "홈 뷰어 스냅샷 메소드 작동 확인");
                                Log.i("파베", "snapshot.child(\"PosterKey\").getValue().toString() : "+snapshot.child("PosterKey").getValue().toString());
                                return new Posting_DTO(
                                        snapshot.child("UserUID").getValue().toString(),     //프로필 이미지
                                        snapshot.child("UserNickName").getValue().toString(), //유저 닉네임
                                        snapshot.child("Body").getValue().toString(),        //게시물 글
                                        snapshot.child("PostedTime").getValue().toString(),  //게시물 만든 시간
                                        snapshot.child("LikeCount").getValue().toString(),   //좋아요 개수
                                        snapshot.child("ReplyCount").getValue().toString(),  //댓글 개수
                                        snapshot.child("PosterKey").getValue().toString());  //게시물 이미지
                            }
                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<Posting_DTO, Home.ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, final int position, @NonNull Posting_DTO posting_dto) {
                holder.setPosterKey(posting_dto.getPosterKey());
                holder.setBody(posting_dto.getBody());
                holder.setUserNickName(posting_dto.getUserNickName());
                holder.setUserUID(posting_dto.getUserUID());
                holder.setLikeCount(posting_dto.getLikeCount());
                holder.setReplyCount(posting_dto.getReplyCount());
                holder.setPostedTime(posting_dto.getPostedTime());
                holder.setNickName_Reply(posting_dto.getUserNickName());
                //위치 메타데이터
                holder.setMetadata(posting_dto.getPosterKey());

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
}