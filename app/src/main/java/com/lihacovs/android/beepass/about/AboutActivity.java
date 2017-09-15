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

package com.lihacovs.android.beepass.about;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;

import com.lihacovs.android.beepass.R;
import com.lihacovs.android.beepass.utils.AppUtils;
import com.lihacovs.android.beepass.utils.SharedPrefHelper;

/**
 * AboutApp activity to setup fragment
 */

public class AboutActivity extends AppCompatActivity{
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, AboutActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        //Customize some ActionBar properties related to this activity
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(R.string.about_app);
            ab.setDisplayUseLogoEnabled(false);
        }

        AboutFragment aboutFragment = (AboutFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fl_about_content);
        if (aboutFragment == null) {
            aboutFragment = AboutFragment.newInstance();
            AppUtils.addFragmentToActivity(getSupportFragmentManager(),
                    aboutFragment, R.id.fl_about_content);
        }

        //Initialize shared preferences
        SharedPrefHelper.init(getApplicationContext());
    }
}
