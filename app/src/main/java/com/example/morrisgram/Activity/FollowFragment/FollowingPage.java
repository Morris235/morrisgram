package com.example.morrisgram.Activity.FollowFragment;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.morrisgram.CameraClass.GlideApp;
import com.example.morrisgram.DTOclass.Firebase.FollowingDTO;
import com.example.morrisgram.DTOclass.Firebase.PostingDTO;
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
import java.util.List;

public class FollowingPage extends Fragment {

    //현재 접속중인 유저UID가져오기
    private FirebaseUser uid = FirebaseAuth.getInstance().getCurrentUser();
    private String userUID = uid.getUid();

    //파이어베이스 리사이클러뷰 맴버변수 선언
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;

    //전체 포스터키 수집용 리스트
    private List<String> PosterKeyList = new ArrayList<>();
    private List<PostingDTO> postingDTOS = new ArrayList<>();

    //파이어베이스 스토리지 변수
    private StorageReference mstorageRef = FirebaseStorage.getInstance().getReference();
    //데이터베이스 변수
    private DatabaseReference mdataref = FirebaseDatabase.getInstance().getReference();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_following_page, container, false);

//        //레이아웃 매니저 세팅
//        linearLayoutManager = new LinearLayoutManager();
//        linearLayoutManager.setReverseLayout(true);
//        linearLayoutManager.setStackFromEnd(true);
//
//        //리사이클러뷰
//        recyclerView.findViewById(R.id.recyclerView_followers);
//        recyclerView.setLayoutManager(linearLayoutManager);
//        recyclerView.setHasFixedSize(true);

//        fetch();

        //------------------------------------------------게시물 키값 수집 데이터 스냅샷--------------------------------------------------
//        //전체 게시물 키 수집
//        mdataref.child("PosterList").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                //포스터키 수집용 리스트
////                private List<String> PosterKeyList = new ArrayList<>();
////                private List<PostingDTO> postingDTOS = new ArrayList<>();
//
//                //포스터키가 리스트에 쌓이지 않도록 클리어하기
//                postingDTOS.clear();
//                PosterKeyList.clear();
//                //유저리스트에 있는 모든 데이터를 읽어온다. 그중에서 파베예외 발생 : Failed to convert a value of type java.util.HashMap to long
//                //C#의 foreach문과 유사한 배열에 이용되는 for문 ->for(변수:배열) = 배열에 있는 값들을 하나씩 순서대로 변수에 대입시킨다. -배열의 자료형과 for문의 변수 자료형은 같아야 한다.
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    //파베 스냅샷으로 받아올때 long이나 int형태로는 못받아 오겠다. 왜냐하면 모델클래스가 해쉬맵이여서? =데이터를 모델에 맞게 받는 코드
//                    PostingDTO postingDTO = snapshot.getValue(PostingDTO.class);
//                    //게시물 키값 받기
//                    String GetKey = snapshot.getKey();
//                    Log.i("포스터키","전체 유저 게시물 키 : "+GetKey);
//
//                    //클래스 주소값? 리스트
//                    postingDTOS.add(postingDTO);
//                    //키값들을 리스트형태로 저장
//                    PosterKeyList.add(GetKey);
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//------------------------------------------------게시물 키값 수집 데이터 스냅샷--------------------------------------------------
    }//------------------------크리에이트-------------------------



    public void onStart() {
        super.onStart();
//        adapter.startListening();
    }

    public void onStop() {
        super.onStop();
//        adapter.stopListening();
    }


