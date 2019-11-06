package com.example.morrisgram.Activity;

import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.morrisgram.Activity.BaseActivity.AddingPoster_BaseAct;
import com.example.morrisgram.Activity.FollowFragment.FollowPager;
import com.example.morrisgram.CameraClass.GlideApp;
import com.example.morrisgram.DTOclass.FollowDTO.FollowerDTO;
import com.example.morrisgram.DTOclass.FollowDTO.FollowingDTO;
import com.example.morrisgram.DTOclass.PosterDTO.PostingDTO;
import com.example.morrisgram.DTOclass.PosterDTO.PreView;
import com.example.morrisgram.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
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
import java.util.List;

public class Myinfo extends AddingPoster_BaseAct implements SwipeRefreshLayout.OnRefreshListener,NavigationView.OnNavigationItemSelectedListener {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ImageButton homeB;
    private ImageButton optionB;
    private Button profilemodifyB;

    //내 정보표시
    private TextView pname;
    private TextView idtv;
    private TextView intro;
    private TextView website;
    private ImageView profileimg;

    //Count
    private TextView posternumTV;
    private TextView followernumTV;
    private TextView forllowingnumTV;

    private DrawerLayout mdrawerLayout;
    private ActionBarDrawerToggle mtoggle;

    //데이터베이스의 주소를 지정 필수
    private DatabaseReference mdataref = FirebaseDatabase.getInstance().getReference();

    //현재 접속중인 유저UID가져오기
    private FirebaseUser uid = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseAuth firebaseAuth;
    private StorageReference mstorageRef = FirebaseStorage.getInstance().getReference();
    private String userUID = uid.getUid();

    //포스터키 수집용 리스트
    private List<String> PosterKeyList = new ArrayList<>();
    private List<PostingDTO> postingDTOS = new ArrayList<>();

    //팔로워 유저 UID 수집용 리스트
    private List<String> MyFollowerUIDList = new ArrayList<>();
    private List<FollowerDTO> followerDTOS = new ArrayList<>();

    //팔로잉 유저 UID 수집용 리스트
    private List<String> MyFollowingUIDList = new ArrayList<>();
    private List<FollowingDTO> followingDTOS = new ArrayList<>();

    //파이어베이스 리사이클러뷰
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private FirebaseRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 화면을 portrait(세로) 화면으로 고정하고 싶은 경우
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_myinfo);

        Log.i("유저","계정 UID : "+userUID);

        //네비게이션 드로우바
        mdrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mtoggle = new ActionBarDrawerToggle(this, mdrawerLayout, R.string.open, R.string.close);
        mdrawerLayout.addDrawerListener(mtoggle);
        mtoggle.syncState();

        //내 정보 화면에 표시할 텍스트들
        optionB = (ImageButton) findViewById(R.id.optionB_my);
        pname = (TextView) findViewById(R.id.name);
        idtv = (TextView) findViewById(R.id.idtv_my);
        website = (TextView) findViewById(R.id.website_my);
        intro = (TextView) findViewById(R.id.introduce_my);

        //CountTV
        posternumTV = (TextView) findViewById(R.id.PosterNum_my);
        followernumTV = (TextView) findViewById(R.id.FollowersNum_my);
        forllowingnumTV = (TextView) findViewById(R.id.FollowingsNum_my);


        profileimg = (ImageView) findViewById(R.id.profileIMG_my);
        recyclerView = findViewById(R.id.recyclerView_myinfo);

        //네비게이션뷰 리스너
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationview);
        navigationView.setNavigationItemSelectedListener(this);

        //헤더제어
        View nav_header_view = navigationView.getHeaderView(0);
        final TextView hname;
        hname = (TextView) nav_header_view.findViewById(R.id.username_header);

        //네비게이션바 버튼
        optionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdrawerLayout.openDrawer(GravityCompat.END);
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_my);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        //파이어베이스 리사이클러뷰
        gridLayoutManager = new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);

        //리사이클러뷰 어댑터 클래스
        fetch();
//-----------------------------------화면이동----------------------------------------
        homeB = (ImageButton) findViewById(R.id.homeB_my);
        homeB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Myinfo.this, Home.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
        //탐색 화면 이동
        ImageButton searchB;
        searchB = (ImageButton) findViewById(R.id.searchB_my);
        searchB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Myinfo.this, Search.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
