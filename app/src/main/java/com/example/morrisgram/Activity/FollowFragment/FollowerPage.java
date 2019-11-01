package com.example.morrisgram.Activity.FollowFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.morrisgram.Activity.UserProfile;
import com.example.morrisgram.CameraClass.GlideApp;
import com.example.morrisgram.DTOclass.Firebase.FollowerDTO;
import com.example.morrisgram.DTOclass.Firebase.FollowingDTO;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FollowerPage extends Fragment {
    //현재 접속중인 유저UID가져오기
    private FirebaseUser uid = FirebaseAuth.getInstance().getCurrentUser();
    private String userUID = uid.getUid();

    //파이어베이스 리사이클러뷰 맴버변수 선언
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;

    //전체 팔로잉 유저 UID 수집용 리스트
    private List<String> FollowerUIDList = new ArrayList<>();
    private List<FollowerDTO> followerDTOS = new ArrayList<>();

    //파이어베이스 스토리지 변수
    private StorageReference mstorageRef = FirebaseStorage.getInstance().getReference();
    //데이터베이스 변수
    private DatabaseReference mdataref = FirebaseDatabase.getInstance().getReference();

    private String UserUID;
    //내 팔로우 리스트와 유저의 팔로우 리스트 분기용
    private int FLAG;

    //널값
    public FollowerPage(int FLAG ,String UserUID){
        this.FLAG = FLAG;
        this.UserUID = UserUID;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_following_page, container, false);

        //레이아웃 매니저 세팅
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        //리사이클러뷰 바인드
        recyclerView = rootView.findViewById(R.id.recyclerView_following);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        Log.i("뷰페이저","팔로워 유저UID 확인 : "+UserUID);
        Log.i("뷰페이저","팔로워 플래그 확인 : "+FLAG);

        //------------------------------------------------팔로잉 유저 UID 값 수집 데이터 스냅샷--------------------------------------------------리얼타임이 아닌 한번만 읽어보게 하기
        mdataref.child("UserList").child(UserUIDSwitch(FLAG,UserUID)).child("FollowingList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //포스터키 수집용 리스트
//                private List<String> PosterKeyList = new ArrayList<>();
//                private List<PostingDTO> postingDTOS = new ArrayList<>();

                //포스터키가 리스트에 쌓이지 않도록 클리어하기
                followerDTOS.clear();
                FollowerUIDList.clear();
                //C#의 foreach문과 유사한 배열에 이용되는 for문 ->for(변수:배열) = 배열에 있는 값들을 하나씩 순서대로 변수에 대입시킨다. -배열의 자료형과 for문의 변수 자료형은 같아야 한다.
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    //파베 스냅샷으로 받아올때 long이나 int형태로는 못받아 오겠다. 왜냐하면 모델클래스가 해쉬맵이여서? = 데이터를 모델에 맞게 받는 코드
                    FollowerDTO  followerDTO = snapshot.getValue(FollowerDTO.class);
                    //팔로잉 유저 UID 받기
                    String GetKey = snapshot.getKey();
                    Log.i("팔로잉","팔로잉 유저 UID 리스트: "+GetKey);

                    //클래스 주소값? 리스트
                    followerDTOS.add(followerDTO);
                    //키값들을 리스트형태로 저장
                    FollowerUIDList.add(GetKey);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //------------------------------------------------팔로잉 유저 UID 값 수집 데이터 스냅샷--------------------------------------------------

        fetch();

        return rootView;
    }

    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }





    //------------------------뷰홀더 클래스------------------------------
    public class ViewHolder extends RecyclerView.ViewHolder {
        //아이템 레이아웃 뷰 변수 선언
        public ConstraintLayout root;
        public TextView UserNickName;
        public ImageView UserprofileIMG;
        public Button FollowB;
        public Button FollowingB;

        //팔로잉 유저의 UID받는 변수
        public String UID;

        public ViewHolder(@NonNull View itemView) {
            //선언한 변수와 아이템 레이아웃의 뷰를 바인드
            super(itemView);
            root = itemView.findViewById(R.id.followitem_root);
            UserNickName = itemView.findViewById(R.id.idtv_followitem);
            UserprofileIMG = itemView.findViewById(R.id.profileIMG_followitem);

            FollowB = itemView.findViewById(R.id.followB_followitem);
            FollowingB = itemView.findViewById(R.id.followingB_followitem);
        }


        //팔로잉 유저들의 닉네임 받기
        public void setUserNickName(final String uid){

            mdataref.child("UserList").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        String nickname = dataSnapshot.child(uid).child("UserInfo").child("NickName").getValue().toString();
                        UserNickName.setText(nickname);
                    }catch (NullPointerException e){
                        e.getStackTrace();
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        //스토리지에서 팔로잉 유저의 프로필 이미지 받기
        public void setUserprofileIMG(String uid){
            Log.i("파베", "setPic 메소드 작동 확인");
            StorageReference imageRef = mstorageRef.child(uid).child("ProfileIMG").child("ProfileIMG");
            GlideApp.with(FollowerPage.this)
                    .load(imageRef)
                    .skipMemoryCache(false)
                    .thumbnail()
                    .centerCrop()
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate()
                    .placeholder(R.drawable.noimage)
                    .into(UserprofileIMG);
        }

        public String getFollowingUserUID(String uid){
            UID = uid;
            return UID;
        }
    }//------------------------뷰홀더 클래스------------------------------





    //----------------------------파이어베이스 어댑터---------------------------------------
    private void fetch() {
//        //BaseQuery - 팔로잉리스트 쿼리
//        Query query = FirebaseDatabase.getInstance()
//                //BaseQuery
//                .getReference()
//                .child("UserList")
//                .child(userUID)
//                .child("FollowerList");

        //orderByChild()	지정된 하위 키의 값에 따라 결과를 정렬합니다.
        //orderByKey()	    하위 키에 따라 결과를 정렬합니다.
        //orderByValue()	하위 값에 따라 결과를 정렬합니다.

        //query를 사용해서 DB의 모든 해당 정보를 받아서 가져오는 스냅샷 - 스트링형식으로 받아와야함 - 팔로잉 유저의 UID만 받아와서 모든걸 해결하자!
        FirebaseRecyclerOptions<FollowerDTO> options =
                new FirebaseRecyclerOptions.Builder<FollowerDTO>()
                        .setQuery(QuerySwitch(FLAG,UserUID), new SnapshotParser<FollowerDTO>() {
                            @NonNull
                            @Override
                            public FollowerDTO parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new FollowerDTO(
                                        snapshot.child("UID").getValue().toString());  //게시물 이미지
                            }
                        })
                        .build();


        //어댑터
        adapter = new FirebaseRecyclerAdapter<FollowerDTO, FollowerPage.ViewHolder>(options) {
            //리사이클러뷰 아이템 생성
            @Override
            public FollowerPage.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.follow_item, parent, false);
                return new FollowerPage.ViewHolder(view);
            }


            @Override                                                                                           //DB 데이터 틀 = DTO 클래스
            protected void onBindViewHolder(@NonNull final FollowerPage.ViewHolder holder, final int position, @NonNull final FollowerDTO follower_set) {
                //팔로잉 유저닉네임
                holder.setUserNickName(follower_set.getUID());
                //팔로잉 유저프로필 이미지
                holder.setUserprofileIMG(follower_set.getUID());


                //아이템 자체에 포지션이 디폴트로 깔려있다.
                //언팔로우 버튼 - 언팔로우할때 바로 팔로잉 유저가 삭제되면 안된다. - 클릭 버튼 = 팔로우 DB 업데이트
                holder.FollowingB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //해당 포지션의 팔로잉 유저UID 가져오기 - 에러 예상됨
                        String UserUIDList = holder.getFollowingUserUID(follower_set.getUID());
                        Log.i("팔로워", "클릭확인 : "+UserUIDList);

                        //언팔로우 하기
                        FirebaseDatabase(false,UserUIDList);

                        //팔로잉 상태
                        holder.FollowB.setVisibility(View.VISIBLE);
                        holder.FollowingB.setVisibility(View.INVISIBLE);
                    }
                });

                //팔로우 버튼 - 언팔로우할때 바로 팔로잉 유저가 삭제되면 안된다. - 클릭 버튼 = 팔로우 DB 업데이트
                holder.FollowB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //해당 포지션의 팔로잉 유저UID 가져오기 - 에러 예상됨
                        String UserUIDList = holder.getFollowingUserUID(follower_set.getUID());
                        Log.i("팔로워", "클릭확인 : "+UserUIDList);

                        //팔로잉 하기
                        FirebaseDatabase(true,UserUIDList);

                        //언팔로잉 상태
                        holder.FollowB.setVisibility(View.INVISIBLE);
                        holder.FollowingB.setVisibility(View.VISIBLE);
                    }
                });


                //버튼 상태 예외처리 - 각 <팔로잉 계정>의 UID 존재 유무 비교 (해당포지션의 UID) *클릭이 아님* 상태값을 받아야 한다. ->전체 팔로잉 유저의 UID를 받고 해당 포지션에 일치하는 UID가 없으면 버튼처리
                mdataref.child("UserList").child(UserUIDSwitch(FLAG,UserUID)).child("FollowingList").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //                //팔로워 리스트에서 나의 계정UID가 있다면 팔로잉버튼 처리
                        // 경로 : root/ UserList/ userUID/ "FollowerList/ FollowerUserUID/  UID : "UID"
                        try {
                            //팔로잉 값이 없는경우 예외처리 필요 IndexOutOfBoundsException
                            //홀더에서 받는 데이터
                            String UserUID = holder.getFollowingUserUID(follower_set.getUID()); //DB전체 팔로잉 유저의 UID
//                            String UserUID = FollowingUIDList.get(position); //싱글벨류 리스트의 팔로잉 유저의 UID
                            Log.i("팔로워", "리스트의 팔로잉 유저UID : "+UserUID);

                            //데이터 스냅샷으로 가져오는 데이터
                            String FollowingUID = dataSnapshot.child(UserUID).child("UID").getValue().toString(); //DB전체 벨류 이벤트 같은 DB에서 가져온 값을 비교한다? 의미 없어보임
                            Log.i("팔로잉", "FollowingUID 데이터 스냅샷 : "+FollowingUID);

                            //버튼예외처리 팔로잉 리스트 경로에
                            if(UserUID.equals(FollowingUID)){

                                //팔로잉 상태 - 팔로잉 리스트에 해당 유저가 있다면
                                holder.FollowB.setVisibility(View.INVISIBLE); //팔로우 - 0
                                holder.FollowingB.setVisibility(View.VISIBLE); //팔로잉 - 1
                            }else if (UserUID.isEmpty()){

                                //언팔로잉 상태 - 팔로잉 리스트에 해당 유저가 없다면
                                holder.FollowB.setVisibility(View.VISIBLE); //팔로우 - 1
                                holder.FollowingB.setVisibility(View.INVISIBLE); //팔로잉 -0
                            }


                        }catch (NullPointerException | IndexOutOfBoundsException e){
                            e.getStackTrace();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                //팔로워 유저의 피드로 이동
                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            String UserUID = FollowerUIDList.get(position);
                            Intent intent = new Intent(getActivity(), UserProfile.class);
                            intent.putExtra("PosterUserUID",UserUID);
                            startActivity(intent);
                        }catch (IndexOutOfBoundsException e){
                            e.getStackTrace();
                        }

                    }
                });

            }
        };
        recyclerView.setAdapter(adapter);
    }    //----------------------------파이어베이스 어댑터---------------------------------------


    //팔로우 업데이트 메소드
    public void FirebaseDatabase(boolean add, String UID){
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously

        //팔로워 리스트 맵 - 업데이트
        Map<String,Object> FollowerUserUID = new HashMap<>();
        //팔로잉 리스트 맵 - 업데이트
        Map<String,Object> FollowingUserUID = new HashMap<>();

        Map<String,Object> FollowerValues = null;
        Map<String,Object> FollowingValues = null;

        //객체 2개 선언
        FollowingDTO followingDTO = new FollowingDTO(UID);
        FollowerDTO followerDTO = new FollowerDTO(userUID);

        //팔로잉 팔로워 맵 두개 사용
        FollowerValues = followerDTO.toMap();
        FollowingValues = followingDTO.toMap();



        //팔로우 버튼
        if (add){
            //<---상대방 팔로워 리스트---> DB에 로그인한 유저의 UID 추가
            // 경로 : 루트/ UserList/ userUID/ FollowerList/ FollowerUserUID/  UID : "UID"
            FollowerUserUID.put(userUID,FollowerValues);
            mdataref.child("UserList").child(UID).child("FollowerList").updateChildren(FollowerUserUID);

            //<---내 팔로잉 리스트---> DB에 상대방 유저의 UID 추가
            // 경로 : 루트/ UserList/ userUID/ FollowingList/ FollowingUserUID/  UID : "UID"
            FollowingUserUID.put(UID,FollowingValues);
            mdataref.child("UserList").child(userUID).child("FollowingList").updateChildren(FollowingUserUID);


            //언팔로우 버튼
        }else{
            //<---상대방 팔로워 리스트---> DB에 로그인한 유저의 UID 삭제
            mdataref.child("UserList").child(UID).child("FollowerList").child(userUID).child("UID").setValue(null);

            //<---내 팔로잉 리스트---> DB에 상대방 유저의 UID 삭제
            mdataref.child("UserList").child(userUID).child("FollowingList").child(UID).child("UID").setValue(null);
        }


    }

    //포스터뷰어 클래스에 가져올 데이터 쿼리 스위치
    public Query QuerySwitch(int FLAG, String PosterUserUID){
        switch (FLAG){
            case -1:
                Toast.makeText(getActivity(),"에러발생",Toast.LENGTH_SHORT).show();
                break;

            //내 프로필 팔로우 DB 쿼리 - 유저 게시물 DB
            case 0 : Query fromMyinfo = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("UserList")
                    .child(userUID)
                    .child("FollowerList");
                Log.i("쿼리 스위치","0번 내 게시물 쿼리 : 유저 게시물 DB");
                return fromMyinfo;

            //유저피드 팔로우 DB 쿼리 - 유저 게시물 DB
            case 1 : Query fromUserfeed = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("UserList")
                    .child(PosterUserUID)
                    .child("FollowerList");
                Log.i("쿼리 스위치","1번 유저피드 게시물 쿼리 : 유저 게시물 DB");
                return fromUserfeed;
        }
        return null;
    }

    public String UserUIDSwitch(int FLAG, String UserProfileUID){
        switch (FLAG){
            case -1 :
                Toast.makeText(getActivity(),"에러발생",Toast.LENGTH_SHORT).show();
                break;

            //나의 UID 반환
            case 0 : return userUID;
            //유저들의 UID 봔환
            case 1 : return UserProfileUID;
        }
        return null;
    }

}
