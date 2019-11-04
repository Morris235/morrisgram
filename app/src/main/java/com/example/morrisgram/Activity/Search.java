package com.example.morrisgram.Activity;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.morrisgram.Activity.BaseActivity.AddingPoster_BaseAct;
import com.example.morrisgram.CameraClass.GlideApp;
import com.example.morrisgram.DTOclass.PreView;
import com.example.morrisgram.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter_LifecycleAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class Search extends AddingPoster_BaseAct implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    //뒤로가기 제어
    private final long FINISH_INTERVAL_TIME = 2000;
    private long   backPressedTime = 0;

    //파이어베이스 리사이클러뷰
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private FirebaseRecyclerAdapter adapter;

    private FirebaseRecyclerAdapter SearchAdapter;

    //파이어베이스 유저 정보
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    private EditText searchbar;

    ArrayList<String> unFilteredList;
    ArrayList<String> filteredList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 화면을 portrait(세로) 화면으로 고정하고 싶은 경우
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_search);

        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.recyclerView_search);
        gridLayoutManager = new GridLayoutManager(this,3);

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);

        //smooth scrolling
        recyclerView.setNestedScrollingEnabled(false);

        //데이터 검색바 바인드
        searchbar = (EditText) findViewById(R.id.searchbar_search);
        //게시물 검색
        searchbar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    String SearchWord = searchbar.getText().toString();
                    if (SearchWord.equals("")){
                        Toast.makeText(getApplicationContext(),"내용을 입력해주세요",Toast.LENGTH_SHORT).show();
                    }else {
                        Log.i("검색","검색어 확인 : "+SearchWord);
                        FirebaseUserSearch(SearchWord);
                        SearchAdapter.startListening();
                        searchbar.setText("");
                    }
                }
                return true;
            }
        });
        //리사이클러뷰 어댑터 클래스
        fetch();

//-----------------------------------화면이동----------------------------------------
//홈 화면 이동
        ImageButton homeB;
        homeB = (ImageButton) findViewById(R.id.homeB_search);
        homeB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Search.this, Home.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
//좋아요 알람 화면 이동
        ImageButton likealarmB;
        likealarmB = (ImageButton) findViewById(R.id.likeB_search);
        likealarmB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Search.this, LikeAlarm.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

//내 프로필 화면 이동
        ImageButton myinfoB;
        myinfoB = (ImageButton) findViewById(R.id.myB_search);
        myinfoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Search.this, Myinfo.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });

        //포스팅 화면 이동
        ImageButton addposterB = (ImageButton) findViewById(R.id.addB_search);
        addposterB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAlbum();
            }
        });
//---------------------------------------------------------------------------------
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_search);
        mSwipeRefreshLayout.setOnRefreshListener(this);


    }//-------------크리에이트-------------------

    //새로고침
    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
        fetch();
        adapter.startListening();
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public void onResume(){
        super.onResume();
    }

    //뒤로가기 제어
    @Override
    public void onBackPressed() {
        try {
            long tempTime = System.currentTimeMillis();
            long intervalTime = tempTime - backPressedTime;

            //처음눌렀을 때
            if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime)
            {
                //뒤로가기 대기
                super.onBackPressed();
//
//                //전체 게시물 표시
//                SearchAdapter.stopListening();
//                adapter.startListening();
//                fetch();
            }
            else
            {
                //시간안에 클릭하면 첫번째 조건식 회피를 위해 정수 대입
                backPressedTime = tempTime;
            }

        }catch (NullPointerException e){
            e.getStackTrace();
        }
    }