//    //------------------------뷰홀더------------------------------
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        //아이템 레이아웃 뷰 변수 선언
//        public ConstraintLayout root;
//        public TextView UserNickName;
//        public ImageView UserprofileIMG;
//        public Button FollowB;
//        public Button FollowingB;
//
//
//        public ViewHolder(@NonNull View itemView) {
//            //선언한 변수와 아이템 레이아웃의 뷰를 바인드
//            super(itemView);
//            root = itemView.findViewById(R.id.preview_userfeed_root);
//            UserNickName = itemView.findViewById(R.id.idtv_followitem);
//            UserprofileIMG = itemView.findViewById(R.id.profileIMG_followitem);
//
//            FollowB = itemView.findViewById(R.id.followB_followitem);
//            FollowingB = itemView.findViewById(R.id.followingB_followitem);
//        }
//
//
//        //팔로잉 유저들의 닉네임 받기
//        public void setUserNickName(final String uid){
//
//            mdataref.child("UserList").addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                   String nickname = dataSnapshot.child(uid).child("UserInfo").child("NickName").getValue().toString();
//                   UserNickName.setText(nickname);
//                }
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//        }
//
//        //스토리지에서 팔로잉 유저의 프로필 이미지 받기
//        public void setUserprofileIMG(String uid){
//            Log.i("파베", "setPic 메소드 작동 확인");
//            StorageReference imageRef = mstorageRef.child(uid).child("ProfileIMG").child("ProfileIMG");
//            GlideApp.with(FollowingPage.this)
//                    .load(imageRef)
//                    .skipMemoryCache(false)
//                    .thumbnail()
//                    .centerCrop()
//                    .fitCenter()
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .dontAnimate()
//                    .placeholder(R.drawable.noimage)
//                    .into(UserprofileIMG);
//        }
//
//    }//------------------------뷰홀더------------------------------
//    //----------------------------파이어베이스 어댑터---------------------------------------
//    private void fetch() {
//        //BaseQuery - 팔로잉리스트 쿼리
//        Query query = FirebaseDatabase.getInstance()
//                //BaseQuery
//                .getReference()
//                .child("UserList")
//                .child(userUID)
//                .child("FollowingList");
//
//        //orderByChild()	지정된 하위 키의 값에 따라 결과를 정렬합니다.
//        //orderByKey()	    하위 키에 따라 결과를 정렬합니다.
//        //orderByValue()	하위 값에 따라 결과를 정렬합니다.
//
//        Log.i("팔로우", "query 경로 확인 : "+query.toString());
//
//        //query를 사용해서 DB의 모든 해당 정보를 받아서 가져오는 스냅샷 - 스트링형식으로 받아와야함 - 팔로잉 유저의 UID만 받아와서 모든걸 해결하자!
//        FirebaseRecyclerOptions<FollowingDTO> options =
//                new FirebaseRecyclerOptions.Builder<FollowingDTO>()
//                        .setQuery(query, new SnapshotParser<FollowingDTO>() {
//                            @NonNull
//                            @Override
//                            public FollowingDTO parseSnapshot(@NonNull DataSnapshot snapshot) {
//                                return new FollowingDTO(
//                                        snapshot.child("UID").getValue().toString());  //게시물 이미지
//                            }
//                        })
//                        .build();
//
//
//
//        //어댑터
//        adapter = new FirebaseRecyclerAdapter<FollowingDTO, FollowingPage.ViewHolder>(options) {
//            //리사이클러뷰 아이템 생성
//            @Override
//            public FollowingPage.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//                View view = LayoutInflater.from(parent.getContext())
//                        .inflate(R.layout.follow_item, parent, false);
//                return new FollowingPage.ViewHolder(view);
//            }
//
//            @Override                                                                                           //DB 데이터 틀 = DTO 클래스
//            protected void onBindViewHolder(@NonNull FollowingPage.ViewHolder holder, final int position, @NonNull FollowingDTO following_set) {
//                //팔로잉 유저닉네임
//                holder.setUserNickName(following_set.getUID());
//                //팔로잉 유저프로필 이미지
//                holder.setUserprofileIMG(following_set.getUID());
//
//                //팔로잉, 팔로우 버튼
////                holder.vetB.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View v) {
////                        CharSequence info[] = new CharSequence[] {"삭제","수정" };
////                        AlertDialog.Builder builder = new AlertDialog.Builder(FollowingPage.this);
//////                        builder.setTitle()
////                        builder.setItems(info, new DialogInterface.OnClickListener() {
////                            @Override
////                            public void onClick(DialogInterface dialog, int which) {
////                                if(which == 0){
//////                                    mdataref.child(userUID).child("UserPosterList").child();
////                                    Toast.makeText(FollowingPage.this, "옵션 클릭확인", Toast.LENGTH_SHORT).show();
////                                }
////                                dialog.dismiss();
////                            }
////                        });
////                        builder.show();
////                    }
////                });
//
//                //피드이동
////                holder.root.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View view) {
////                        Toast.makeText(FollowingPage.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
////                        //해당 포지션으로 포커스 주기 - 포스터뷰어로 이동
////                    }
////                });
//
//            }
//        };
//        recyclerView.setAdapter(adapter);
//    }
}
