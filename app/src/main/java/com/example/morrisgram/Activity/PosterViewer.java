package com.example.morrisgram.Activity;

import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.ActivityInfo;
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
import com.example.morrisgram.DTOclass.PosterDTO.PostingDTO;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.util.ArrayList;
import java.util.List;

//게시물 액티비티
public class PosterViewer extends AddingPoster_BaseAct implements SwipyRefreshLayout.OnRefreshListener{

    SwipyRefreshLayout mSwipeRefreshLayout;
    //데이터베이스의 주소를 지정 필수
    private DatabaseReference mdataref = FirebaseDatabase.getInstance().getReference();
    //현재 접속중인 유저UID가져오기
    private FirebaseUser uid = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseAuth firebaseAuth;
    private StorageReference mstorageRef = FirebaseStorage.getInstance().getReference();
    private String userUID = uid.getUid();

    //파이어베이스 리사이클러뷰
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter<PostingDTO, ViewHolder> adapter;

    //전체 포스터키 수집용 리스트
    private List<String> PosterKeyList = new ArrayList<>();
    private List<PostingDTO> postingDTOS = new ArrayList<>();

    //내 포스터키 수집용 리스트
    private List<String> UserPosterKeyList = new ArrayList<>();
    private List<PostingDTO> UserpostingDTOS = new ArrayList<>();


    //포커스
    public boolean FIRST_FOCUS = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 화면을 portrait(세로) 화면으로 고정하고 싶은 경우
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_poster_viewer);

        recyclerView = findViewById(R.id.recyclerView_posterviewer);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(false);
        linearLayoutManager.setReverseLayout(false);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        mSwipeRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.refresh_posterviewer);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setDirection(SwipyRefreshLayoutDirection.TOP);


        //포스트뷰어 게시물 표시 신호 플래그 인텐트
        final Intent intent = getIntent();
        final int FLAG = intent.getIntExtra("FLAG",-1);

        //게시물에서 유저UID를 받은 경우 유저UID 인텐트
        final String PosterUserUID = intent.getStringExtra("PosterUserUID");

//------------------------------------------------게시물 키값 수집 데이터 스냅샷--------------------------------------------------
        //내 게시물 키 수집 - 나와 타 유저가 포스터뷰어에 접근 했을 때 사용할 포스터키
        try {
            mdataref.child("UserList").child(PosterUserUID).child("UserPosterList").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //포스터키 수집용 리스트
//                private List<String> PosterKeyList = new ArrayList<>();
//                private List<PostingDTO> postingDTOS = new ArrayList<>();

                    //포스터키가 리스트에 쌓이지 않도록 클리어하기
                    UserpostingDTOS.clear();
                    UserPosterKeyList.clear();
                    //유저리스트에 있는 모든 데이터를 읽어온다. 그중에서 파베예외 발생 : Failed to convert a value of type java.util.HashMap to long
                    //C#의 foreach문과 유사한 배열에 이용되는 for문 ->for(변수:배열) = 배열에 있는 값들을 하나씩 순서대로 변수에 대입시킨다. -배열의 자료형과 for문의 변수 자료형은 같아야 한다.
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        //파베 스냅샷으로 받아올때 long이나 int형태로는 못받아 오겠다. 왜냐하면 모델클래스가 해쉬맵이여서? =데이터를 모델에 맞게 받는 코드
                        PostingDTO postingDTO = snapshot.getValue(PostingDTO.class);
                        //게시물 키값 받기
                        String GetKey = snapshot.getKey();
                        Log.i("포스터키","로그인한 유저 게시물 키 : "+GetKey);

                        //클래스 주소값? 리스트
                        UserpostingDTOS.add(postingDTO);
                        //키값들을 리스트형태로 저장
                        UserPosterKeyList.add(GetKey);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch (RuntimeException e){
            e.getStackTrace();
        }




        //전체 게시물 키 수집 - 서치Act로부터 포스터뷰어에 접근했을 떄 사용할 게시물UID
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
//------------------------------------------------게시물 키값 수집 데이터 스냅샷--------------------------------------------------



        //포커스 boolean
        FIRST_FOCUS = true;
        //리사이클러뷰 전개
        fetch(FLAG,PosterUserUID);



//----------------------------------화면이동----------------------------------------
//홈 화면 이동
        ImageButton homeB;
        homeB=(ImageButton)findViewById(R.id.homeB_poster);
        homeB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PosterViewer.this, Home.class);
                startActivity(intent);
            }
        });