//좋아요 알람 화면 이동
        ImageButton likealarmB;
        likealarmB = (ImageButton) findViewById(R.id.likeB_my);
        likealarmB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Myinfo.this, Alarm.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
        //팔로워 버튼 페이지 이동
        ViewGroup followersB = (ViewGroup) findViewById(R.id.FollowerTV_my);
        followersB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Myinfo.this, FollowPager.class);
                intent.putExtra("FLAG",0);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //팔로잉 버튼 페이지 이동
        ViewGroup followingsB = (ViewGroup) findViewById(R.id.FollowingTV_my);
        followingsB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Myinfo.this, FollowPager.class);
                intent.putExtra("FLAG",0);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //프로필 변경하기로 이동
        profilemodifyB = (Button) findViewById(R.id.profileB);
        profilemodifyB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Myinfo.this, ProfileModify.class);
                startActivity(intent);
            }
        });

        //포스팅 화면 이동
        ImageButton addposterB = (ImageButton) findViewById(R.id.addB_my);
        addposterB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAlbum();
            }
        });
//-----------------------------------화면이동----------------------------------------




        //처음 앱을 실행하고 버튼을 눌렀을 때만 값을 읽어옴 addListenerForSingleValueEvent
        //수시로 해당 디비의 하위값들이 변화를 감지하고 그떄마다 값을 불러오려면 addValueEventListener를 사용
        mdataref.child("UserList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //현재 로그인된 유저 정보와 일치하는 데이터를 가져오기.
                String NameVal = (String) dataSnapshot.child(userUID).child("UserInfo").child("NickName").getValue();
                String WebsiteVal = (String) dataSnapshot.child(userUID).child("Profile").child("Website").getValue();
                String IntroVal = (String) dataSnapshot.child(userUID).child("Profile").child("Introduce").getValue();
                hname.setText(NameVal);
                pname.setText(NameVal);
                idtv.setText(NameVal);
                website.setText(WebsiteVal);
                intro.setText(IntroVal);

                //게시물,팔로워,팔로잉 카운트
                String posternum = String.valueOf( (int) dataSnapshot.child(userUID).child("UserPosterList").getChildrenCount());
                String followernum = String.valueOf((int)dataSnapshot.child(userUID).child("FollowerList").getChildrenCount());
                String followingnum = String.valueOf((int)dataSnapshot.child(userUID).child("FollowingList").getChildrenCount());
                posternumTV.setText(posternum);
                followernumTV.setText(followernum);
                forllowingnumTV.setText(followingnum);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        //-----------------------------회원탈퇴시 데이터 삭제를 위한 팔로잉 & 팔로워 리스트 수집-----------------------------
        //자신의 팔로워 리스트 수집 = 내 UID를 자신의 팔로잉 리스트에 갖고 있는 유저UID
        mdataref.child("UserList").child(userUID).child("FollowerList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                followerDTOS.clear();
                MyFollowerUIDList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    FollowerDTO followerDTO = snapshot.getValue(FollowerDTO.class);
                    String GetKey = snapshot.getKey();

                    //클래스 주소값?
                    followerDTOS.add(followerDTO);
                    MyFollowerUIDList.add(GetKey);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
        //-----------------------------회원탈퇴시 데이터 삭제를 위한 팔로잉 & 팔로워 리스트 수집-----------------------------


        //--------------------게시물 키값 수집-----------------------
        mdataref.child("UserList").child(userUID).child("UserPosterList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

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
        //--------------------게시물 키값 수집-----------------------
    }//------------------크리에이트--------------




    //새로고침
    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
        fetch();
        adapter.startListening();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mtoggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.logout_navi) {
            //로그아웃
            Toast.makeText(Myinfo.this, "로그아웃 되었습니다.", Toast.LENGTH_LONG).show();
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signOut();
            finish();
            Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent1);
        }

        if (id == R.id.leave_navi) {
            //회원탈퇴
            AlertDialog.Builder alert_confirm = new AlertDialog.Builder(Myinfo.this);
            alert_confirm.setMessage("계정을 삭제 하시겠습니까?").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    uid = FirebaseAuth.getInstance().getCurrentUser();

                    //로그인 화면으로 이동
                    uid.delete();
                    finish();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));

                    uid.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(Myinfo.this, "계정이 삭제 되었습니다.", Toast.LENGTH_LONG).show();
                            try {
                                //상대 유저의 팔로워 리스트에서 내 UID 삭제
                                for (int i=0; i<MyFollowingUIDList.size(); i++){
                                    mdataref.getDatabase().getReference().child("UserList").child(MyFollowingUIDList.get(i)).child("FollowerList").child(userUID).removeValue();
                                    Log.i("삭제","내 계정 UID : "+userUID);
                                    Log.i("삭제","내 팔로잉 계정 리스트 : "+MyFollowingUIDList.get(i));
                                    Log.i("삭제","상대 유저 팔로워 리스트 경로 : "+mdataref.child("UserList").child(MyFollowerUIDList.get(i)).child("FollowerList").child(userUID).child("UID").toString());
                                }
                                //상대 유저의 팔로잉 리스트에서 내 UID 삭제
                                for (int i=0; i<MyFollowingUIDList.size(); i++){
                                    mdataref.getDatabase().getReference().child("UserList").child(MyFollowerUIDList.get(i)).child("FollowingList").child(userUID).removeValue();
                                    Log.i("삭제","내 계정 UID : "+userUID);
                                    Log.i("삭제","내 팔로워 계정 리스트 : "+MyFollowerUIDList.get(i));
                                    Log.i("삭제","상대 유저 팔로잉 리스트 경로 : "+mdataref.child("UserList").child(MyFollowerUIDList.get(i)).child("FollowingList").child(userUID).child("UID").toString());
                                }

                                //내가 쓴 댓글 삭제
                                for(int i=0; i<PosterKeyList.size(); i++){
                                    //자신의 포스터키 리스트
                                    mdataref.getDatabase().getReference().child("Reply").child(PosterKeyList.get(i)).removeValue();
                                }

                                //-GetPosterKey is ArrayList-
                                for (int i=0; i<PosterKeyList.size(); i++){
                                    //스토리지에서 유저의 게시물 이미지 모두 삭제
                                    mstorageRef.child("PosterPicList").child(PosterKeyList.get(i)).child("PosterIMG").delete();
                                    //유저의 게시물 전체 DB삭제
                                    mdataref.getDatabase().getReference("PosterList").child(PosterKeyList.get(i)).removeValue();
                                    Log.i("삭제","내 게시물 리스트 : "+MyFollowerUIDList.get(i));
                                }

                                //유저의 프로필 사진 삭제
                                mstorageRef.child(userUID).child("ProfileIMG").child("ProfileIMG").delete();

                                //유저의 DB 모든 데이터 삭제
                            mdataref.child("UserList").child(userUID).removeValue();

                                //회원탈퇴 처리
                                FirebaseAuth.getInstance().getCurrentUser().delete();
                                Log.i("삭제","계정삭제 확인 : "+FirebaseAuth.getInstance().getCurrentUser().toString());


                            }catch (NullPointerException e){
                                e.getStackTrace();
                            }
                        }
                    });
                }
            });
            alert_confirm.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(Myinfo.this, "취소", Toast.LENGTH_LONG).show();
                }
            });
            alert_confirm.show();
        }
        if (id == R.id.setting_navi) {
            //설정
        }
        return false;
    }




    //뒤로가기 버튼으로 네비게이션 닫기
    @Override
    public void onBackPressed() {
        if (mdrawerLayout.isDrawerOpen(GravityCompat.END)) {
            mdrawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }
    //네비게이션 드로어 메소드
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    //--------------생명주기--------------
    public void onStart() {
        super.onStart();
        Log.i("파베", "마이 스타트");
        //Glide를 통한 프로필 이미지 바인딩
        StorageReference imageRef = mstorageRef.child(userUID + "/ProfileIMG/ProfileIMG");
        GlideApp.with(Myinfo.this)
                .load(imageRef)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .dontAnimate()
                .placeholder(R.drawable.noimage)
                .centerCrop()
                .into(profileimg);

        adapter.startListening();
    }
    public void onResume() {
        super.onResume();
//        Log.i("파베", "마이 리즈메");

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

    public void onDestroy() {
        super.onDestroy();
//        Log.i("파베", "마이 디스트로이");
    }

    public void onRestart() {
        super.onRestart();
//        Log.i("파베", "마이 리스타트");
    }






    //------------------------뷰홀더------------------------------
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout root;
        public ImageView PosterKey;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            Log.i("파베", "ViewHolder 메소드 작동 확인");
            root = itemView.findViewById(R.id.preview_userfeed_root);
            PosterKey = itemView.findViewById(R.id.preview_userfeed_IMG);
        }
        //스토리지에서 게시물 미리보기 이미지 받아오기
        public void setPosterKey(String uri) {
            Log.i("파베", "setPic 메소드 작동 확인");
            StorageReference imageRef = mstorageRef.child("PosterPicList").child(uri).child("PosterIMG");
            GlideApp.with(Myinfo.this)
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
    private void fetch() {
        try {
            Query query = FirebaseDatabase.getInstance()
                    //BaseQuery
                    .getReference()
                    .child("UserList")
                    .child(userUID)
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
                            Toast.makeText(Myinfo.this, String.valueOf(position), Toast.LENGTH_SHORT).show();

                            final int FLAG = 0;
                            Intent intent = new Intent(Myinfo.this,PosterViewer.class);
                            intent.putExtra("FOCUS",position);
                            intent.putExtra("FLAG",FLAG);
                            intent.putExtra("PosterUserUID",userUID);

                            startActivity(intent);
                            overridePendingTransition(0, 0);
                        }
                    });
                }
            };
            recyclerView.setAdapter(adapter);

        }catch (NullPointerException e){
            e.getStackTrace();
            Log.i("try", "NullPointerException :"+e);
        }
    }//----------------------------파이어베이스 어댑터 클래스---------------------------------------




}//---------------myinfo class---------------

