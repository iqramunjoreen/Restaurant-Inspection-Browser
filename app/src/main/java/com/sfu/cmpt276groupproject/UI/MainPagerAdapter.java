package com.sfu.cmpt276groupproject.UI;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

/**
 * UI Fragment for switching map and list button
 */
public class MainPagerAdapter extends FragmentPagerAdapter {
    private int numTabs;
    private List<Fragment> fragments;

    public MainPagerAdapter(@NonNull FragmentManager fm, List<Fragment> fragments, int numTabs) {
        super(fm);
        this.fragments = fragments;
        this.numTabs = numTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return numTabs;
    }
}
