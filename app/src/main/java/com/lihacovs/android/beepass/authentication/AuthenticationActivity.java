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

package com.lihacovs.android.beepass.authentication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;

import com.lihacovs.android.beepass.R;
import com.lihacovs.android.beepass.data.source.AppRepository;
import com.lihacovs.android.beepass.data.source.local.AppLocalDataSource;
import com.lihacovs.android.beepass.data.source.remote.AppRemoteDataSource;
import com.lihacovs.android.beepass.utils.AppUtils;
import com.lihacovs.android.beepass.utils.SharedPrefHelper;

/**
 * Authentication activity to setup fragment and presenter
 */

public class AuthenticationActivity extends AppCompatActivity {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, AuthenticationActivity.class);
        // Closing all activities
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        //Customize some ActionBar properties related to this activity
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(R.string.app_name);
        }

        AuthenticationFragment loginFragment = (AuthenticationFragment)
                getSupportFragmentManager().findFragmentById(R.id.fl_authentication_content);
        if (loginFragment == null) {
            // Create the fragment
            loginFragment = AuthenticationFragment.newInstance();
            AppUtils.addFragmentToActivity(
                    getSupportFragmentManager(), loginFragment, R.id.fl_authentication_content);
        }

        AppRepository repository = AppRepository.getInstance(AppRemoteDataSource.getInstance(),
                AppLocalDataSource.getInstance(getApplicationContext()));

        // Create the presenter
        new AuthenticationPresenter(
                this,
                repository,
                loginFragment);

        //Initialize shared preferences
        SharedPrefHelper.init(getApplicationContext());
    }
}