//탐색 화면 이동
        ImageButton searchB;
        searchB=(ImageButton)findViewById(R.id.searchB_poster);
        searchB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PosterViewer.this, Search.class);
                startActivity(intent);
            }
        });
//좋아요 알람 화면 이동
        ImageButton likealarmB;
        likealarmB=(ImageButton)findViewById(R.id.likeB_poster);
        likealarmB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PosterViewer.this, Alarm.class);
                startActivity(intent);
            }
        });
//내 프로필 화면 이동
        ImageButton myinfoB;
        myinfoB=(ImageButton)findViewById(R.id.myB_poster);
        myinfoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PosterViewer.this, Myinfo.class);
                startActivity(intent);
            }
        });
        //포스팅 화면 이동
        ImageButton addposterB = (ImageButton) findViewById(R.id.addB_poster);
        addposterB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAlbum();
            }
        });

    }//-------------------------------크리에이트-----------------------------------




    //--------------------------생명주기-------------------------------
    public void onStart() {
        super.onStart();
        Log.i("파베", "마이 스타트");
        adapter.startListening();
    }

    public void onStop() {
        super.onStop();
        Log.i("파베", "마이 스탑");
        adapter.stopListening();
    }
    public void onResume(){
        super.onResume();

    }
    //애니메이션 효과 지우기
    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
