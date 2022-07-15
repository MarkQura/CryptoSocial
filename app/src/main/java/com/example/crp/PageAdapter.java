package com.example.crp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PageAdapter extends FragmentPagerAdapter {

    private int tabNumber;

    public PageAdapter(FragmentManager fm, int numberTabs){
        super(fm);
        this.tabNumber = numberTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new tab1();

            case 1:
                return new tab2();

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabNumber;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
