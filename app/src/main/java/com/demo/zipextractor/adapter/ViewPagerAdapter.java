package com.demo.zipextractor.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.demo.zipextractor.utils.MainConstant;


public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public CharSequence getPageTitle(int i) {
        if (i == 0) {
            return "All";
        }
        if (i == 1) {
            return "Word";
        }
        if (i == 2) {
            return "Excel";
        }
        if (i == 3) {
            return MainConstant.FILE_TYPE_PDF_CAPS;
        }
        if (i == 4) {
            return "PPT";
        }
        if (i == 5) {
            return "TXT";
        }
        return null;
    }

    public ViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int i) {
        if (i == 0) {

        }
        if (i == 1) {

        }

        if (i == 2) {

        }
        if (i == 3) {

        }
        if (i == 4) {

        }
        if (i == 5) {

        }
        return null;
    }
}
