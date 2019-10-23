package com.example.morrisgram.Activity;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.GpsDirectory;
import com.example.morrisgram.Activity.BaseActivity.AddingPoster_BaseAct;
import com.example.morrisgram.CameraClass.GlideApp;
import com.example.morrisgram.ClassesDataSet.Firebase.PostingDTO;
import com.example.morrisgram.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Posting extends AddingPoster_BaseAct {

    private ImageView thumbIMG;
    private ImageButton cancelB;

    private TextView postingB;
    private EditText body;

    //데이터베이스의 주소를 지정 필수
    private DatabaseReference mdataref = FirebaseDatabase.getInstance().getReference();
    //현재 접속중인 유저UID가져오기
    public FirebaseUser uid = FirebaseAuth.getInstance().getCurrentUser();
    private StorageReference mstorageRef = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 화면을 portrait(세로) 화면으로 고정하고 싶은 경우
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_posting);
        Log.i("메타데이타","포스팅 tempfile 값 확인 : "+tempFile);
        //현재 로그인된 유저UID
        final String userUID = uid.getUid();

        thumbIMG = (ImageView) findViewById(R.id.thumbIMG_posting);
        postingB = (TextView) findViewById(R.id.postingB_posting);
        body = (EditText) findViewById(R.id.inputtext_posting);
        cancelB = (ImageButton) findViewById(R.id.backB_posting);

        //이미지 고르면 스토리지로 업로드가 되는데 게시물 작성 취소를 누르면 스토리지의 이미지도 삭제
        //베이스 액티비티로부터 포스팅 버튼 누룰시 게시물 스토리지 이미지 랜덤 키값 받기
        Intent intent = getIntent();
        final String PosterKey_posting = intent.getStringExtra("PosterKey"); // = 게시물 데이터 키값

        final StorageReference imageRef = mstorageRef.child(PosterPicList+"/"+PosterKey_posting+"/"+PosterIMGname);
        GlideApp.with(this)
                .load(imageRef)
                .thumbnail()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .dontAnimate()
                .placeholder(R.drawable.noimage)
                .into(thumbIMG);

        //포스팅 완료 버튼
        postingB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //현재 접속중인 유저 데이터 받기
                mdataref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            //현재 로그인된 유저 정보와 일치하는 데이터를 가져오기.
                            String NickName = (String) dataSnapshot.child("UserList").child(userUID).child("UserInfo").child("NickName").getValue();
                            Log.i("파베","유저 닉네임 확인 : "+NickName);
                            //게시물 내용
                            String Body = body.getText().toString();

                            //----------------DTO 클래스를 통한 DB 업데이트 메소드-------------------
                        //현재시간을 msec으로 구한다.
                        long now = System.currentTimeMillis();
                        //현재시간을 date변수에 저장
                        Date date = new Date(now);
                        //시간을 나타낼 포맷을 정한다.
                        SimpleDateFormat SDF = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초");
                        SimpleDateFormat TIMESTEMP = new SimpleDateFormat("yyyyMMddHHmmss");

                        //스트링변수에 고유키값을 저장.
                        String DATE = SDF.format(date);
                        String TimeStemp = TIMESTEMP.format(date);

                        long likecount = 0;
                            UpFirebaseDatabase(true,userUID,NickName,Body,DATE,PosterKey_posting,TimeStemp,likecount);

                            //위치 메타데이터 업로드 메소드
                            getMetaData(PosterKey_posting);
                }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                //자연스럽게 화면전환 변경 필요
                Intent intent = new Intent(Posting.this,Home.class);
                startActivity(intent);
                finish();
            }

        });

        //포스팅 취소 - 업로드된 스토리지 사진 디렉토리 삭제
        cancelB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //미리보기용 업로드 삭제
                mstorageRef.child(PosterPicList).child(PosterKey_posting).child("PosterIMG").delete();

                Intent intent = new Intent(Posting.this,Home.class);
                startActivity(intent);
                finish();
            }
        });
    }

    //파이어 베이스 업데이트 메소드 - 게시물 포스팅 클래스에서 입력받아야 하는 모든 데이터들 // 인트로 업로드
    public void UpFirebaseDatabase(boolean add, String UserUID, String UserNicName, String Body,String Time, String PosterKey, String TimeStemp, long likeCount){
        //해쉬맵 생성
        Map<String,Object> childUpdates = new HashMap<>();
        Map<String,Object> PostValues = null;

        if(add){
            PostingDTO posting = new PostingDTO(UserUID,UserNicName,Body,Time,likeCount,PosterKey,TimeStemp);
            PostValues = posting.toMap();
        }

        //새로운 차일드 목록 만들기 - 차일드 그룹 - PosterList 게시물 전체 데이터
        childUpdates.put(PosterKey ,PostValues);
        mdataref.child("PosterList").updateChildren(childUpdates);

        //유저의 게시물 따로 업데이트
        mdataref.child("UserList").child(userUID).child("UserPosterList").updateChildren(childUpdates);
    }

    //위치 메타데이터 추출 메소드
    public void getMetaData(String PosterKey){
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(tempFile);
            Log.i("메타데이터", "tempFile 값 확인 : " + tempFile);

            GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);
            Log.i("메타데이터", "geoLocation : " + gpsDirectory);

            //GPS경도값 가져오기
            GeoLocation geoLocation = gpsDirectory.getGeoLocation();
            Log.i("메타데이터", "geoLocation : " + geoLocation);

            //GPS Directory (tags)
            String ImageMetaDataGPS = gpsDirectory.toString();
            Log.i("메타데이터", "ImageMetaDataGPS : " + ImageMetaDataGPS);

            //메타데이터 업데이트 메소드
            MetaDataUpload(PosterKey,geoLocation);

        } catch (ImageProcessingException | NullPointerException | IOException e) {
            e.printStackTrace();
            Log.i("메타데이터", "에러발생! : " + e);
            Toast.makeText(getApplicationContext(),"위치 메타데이터 없음",Toast.LENGTH_LONG).show();
        }

    }

    //위치 메타데이터 업로드메소드
    public String MetaDataUpload (String PosterKey, GeoLocation geoLocation){

        //uri를 파일로 변환
        //File file = new File(uri.getPath());
        // *Exif는 JPEG파일에서만 제공된다*

            //이미지 메타데이터 업로드 코드
            final String Location = geoLocation.toString();
            StorageMetadata Smetadata = new StorageMetadata.Builder()
                    .setContentType("image/jpg")
                    .setCustomMetadata("location",Location)
                    .build();

            //스토리지에 메타데이터 업로드 리스너
            final StorageReference imageRef = mstorageRef.child(PosterPicList+"/"+PosterKey+"/"+PosterIMGname);
            imageRef.updateMetadata(Smetadata).addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                @Override
                public void onSuccess(StorageMetadata storageMetadata) {
                    // Updated metadata is in storageMetadata
                    Log.i("메타데이타","메타 데이타 업로드 성공");
                    Toast.makeText(getApplicationContext(),"업로드 성공!",Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Uh-oh, an error occurred!
                    Toast.makeText(getApplicationContext(),"메타데이터 없음",Toast.LENGTH_LONG).show();
                }
            });
            return Location;
    }

    //뒤로가기 버튼제어 - 홈으로 보내기 - 업로드된 스토리지 사진 디렉토리 삭제
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this,Home.class);

        //미리보기용 업로드 삭제
        Intent intent2 = getIntent();
        String MPosterKey = intent2.getStringExtra("PosterKey");
        mstorageRef.child(PosterPicList).child(MPosterKey).child("PosterIMG").delete();

        //홈화면 이동
        startActivity(intent);
        finish();
    }
}