//    //어댑터 필터링
//        @Override
//        public Filter getFilter() {
//            return new Filter() {
//                @Override
//                protected FilterResults performFiltering(CharSequence constraint) {
//                    String charString = constraint.toString();
//                    if (charString.isEmpty()){
//                        filteredList = unFilteredList;
//                    }else {
//                        ArrayList<String> filteringList = new ArrayList<>();
//                        for (String name : unFilteredList){
//                            if (name.toLowerCase().contains(charString.toLowerCase())){
//                                filteringList.add(name);
//                            }
//                        }
//                        filteredList = filteringList;
//                    }
//                    FilterResults filterResults = new FilterResults();
//                    filterResults.values = filteredList;
//                    return filterResults;
//                }
//                //리사이클러뷰를 업데이트해주는 작업 수행 메소드
//                @Override
//                protected void publishResults(CharSequence constraint, FilterResults results) {
//                    filteredList = (ArrayList<String>)results.values;
//                    adapter.notifyDataSetChanged();
//                }
//            };
//
//
//        }




    //------------------------뷰홀더------------------------------
    public class ViewHolder extends RecyclerView.ViewHolder{

        public ConstraintLayout root;
        public ImageView PosterKey;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            root = itemView.findViewById(R.id.preview_search_root);
            PosterKey = itemView.findViewById(R.id.preview_search_IMG);
        }

        //스토리지에서 게시물 미리보기 이미지 받아오기
        public void setPosterKey(String uri) {
            Log.i("파베", "setPic 메소드 작동 확인");
            StorageReference imageRef = mstorageRef.child("PosterPicList").child(uri).child("PosterIMG");
            GlideApp.with(Search.this)
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


    }
    //----------------------------파이어베이스 전체 게시물 프리뷰 어댑터---------------------------------------
    private void fetch() {
        //BaseQuery
        Query query = FirebaseDatabase.getInstance()
                //BaseQuery
                .getReference()
                .child("PosterList")
                .orderByChild("TimeStemp");

        //orderByChild()	지정된 하위 키의 값에 따라 결과를 정렬합니다.
        //orderByKey()	    하위 키에 따라 결과를 정렬합니다.
        //orderByValue()	하위 값에 따라 결과를 정렬합니다.

        Log.i("파베", "홈 뷰어 query 경로 확인 : " + query.toString());

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
                        .inflate(R.layout.preview_search_item, parent, false);
                return new ViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(final ViewHolder holder, final int position, PreView preView) {
                holder.setPosterKey(preView.getPosterKey());

                //클릭한 이미지의 포스트뷰어로 이동하기
                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(Search.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Search.this, PosterViewer.class);

                        //유저피드에서 해당 게시물 데이터를 받기 위한 쿼리스위치 신호
                        final int FLAG = 2;
                        intent.putExtra("FLAG",FLAG);
                        intent.putExtra("FOCUS", position);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    }
                });
            }
        };

        recyclerView.setAdapter(adapter);
    }





//----------------------------------------검색용 어댑터------------------------------------------------
    //검색 뷰홀더 클래스
    private class UserViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout root;
        public ImageView PosterKey;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            root = itemView.findViewById(R.id.preview_search_root);
            PosterKey = itemView.findViewById(R.id.preview_search_IMG);
        }

        //스토리지에서 게시물 미리보기 이미지 받아오기
        public void setPosterKey(String uri) {
            Log.i("파베", "setPic 메소드 작동 확인");
            StorageReference imageRef = mstorageRef.child("PosterPicList").child(uri).child("PosterIMG");
            GlideApp.with(Search.this)
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
    }

    //검색 어댑터
    private void FirebaseUserSearch(String SearchWord){
        Log.i("검색","게시물 검색 메소드 동작");
        //BaseQuery
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("PosterList")
                .orderByChild("Body")
                .equalTo(SearchWord);

        Log.i("검색","쿼리 경로 확인 : "+query.toString());

        FirebaseRecyclerOptions<PreView> options =
                new FirebaseRecyclerOptions.Builder<PreView>()
                        .setQuery(query, new SnapshotParser<PreView>() {
                            @NonNull
                            @Override
                            public PreView parseSnapshot(@NonNull DataSnapshot snapshot) {
                                Log.i("검색","스냅샷 동작");
                                return new PreView(
                                        snapshot.child("PosterKey").getValue().toString());
                            }
                        })
                        .build();

        SearchAdapter = new FirebaseRecyclerAdapter<PreView, UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, final int position, @NonNull PreView preView) {
                holder.setPosterKey(preView.getPosterKey());

                //클릭한 이미지의 포스트뷰어로 이동하기
                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(Search.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Search.this, PosterViewer.class);

                        //유저피드에서 해당 게시물 데이터를 받기 위한 쿼리스위치 신호
                        final int FLAG = 2;
                        intent.putExtra("FLAG",FLAG);
                        intent.putExtra("FOCUS", position);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    }
                });
            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.preview_search_item, parent, false);
                return new UserViewHolder(view);
            }
        };
        recyclerView.setAdapter(SearchAdapter);
    }
    //----------------------------------------검색용 어댑터------------------------------------------------
}