//        Log.i("파베", "마이 포즈");
    }
    //새로고침
    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        mSwipeRefreshLayout.setRefreshing(false);

        //포스트뷰어 게시물 표시 신호 플래그 인텐트
        final Intent intent = getIntent();
        final int FLAG = intent.getIntExtra("FLAG",-1);

        //게시물에서 유저UID를 받은 경우 유저UID 인텐트
        final String PosterUserUID = intent.getStringExtra("PosterUserUID");

        fetch(FLAG,PosterUserUID);
        adapter.startListening();
    }
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }
    //-----------------------------------------------------------------







    //------------------------뷰홀더 클래스------------------------------
   public class ViewHolder extends RecyclerView.ViewHolder {

        public ConstraintLayout root;

        public TextView UserNicName;
        public TextView Body;
        public TextView PostedTime;
        public TextView LikeCount;
//        public TextView ReplyCount;
        public TextView NickName_Reply;
        public TextView LocationData;
        public ImageView profileIMG;
        public ImageView PosterKey;
        public ImageView vetB;

        public TextView ReplyCount;

        //유저 게시물키 받기용
        public String UserPosterKey;
        public ImageButton likeButton;

        public ImageButton replyB;
        public ViewGroup replyviewB;

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
            likeButton = itemView.findViewById(R.id.likeB_posteritem);
            replyB = itemView.findViewById(R.id.reply_posteritem);
            replyviewB = itemView.findViewById(R.id.reply_layout);
        }

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

        public void setUserUID(String uri) {
            //스토리지에서 프로필 이미지 받아오기
            StorageReference imageRef = mstorageRef.child(uri+"/ProfileIMG/ProfileIMG");
            GlideApp.with(PosterViewer.this)
                    .load(imageRef)
                    .skipMemoryCache(false)
                    .thumbnail()
                    .circleCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate()
                    .placeholder(R.drawable.ic_insert_photo_black_24dp)
                    .into(profileIMG);
        }

        public void setBody(String string) {
            Body.setText(string);
        }

        public void setPostedTime(String string) {
            PostedTime.setText(string);
        }

        public void setLikeCount(String string) {
            LikeCount.setText(string);
        }

        public void setReplyCount(String string) {
            ReplyCount.setText(string);
        }

        public void setPosterKey(String uri){
            //스토리지에서 이미지 받아오기 mstorageRef.child("PosterPicList").child(userUID).child(uri).child("PosterIMG");
            StorageReference imageRef = mstorageRef.child("PosterPicList").child(uri).child("PosterIMG");
            GlideApp.with(PosterViewer.this)
                    .load(imageRef)
                    .skipMemoryCache(false)
                    .thumbnail()
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate()
                    .placeholder(R.drawable.ic_insert_photo_black_24dp)
                    .into(PosterKey);
        }

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

        //유저 게시물 키 받기
        public String getUserPosterKey(String Key){
//            UserPosterKey = Key;
            return Key;
        }
    }//------------------------뷰홀더 클래스------------------------------




    //----------------------------파이어베이스 어댑터 메소드---------------------------------------
    private void fetch(final int FLAG, final String PosterUserUID) {


        //DB에 정보를 받아서 가져오는 스냅샷 - 스트링형식으로 받아와야함
        FirebaseRecyclerOptions<PostingDTO> options =
                new FirebaseRecyclerOptions.Builder<PostingDTO>()
                        .setQuery(       QuerySwitch(FLAG,PosterUserUID),    new SnapshotParser<PostingDTO>() {
                            @NonNull
                            @Override
                            public PostingDTO parseSnapshot(@NonNull DataSnapshot snapshot) {

                                //받은 게시물 키값으로 검색,대조,데이터 수정
//                                String UserPosterKey =  snapshot.child("PosterKey").getValue().toString();
//                                Log.i("포스터키", "UserPosterKey : "+UserPosterKey);

                                return new PostingDTO(
                                        snapshot.child("UserUID").getValue().toString(),     //프로필 이미지
                                        snapshot.child("Body").getValue().toString(),        //게시물 글
                                        snapshot.child("PostedTime").getValue().toString(),  //게시물 만든 시간

                                        //정수형을 문자형으로
//                                        (HashMap<String, Long>) snapshot.child("like").getChildren().iterator().next().getValue(), //좋아요 개수 이렇게 받아왔을 때 아무런 좋아요 입력이 없었다면 0을 받아 오게 해야 한다.
                                        snapshot.child("PosterKey").getValue().toString(),
                                        null);  //게시물 이미지
                            }
                        })
                        .build();



        //--------------------어댑터------------------
        adapter = new FirebaseRecyclerAdapter<PostingDTO, ViewHolder>(options) {
            @Override
            public PosterViewer.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.poster_item, parent, false);
                return new ViewHolder(view);
            }


            //뷰홀더
            @Override
            protected void onBindViewHolder(final PosterViewer.ViewHolder holder, final int position, final PostingDTO posting_set) {
                holder.setPosterKey(posting_set.getPosterkey());
                holder.setBody(posting_set.getBody());
                holder.setUserNickName(posting_set.getUserUID());
                holder.setUserUID(posting_set.getUserUID());
                //정수를 스트링 타입으로
//                holder.setLikeCount(String.valueOf(posting_set.getLikeCount()));
                holder.setPostedTime(posting_set.getPostedtime());
                holder.setNickName_Reply(posting_set.getUserUID());
                //위치 메타데이터
                holder.setMetadata(posting_set.getPosterkey());
//                //클릭한 포스터의 키값 받기
//                final String Key = holder.getUserPosterKey(posting_set.getPosterkey());



                //----------------------------댓글 개수 스냅샷----------------------------
                if (FLAG <= 1){
                    //유저 포스터키 참조
                    mdataref.child("Reply").child(UserPosterKeyList.get(position)).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String replycount = String.valueOf(dataSnapshot.getChildrenCount());
                            holder.setReplyCount(replycount);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }else{
                    //전체 포스터키 참조
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
                }//----------------------------댓글 개수 스냅샷----------------------------






                //---------------게시물 삭제 클릭------------클릭한 게시물 개별 접근------------
                holder.vetB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence info[] = new CharSequence[] {"삭제","수정" };
                        AlertDialog.Builder builder = new AlertDialog.Builder(PosterViewer.this);
//                        builder.setTitle()
                        builder.setItems(info, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which == 0){
                                    //클릭한 게시물의 키값 가져오기
                                   String Key = holder.getUserPosterKey(posting_set.getPosterkey());
                                    Log.i("포스터키", "클릭한 PosterKey : "+Key);

                                    //UserPostList 에서의 삭제
                                    mdataref.child("UserList").child(userUID).child("UserPosterList").child(Key).removeValue();
                                    //PosterList 에서의 삭제
                                    mdataref.getDatabase().getReference("PosterList").child(Key).removeValue();
                                    //댓글 DB 삭제
                                    mdataref.getDatabase().getReference("Reply").child(Key).removeValue();
                                    //스토리지에서의 게시물 이미지 삭제
                                    mstorageRef.child("PosterPicList").child(Key).child("PosterIMG").delete();
                                }
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    }
                });//-------------게시물 삭제 클릭------------




                //-----------------------------유저 피드로 이동하기 클릭-----------------------------
                holder.profileIMG.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        //플래그가 0이나 1이면 프로필에서 게시물을 보는거기 때문에 유저 게시물 DB를 참조
                        if (FLAG<=1){
                            Log.i("쿼리", "로그인한 게시물 DB 참조");

                            //유저 게시물DB에서 유저 UID가져오기 - 유저피드에서 다시 유저피드로 이동 클릭시 문제 있음
                            mdataref.child("UserList").child(PosterUserUID).child("UserPosterList").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    //전체 게시물 (홈,서치Act) 노드에서의 유저UID 얻기 - 클릭한 전체 게시물ID
                                    String PosterUserUID = dataSnapshot.child(UserPosterKeyList.get(position)).child("UserUID").getValue().toString();

                                    //** 가져온 유저UID가 자신의 UID와 같으면 자신의 프로필로 이동 ->Myinfo.class ->게시물 위치 다름 userUID 다시받기 **
                                    if (PosterUserUID.equals(userUID)){
                                        Intent intent2 = new Intent(PosterViewer.this,Myinfo.class);
                                        startActivity(intent2);

                                        //같지 않다면 유저프로필로 이동
                                    }else{
                                        //유저 피드로 유저 UID보내고 이동하기 -> UserProfile.class
                                        Intent intent1 = new Intent(PosterViewer.this,UserProfile.class);
                                        intent1.putExtra("PosterUserUID",PosterUserUID);
                                        startActivity(intent1);
                                    }


                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });



                        //플래그가 2이면 홈이나 서치화면에서 게시물을 보는거기 때문에 전체 게시물 DB 참조
                        }else {
                            Log.i("쿼리", "전체 게시물 DB 참조");


                            //전체 게시물DB에서 유저 UID가져오기 -홈,서치Act에서 접근
                            mdataref.child("PosterList").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    //전체 게시물 노드에서의 유저UID - 클릭한 전체 게시물ID
                                    String PosterUserUID = dataSnapshot.child(PosterKeyList.get(position)).child("UserUID").getValue().toString();


                                    //** 가져온 유저UID가 자신의 UID와 같으면 자신의 프로필로 이동 ->Myinfo.class ->게시물 위치 다름 userUID 다시받기 **
                                    if (PosterUserUID.equals(userUID)){
                                        Intent intent2 = new Intent(PosterViewer.this,Myinfo.class);
                                        startActivity(intent2);
                                    }else{
                                        //유저 피드로 유저 UID보내고 이동하기 -> UserProfile.class
                                        Intent intent1 = new Intent(PosterViewer.this,UserProfile.class);
                                        intent1.putExtra("PosterUserUID",PosterUserUID);
                                        startActivity(intent1);
                                    }


                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });


                        }

                    }
                });//-----------------------------유저 피드로 이동하기 클릭-----------------------------





                //댓글달기 화면으로 이동 - (인풋텍스트뷰에 포커스 작업해야함) 말풍선 버튼
                holder.replyB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //유저의 포스터키 보내기
                        if (FLAG <= 1){
                            Intent intent1 = new Intent(PosterViewer.this,ReplyDisplay.class);
                            intent1.putExtra("PosterKey", UserPosterKeyList.get(position)); //전체 DB PosterKey를 보냄
                            startActivity(intent1);
                        }else {
                            //전체 DB 포스터키 보내기
                            Intent intent2 = new Intent(PosterViewer.this,ReplyDisplay.class);
                            intent2.putExtra("PosterKey", PosterKeyList.get(position)); //전체 DB PosterKey를 보냄
                            startActivity(intent2);
                        }

                    }
                });


                //전체 댓글보기버튼
                holder.replyviewB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //유저의 포스터키 보내기
                        if (FLAG <= 1){
                            Intent intent1 = new Intent(PosterViewer.this,ReplyDisplay.class);
                            intent1.putExtra("PosterKey", UserPosterKeyList.get(position)); //전체 DB PosterKey를 보냄
                            startActivity(intent1);
                        }else {
                            //전체 DB 포스터키 보내기
                            Intent intent2 = new Intent(PosterViewer.this,ReplyDisplay.class);
                            intent2.putExtra("PosterKey", PosterKeyList.get(position)); //전체 DB PosterKey를 보냄
                            startActivity(intent2);
                        }
                    }
                });






                //내피드에서 선택한 게시물 포커스 주기 - 액티비티 최초실행시 실행
                if (FIRST_FOCUS){
                    Intent intent = getIntent();
                    int focus = intent.getIntExtra("FOCUS",0);
                    recyclerView.smoothScrollToPosition(focus);
                }
                FIRST_FOCUS = false;




