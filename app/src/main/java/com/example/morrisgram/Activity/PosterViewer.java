package com.example.morrisgram.Activity;

import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.like.LikeButton;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

//게시물 액티비티
public class PosterViewer extends AddingPoster_BaseAct implements SwipyRefreshLayout.OnRefreshListener{

    SwipyRefreshLayout mSwipeRefreshLayout;
    //데이터베이스의 주소를 지정 필수
    private DatabaseReference mdataref = FirebaseDatabase.getInstance().getReference("UserList");
    //현재 접속중인 유저UID가져오기
    private FirebaseUser uid = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseAuth firebaseAuth;
    private StorageReference mstorageRef = FirebaseStorage.getInstance().getReference();
    private String userUID = uid.getUid();

    //파이어베이스 리사이클러뷰
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;

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
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

//        //아이템 역순 추가정렬 = true
//        linearLayoutManager.setReverseLayout(false);
//        linearLayoutManager.setStackFromEnd(false);
//        recyclerView.setLayoutManager(linearLayoutManager); //setLayoutManager 메소드를 사용해서 매니저를 리사이클러뷰에 설정

        mSwipeRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.refresh_posterviewer);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setDirection(SwipyRefreshLayoutDirection.TOP);

        //포커스 boolean
        FIRST_FOCUS = true;
        fetch();
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
                Intent intent = new Intent(PosterViewer.this, LikeAlarm.class);
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
//---------------------------------------------------------------------------------
    }
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
    //새로고침
    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        mSwipeRefreshLayout.setRefreshing(false);
        fetch();
        adapter.startListening();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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

        public void setUserNickName(String string) {
            UserNicName.setText(string);
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
            //스토리지에서 이미지 받아오기
            StorageReference imageRef = mstorageRef.child("PosterPicList/"+uri+"/PosterIMG");
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

        public void setNickName_Reply(String string){
            NickName_Reply.setText(string);
        }
    }
    //----------------------------파이어베이스 어댑터---------------------------------------
    private void fetch() {
        Query query = FirebaseDatabase.getInstance()
                //BaseQuery
                .getReference()
                .child("UserList")
                .child(userUID)
                .child("UserPosterList");

        Log.i("파베", "포스터 뷰어 query 경로 확인 : "+query.toString());

        //DB에 정보를 받아서 가져오는 스냅샷 - 스트링형식으로 받아와야함
        FirebaseRecyclerOptions<Posting_DTO> options =
                new FirebaseRecyclerOptions.Builder<Posting_DTO>()
                        .setQuery(query, new SnapshotParser<Posting_DTO>() {
                            @NonNull
                            @Override
                            public Posting_DTO parseSnapshot(@NonNull DataSnapshot snapshot) {
                                Log.i("파베", "포스터 뷰어 스냅샷 메소드 작동 확인");
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

        adapter = new FirebaseRecyclerAdapter<Posting_DTO, ViewHolder>(options) {
            @Override
            public PosterViewer.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.poster_item, parent, false);
                return new ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final PosterViewer.ViewHolder holder, final int position, Posting_DTO posting_dto) {
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(PosterViewer.this);
//                        builder.setTitle()
                        builder.setItems(info, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which == 0){
//                                    mdataref.child(userUID).child("UserPosterList").child();
                                    Toast.makeText(PosterViewer.this, "옵션 클릭확인", Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    }
                });
                //내피드에서 선택한 게시물 포커스 주기 - 액티비티 최초실행시 실행
                if (FIRST_FOCUS){
                    Intent intent = getIntent();
                    int focus = intent.getIntExtra("FOCUS",0);
                    recyclerView.smoothScrollToPosition(focus);
                }
                FIRST_FOCUS = false;

                //터치
                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(PosterViewer.this, String.valueOf(position), Toast.LENGTH_SHORT).show();

                    }
                });
            }

        };
        recyclerView.setAdapter(adapter);
    }
}

