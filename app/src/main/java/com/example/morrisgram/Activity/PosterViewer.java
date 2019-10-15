package com.example.morrisgram.Activity;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
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

import com.example.morrisgram.Activity.BaseActivity.AddingPoster_BaseAct;
import com.example.morrisgram.DTO_Classes.Firebase.Posting_DTO;
import com.example.morrisgram.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

//게시물 액티비티
public class PosterViewer extends AddingPoster_BaseAct {

    //데이터베이스의 주소를 지정 필수
    private DatabaseReference mdataref = FirebaseDatabase.getInstance().getReference("UserList");
    //현재 접속중인 유저UID가져오기
    private FirebaseUser uid = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseAuth firebaseAuth;
    private StorageReference mstorageRef = FirebaseStorage.getInstance().getReference();
    private String userUID = uid.getUid();

    //리사이클러뷰
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private FirebaseRecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 화면을 portrait(세로) 화면으로 고정하고 싶은 경우
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_poster_viewer);

        recyclerView = findViewById(R.id.recyclerView_myinfo);
        gridLayoutManager = new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        fetch();
//-----------------------------------화면이동----------------------------------------
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
    private void fetch() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("UserList")
                .child(userUID)
                .child("UserPosterList")
                .child(GetPosterKey().get(3).toString());
        Log.i("파베", "query 경로 확인 : "+query.toString());
        FirebaseRecyclerOptions<Posting_DTO> options =
                new FirebaseRecyclerOptions.Builder<Posting_DTO>()
                        .setQuery(query, new SnapshotParser<Posting_DTO>() {
                            @NonNull
                            @Override
                            public Posting_DTO parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new Posting_DTO(snapshot.child(GetPosterKey().toString()).child("UserUID").getValue().toString(),
                                        snapshot.child("UserNicName").getValue().toString(),
                                        snapshot.child("Body").getValue().toString(),
                                        snapshot.child("PostedTime").getValue().toString(),
                                        snapshot.child("LikeCount").getValue().toString(),
                                        snapshot.child("ReplyCount").getValue().toString(),
                                        snapshot.child("PosterKey").getValue().toString());
                            }
                        })
                        .build();
        adapter = new FirebaseRecyclerAdapter<Posting_DTO, ViewHolder>(options) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.poster_item, parent, false);
                return new ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ViewHolder Holder, final int position, @NonNull Posting_DTO posting_dto) {
                Holder.setUserNicName(posting_dto.getUserNickName());
                Holder.setUserUID(Uri.parse(posting_dto.getUserUID()));
                Holder.setBody(posting_dto.getBody());

                //스트링으로 받아서 uri로 파싱??
                Holder.setPic(Uri.parse(posting_dto.getPosterKey()));
                Holder.setPostedTime(posting_dto.getPostedTime());

                //스트링으로로 받아서 숫자로 파싱?
                //숫자로 받아서 스트링으로 파싱
                Holder.setLikeCount(String.valueOf(posting_dto.getLikeCount()));
                Holder.setReplyCount(String.valueOf(posting_dto.getReplyCount()));

                Holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(PosterViewer.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
                    }
                });
                recyclerView.setAdapter(adapter);
            }
        };
    }
   public class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout root;
        public TextView UserNicName;
        public TextView Body;
        public TextView PostedTime;
        public TextView LikeCount;
        public TextView ReplyCount;
        public ImageView UserUID;
        public ImageView Pic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            UserNicName = itemView.findViewById(R.id.nicknameTV);
            Body = itemView.findViewById(R.id.bodyTV);
            PostedTime = itemView.findViewById(R.id.timeTV);
            LikeCount = itemView.findViewById(R.id.like_counter);
            ReplyCount = itemView.findViewById(R.id.reply_counter);
            Pic = itemView.findViewById(R.id.imageView_posteritem);
            UserUID = itemView.findViewById(R.id.profileIMG_posteritem);
        }

        public void setUserNicName(String string) {
            UserNicName.setText(string);
        }

        public void setUserUID(Uri uri) {
            UserUID.setImageURI(uri);
        }

        public void setBody(String string) {
            Body.setText(string);
        }

        public void setPic(Uri uri) {
            Pic.setImageURI(uri);
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
    }
}

