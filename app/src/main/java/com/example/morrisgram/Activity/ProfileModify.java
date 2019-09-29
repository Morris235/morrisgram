package com.example.morrisgram.Activity;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.morrisgram.R;

public class ProfileModify extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_modify);

        //취소버튼
        ImageButton cancelB;
        cancelB=(ImageButton)findViewById(R.id.cancelB);
        cancelB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @Override
    public void onPause(){
        super.onPause();
        overridePendingTransition(0,0);
    }
}
