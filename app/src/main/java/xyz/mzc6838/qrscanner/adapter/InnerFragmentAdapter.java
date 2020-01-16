package xyz.mzc6838.qrscanner.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class InnerFragmentAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;
    private String[] titles;

    public InnerFragmentAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    public void addFragments(String[] titles, List<Fragment> fragments){
        this.titles = titles;
        this.fragments = fragments;
    }
}
