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

import android.content.SharedPreferences;
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
import com.example.morrisgram.CameraClass.GlideApp;
import com.example.morrisgram.DTOclass.Firebase.PostingDTO;
import com.example.morrisgram.DTOclass.Firebase.PreView;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
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
    private DatabaseReference mdataref = FirebaseDatabase.getInstance().getReference("UserList");
    //현재 접속중인 유저UID가져오기
    private FirebaseUser uid = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseAuth firebaseAuth;
    private StorageReference mstorageRef = FirebaseStorage.getInstance().getReference();
    private String userUID = uid.getUid();

    //포스터키 수집용 리스트
    private List<String> PosterKeyList = new ArrayList<>();
    private List<PostingDTO> postingDTOS = new ArrayList<>();

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
                Intent intent = new Intent(Myinfo.this, LikeAlarm.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
        //팔로워 버튼 페이지 이동
        ViewGroup followersB = (ViewGroup) findViewById(R.id.FollowerTV_my);
        followersB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Myinfo.this, Followers_AND_Following.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //팔로잉 버튼 페이지 이동
        ViewGroup followingsB = (ViewGroup) findViewById(R.id.FollowingTV_my);
        followingsB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Myinfo.this, Followers_AND_Following.class);
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
//---------------------------------------------------------------------------------




        //처음 앱을 실행하고 버튼을 눌렀을 때만 값을 읽어옴 addListenerForSingleValueEvent
        //수시로 해당 디비의 하위값들이 변화를 감지하고 그떄마다 값을 불러오려면 addValueEventListener를 사용
        mdataref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //현재 로그인된 유저 정보와 일치하는 데이터를 가져오기.
                String NameVal = (String) dataSnapshot.child(userUID).child("UserInfo").child("NickName").getValue();
                String WebsiteVal = (String) dataSnapshot.child(userUID).child("Profile").child("Website").getValue();
                String IntroVal = (String) dataSnapshot.child(userUID).child("Profile").child("Introduce").getValue();
                String posternum = String.valueOf( (int) dataSnapshot.child(userUID).child("UserPosterList").getChildrenCount());

                posternumTV.setText(posternum);
                hname.setText(NameVal);
                pname.setText(NameVal);
                idtv.setText(NameVal);
                website.setText(WebsiteVal);
                intro.setText(IntroVal);


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





        //-----------현재 접속한 회원의 스토리지 이미지만 삭제 - 회원의 모든 포스터키 데이터 쿼리----------

//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                try {
//                    String PosterKeyGet = dataSnapshot.getValue().toString();
//                    Log.i("포스터키","데이터스냅샷 : "+PosterKeyGet);
//
//                    PostingDTO posting_dto = gson.fromJson(gson.toJson(PosterKeyGet),PostingDTO.class);
//                    Log.i("포스터키","posting_dto : "+posting_dto.toString());
//
//                    String json = gson.toJson(PosterKeyGet,StringType);
//                    Log.i("포스터키","json : "+json);
//
//                    JSONArray jsonArray = new JSONArray(json);
//                    Log.i("포스터키","jsonArray : "+jsonArray.getString(0));
//
//                    for(int i=0; i<jsonArray.length(); i++){
//                        String Key = jsonArray.getString(i);
//                        Log.i("포스터키","jsonArray : "+jsonArray.getString(i));
//
//                        JSONObject jsonObject = new JSONObject(Key);
//
//                        //종단 처리 되지 않았음. "Key":"value"
//                        String PosterKey = jsonObject.getString("PosterKey");
//                        Log.i("포스터키","회원탈퇴 포스터키 삭제 요구 : "+PosterKey);
//                    }
//                }catch (NullPointerException | JSONException | TypeNotPresentException e){
//                    e.getStackTrace();
//                    Log.i("포스터키","Exception e : "+e);
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });//-----------------------------------------------------------------------

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
                    uid.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(Myinfo.this, "계정이 삭제 되었습니다.", Toast.LENGTH_LONG).show();
                            //유저의 DB 모든 데이터 삭제
                            mdataref.child(userUID).removeValue();

                            //유저의 프로필 사진 삭제
                            mstorageRef.child(userUID).child("ProfileIMG").child("ProfileIMG").delete();

                            //-GetPosterKey is ArrayList-
                            for (int i=0; i<PosterKeyList.size(); i++){
                                //스토리지에서 유저의 게시물 이미지 모두 삭제
                                mstorageRef.child("PosterPicList").child(PosterKeyList.get(i)).child("PosterIMG").delete();
                                //유저의 게시물 전체 DB삭제
                                mdataref.getDatabase().getReference("PosterList").child(GetPosterKey().get(i).toString()).removeValue();
                            }

                            //내부 DB의 포스터키 삭제
                            SharedPreferences MY_POSTER_KEYS = getSharedPreferences("POSTER_KEYS",MODE_PRIVATE);
                            SharedPreferences.Editor KEY_EDITOR = MY_POSTER_KEYS.edit();
                            KEY_EDITOR.clear();
                            KEY_EDITOR.apply();

                            //회원탈퇴 처리
                            uid.delete();

                            //로그인 화면으로 이동
                            finish();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
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