//                //좋아요 버튼
//                holder.likeButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        //포스터키는 리스트사용 -
//                        //포스터리스트에도 업데이트 되야 한다. 그 구조도 염두해 둬야함.
//                        onStarClicked(mdataref.child(userUID).child("UserPosterList").child(PosterKeyList.get(position)));
//                        //좋아요를 해당 계정이 이미 눌렀다면 빈하트, 아니라면 하트
//                        if (postingDTOS.get(position).likes.containsKey(userUID)){
//                            holder.likeButton.setImageResource(R.drawable.like3);
//                        }else {
//                            holder.likeButton.setImageResource(R.drawable.like1);
//                        }
//                    }
//                });
            }
        };//--------------------어댑터--------------------

        recyclerView.setAdapter(adapter);
    }//----------------------------파이어베이스 어댑터 메소드---------------------------------------




    //포스터뷰어 클래스에 가져올 데이터 쿼리 스위치
    public Query QuerySwitch(int FLAG, String PosterUserUID){
        switch (FLAG){
            case -1:
                Toast.makeText(getApplicationContext(),"에러발생",Toast.LENGTH_SHORT).show();
                break;

            //내 프로필 게시물 DB 쿼리 - 유저 게시물 DB
            case 0 : Query fromMyinfo = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("UserList")
                    .child(userUID)
                    .child("UserPosterList");
            Log.i("쿼리 스위치","0번 내 게시물 쿼리 : 유저 게시물 DB");
            return fromMyinfo;

            //유저피드 게시물 DB 쿼리 - 유저 게시물 DB
            case 1 : Query fromUserfeed = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("UserList")
                    .child(PosterUserUID)
                    .child("UserPosterList");
                Log.i("쿼리 스위치","1번 유저피드 게시물 쿼리 : 유저 게시물 DB");
            return fromUserfeed;

            //검색 또는 홈 게시물 DB 쿼리 - 전체 게시물 DB
            case 2 : Query fromSearch = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("PosterList");
                Log.i("쿼리 스위치","2번 검색 게시물 쿼리 : 전체 게시물 DB");
            return fromSearch;
        }
        return null;
    }







    //좋아요 메소드
    //문제 원인의 발단 - DTO형식을 사용하여 DB에 업로드 했으나 좋아요를 업로드 하면 기존 형식을 무너뜨림 = 에러발생
