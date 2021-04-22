/*
Layout design inspired by:
Coding With Tea, Modern Login and Sign up Animation using Fragments and Viewpager - Android Studio Tutorials - Part 5. YouTube, 2020. [Video]. Available:
https://www.youtube.com/watch?v=ayKMfVt2Sg4&list=PL5jb9EteFAOAO16th6HQa76-pdyT3y4L5&index=4. [Accessed: 03-Apr-2021].
*/
package com.example.soundsensev1;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class LoginAdapter extends FragmentPagerAdapter {

    private Context context;
    int totalTabs;

    public LoginAdapter(FragmentManager fm, Context context, int totalTabs) {
        super(fm);
        this.context = context;
        this.totalTabs = totalTabs;
    }

    @Override
    public int getCount() {
        return totalTabs;
    }

    public Fragment getItem (int position){
        switch (position){
            case 0:
                LoginTabFragment loginTabFragment = new LoginTabFragment();
                return loginTabFragment;
            case 1:
                SignUpTabFragment signUpTabFragment = new SignUpTabFragment();
                return signUpTabFragment;
            default:
                return null;
        }
    }

}
