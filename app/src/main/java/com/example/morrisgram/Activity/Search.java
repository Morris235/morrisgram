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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.morrisgram.Activity.BaseActivity.AddingPoster_BaseAct;
import com.example.morrisgram.CameraClass.GlideApp;
import com.example.morrisgram.DTOclass.PostingDTO;
import com.example.morrisgram.DTOclass.PreView;
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
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class Search extends AddingPoster_BaseAct implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    //뒤로가기 제어
    private final long FINISH_INTERVAL_TIME = 2000;
    private long   backPressedTime = 0;

    //파이어베이스 리사이클러뷰
    //데이터베이스의 주소 지정
    private DatabaseReference mdataref = FirebaseDatabase.getInstance().getReference();
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private FirebaseRecyclerAdapter adapter;
    private FirebaseRecyclerAdapter SearchAdapter;

    //검색된 게시물의 UID값 받기
    private List<String> SearchPosterKeyList = new ArrayList<>();

    //전체 포스터키 수집용 리스트
    public List<String> PosterKeyList = new ArrayList<>();
    public List<PostingDTO> postingDTOS = new ArrayList<>();

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
                    String getValue = snapshot.child("Body").getValue().toString();
                    Log.i("포스터키","전체 유저 게시물 키 : "+GetKey);
                    Log.i("포지션","전체 유저 게시물의 특정 값 : "+getValue);
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


        //데이터 검색바 바인드
        searchbar = (EditText) findViewById(R.id.searchbar_search);

        searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                FirebaseUserSearch(s.toString());
                SearchAdapter.startListening();

                //검색중이 아닐때 전체게시물 보여주기
                if (s.length() == 0){
                    SearchAdapter.stopListening();

                    fetch();
                    adapter.startListening();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

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
                Intent intent = new Intent(Search.this, Alarm.class);
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
//    @Override
//    public void onBackPressed() {
//
//    }

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
                .child("PosterList");

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
                //각 게시물에 포지션값 업데이트 - 검색 액티비티에서 게시물을 클릭해서 인텐트로 업로드한 포지션을 다운받아 포스터뷰어로 이동할 때 포지션값을 받아
                //스크롤을 움직여 해당 게시물에 포커스를 주기위함
                mdataref.child("PosterList").child(PosterKeyList.get(position)).child("Position").setValue(position);


                holder.setPosterKey(preView.getPosterKey());
                //클릭한 이미지의 포스트뷰어로 이동하기
                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
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

        //게시물 키값 받는 변수
        public String UserPosterKey;

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

        public String getPosterKey(String Key){
            return Key;
        }
    }

    //검색 어댑터
    private void FirebaseUserSearch(final String SearchWord){
        Log.i("검색","게시물 검색 메소드 동작");
        //BaseQuery
        final Query query = FirebaseDatabase.getInstance()
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

                //검색한 게시물의 키값 얻기
               final String PosterKey = holder.getPosterKey(preView.getPosterKey());



                //클릭한 이미지의 포스트뷰어로 이동하기
                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mdataref.child("PosterList").child(PosterKey).child("Position").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                try {
                                    //DB에서 포지션값 얻기
                                    String PosterPosition = String.valueOf(dataSnapshot.getValue());

                                    Log.i("포지션","DB에서 받아온 포지션값 확인 : "+PosterPosition);
                                    //유저피드에서 해당 게시물 데이터를 받기 위한 쿼리스위치 신호
                                    final int FLAG = 2;
                                    //포스터뷰어로 DB에서 받아온 포지션값 보내기
                                    final Intent intent = new Intent(Search.this, PosterViewer.class);
                                    intent.putExtra("FLAG",FLAG);
                                    intent.putExtra("FOCUS", Integer.valueOf(PosterPosition));
                                    startActivity(intent);
                                    overridePendingTransition(0, 0);
                                }catch (NumberFormatException | NullPointerException e){
                                    e.getStackTrace();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

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
