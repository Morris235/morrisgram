package com.example.morrisgram.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.morrisgram.Activity.BaseActivity.AddingPoster_BaseAct;
import com.example.morrisgram.CameraClass.GlideApp;
import com.example.morrisgram.R;
import com.google.firebase.storage.StorageReference;

public class Posting extends AddingPoster_BaseAct {

    private ImageView thumbIMG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 화면을 portrait(세로) 화면으로 고정하고 싶은 경우
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_posting);

        thumbIMG = (ImageView) findViewById(R.id.thumbIMG_posting);

        //게시물 이미지 UID받기
        Intent intent = getIntent();
        String PosterUID = intent.getStringExtra("PosterUID");

        //이미지 고르면 스토리지로 업로드가 되는데 게시물 작성 취소를 누르면 스토리지의 이미지도 삭제
        //Glide를 통한 이미지 바인딩
        StorageReference imageRef = mstorageRef.child(PosterPicList+"/"+"test"+"/"+PosterIMGname);
        GlideApp.with(this)
                .load(imageRef)
                .thumbnail()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .dontAnimate()
                .centerCrop()
                .placeholder(R.drawable.noimage)
                .into(thumbIMG);
    }

    //뒤로가기 버튼제어
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this,Home.class);
        startActivity(intent);
        finish();
    }
}
