/*
 * Copyright (C) 2017. Konstantins Lihacovs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lihacovs.android.beepass.introduction;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.lihacovs.android.beepass.R;
import com.lihacovs.android.beepass.authentication.AuthenticationActivity;
import com.lihacovs.android.beepass.utils.SharedPrefHelper;

/**
 * App introduction slides showed once per app installation;
 * Api library used for this intro: 'com.github.paolorotolo:appintro:4.2.0'
 */

public class CloudPassIntro extends AppIntro2{
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //This is App main entry point, required to initialize SharedPreferences once on app start
        SharedPrefHelper.init(getApplicationContext());
        if (SharedPrefHelper.isAppIntroWatched()) {
            Intent intent = new Intent(this, AuthenticationActivity.class);
            startActivity(intent);
            finish();
        }

        if(getSupportActionBar()!=null) getSupportActionBar().hide();

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest
        addSlide(AppIntroFragment.newInstance(getString(R.string.zero_screen_tittle),
                getString(R.string.zero_screen_description),
                R.drawable.ic_honeycombs_with_bee,
                ContextCompat.getColor(getApplicationContext(),R.color.colorGray700)));
        addSlide(AppIntroFragment.newInstance(getString(R.string.first_screen_tittle),
                getString(R.string.first_screen_description),
                R.drawable.ic_honeycombs_with_key,
                ContextCompat.getColor(getApplicationContext(),R.color.colorGray700)));
        addSlide(AppIntroFragment.newInstance(getString(R.string.second_screen_tittle),
                getString(R.string.second_screen_description),
                R.drawable.ic_honeycombs_with_engine,
                ContextCompat.getColor(getApplicationContext(),R.color.colorGray700)));
        addSlide(AppIntroFragment.newInstance(getString(R.string.third_screen_tittle),
                getString(R.string.third_screen_description),
                R.drawable.ic_honeycombs_with_secure,
                ContextCompat.getColor(getApplicationContext(),R.color.colorGray700)));

        // OPTIONAL METHODS
        //refer to https://github.com/apl-devs/AppIntro for available methods

        // SHOW or HIDE the statusbar
        showStatusBar(false);
        showSkipButton(false);
        setZoomAnimation();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        SharedPrefHelper.watchAppIntro(true);

        //If user was logged when navigating to AppIntro, simply finish activity and return
        //to previous activity stack
        if(SharedPrefHelper.isUserLoggedIn()){
            finish();
        }else {
            Intent intent = new Intent(this, AuthenticationActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when slide is changed
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPrefHelper.watchAppIntro(true);
    }
}
