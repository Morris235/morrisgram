package com.example.morrisgram.Adapter.ViewPagerAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.morrisgram.R;

public class ViewPagerAdapter extends PagerAdapter {
    // LayoutInflater 서비스 사용을 위한 Context 참조 저장.
    private Context mContext = null ;

    // Context를 전달받아 mContext에 저장하는 생성자 추가.
    public ViewPagerAdapter(Context context){
        mContext = context;
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position){
        View view = null;
        if (mContext != null) {
            // LayoutInflater를 통해 "/res/layout/page.xml"을 뷰로 생성.
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.activity_followers, container, false);

            TextView textView = (TextView) view.findViewById(R.id.viewtest) ;
            textView.setText("TEST " + position);
        }

        // 뷰페이저에 추가.
        container.addView(view) ;
        return view ;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // 뷰페이저에서 삭제.
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        //전체 페이지수 고정
        return 2;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == (View)object);
    }
    @Override
    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "팔로워 0명";
            case 1:
                return "팔로잉 0명";
            default:
                return null;
        }
    }
}
