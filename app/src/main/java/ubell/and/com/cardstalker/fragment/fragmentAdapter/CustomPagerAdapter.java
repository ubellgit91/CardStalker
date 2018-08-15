package ubell.and.com.cardstalker.fragment.fragmentAdapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ubell.and.com.cardstalker.fragment.BankFragment;
import ubell.and.com.cardstalker.fragment.UsedListFragment;

public class CustomPagerAdapter extends FragmentPagerAdapter {

    public CustomPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0 :
                return UsedListFragment.newInstance(); //해당 클래스의 fragment를 import한 것을 android.support.v4.app.Fragment로 수정해줘야함!@!
            case 1 :
                return BankFragment.newInstance();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){
            case 0 : return "List";
            case 1 : return "bank";
            default: return null;
        }
    }
}
