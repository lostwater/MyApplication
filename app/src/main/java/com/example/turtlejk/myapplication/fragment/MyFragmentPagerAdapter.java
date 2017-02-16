package com.example.turtlejk.myapplication.fragment;

import android.support.v4.app.*;
import android.view.ViewGroup;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private final int PAGER_COUNT = 2;
    private StepFragment stepFragment = null;
    private TreeFragment treeFragment = null;

    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        stepFragment = new StepFragment();
        treeFragment = new TreeFragment();
    }

    @Override
    public int getCount() {
        return PAGER_COUNT;
    }

    @Override
    public Object instantiateItem(ViewGroup vg, int position) {
        return super.instantiateItem(vg, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        System.out.println("position Destory" + position);
        super.destroyItem(container, position, object);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case FragmentsActivity.PAGE_STEP:
                fragment = stepFragment;
                break;
            case FragmentsActivity.PAGE_TREE:
                fragment = treeFragment;
                break;
        }
        return fragment;
    }

}
