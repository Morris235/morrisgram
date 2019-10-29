package com.example.morrisgram.Adapter.ViewPagerAdapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.example.morrisgram.Activity.FollowFragment.FollowerPage;
import com.example.morrisgram.Activity.FollowFragment.FollowingPage;


//ViewPager의 경로 및 속성을 설정해주는 PagerAdapter.class
public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    // Context를 전달받아 mContext에 저장하는 생성자 추가.
    public ViewPagerAdapter(FragmentManager fm, int NumOfTabs){
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0 :
                FollowerPage tab1 = new FollowerPage();
                return tab1;
            case 1 :
                FollowingPage tab2 = new FollowingPage();
                return tab2;

                default:
                    return null;
        }
    }

    @Override
    public int getCount() {
        //전체 페이지수
        return mNumOfTabs;
    }

//    @Override
//    public Object instantiateItem(@NonNull ViewGroup container, int position) {
//        return super.instantiateItem(container, position);
//    }

    //화면에 표시할 페이지뷰를 만드는 작업
    //LayoutInflater를 통해서 페이지뷰를 만들기
    //new 키워드를 사용하여 뷰 위젯등을 직접 생성하거나, 프래그먼트를 사용하여 페이지 화면을 만드는등 다양한 방법으로 페이지뷰를 구성할 수 있다.


//    @Override
//    public void destroyItem(ViewGroup container, int position, Object object) {
//        // 뷰페이저에서 삭제.
//        container.removeView((View) object);
//    }



//    //뷰페이저 내부적으로 관리되는 키 객체(key object)와 페이지뷰가 연관되는지 여부를 확인하는 역할을 수행
//    @Override
//    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
//        return (view == (View)object);
//    }

    //탭메뉴 텍스트 
//    @Override
//    public CharSequence getPageTitle(int position){
//        switch (position){
//            case 0:
//                return "팔로워 0명";
//            case 1:
//                return "팔로잉 0명";
//            default:
//                return null;
//        }
//    }
}