//    private void onStarClicked(final DatabaseReference postRef) {
//            postRef.runTransaction(new Transaction.Handler() {
//                @Override
//                public Transaction.Result doTransaction(MutableData mutableData) {
//                    Log.i("좋아요","트랜젝션 실행확인");
//
//                    PostingDTO p = mutableData.getValue(PostingDTO.class);
//
//                    if (p == null) {
//                        return Transaction.success(mutableData);
//                    }
//                    HashMap<String,Long> Count = new HashMap<>();
//
//                    if (p.likes.containsKey(userUID)) {
//                        // Unstar the post and remove self from stars
//                        Log.i("좋아요","containsKey 실행확인");
//                        long count = -1;
//                        Count.put("Count",count);
//                        p.setLikeCount(Count);
//                        p.likes.remove(userUID);
//
//                    } else {
//                        // Star the post and add self to stars
//                        Log.i("좋아요","else 실행확인");
//                        long count = +1;
//                        Count.put("Count",count); //좋아요 최하위노드
//                        p.setLikeCount(Count);
//                        p.likes.put(userUID, true);
//                    }
//
//                    // -------------Set value and report transaction success---------
//                    mutableData.child("like").setValue(p); //좋아요 루트 노드
//                    return Transaction.success(mutableData);
//                }
//
//                @Override
//                public void onComplete(DatabaseError databaseError, boolean b,
//                                       DataSnapshot dataSnapshot) {
//                    // Transaction completed
//                    Log.i("좋아요","b : "+b);
//                    Log.i("좋아요","databaseError : "+databaseError);
//                    Log.i("좋아요","dataSnapshot : "+dataSnapshot);
//                }
//            });
//    }
}

