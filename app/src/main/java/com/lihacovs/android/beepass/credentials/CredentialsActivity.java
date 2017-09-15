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

package com.lihacovs.android.beepass.credentials;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
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
 * Credential groups activity to setup fragment and presenter
 */
public class CredentialsActivity extends AppCompatActivity {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private static final String EXTRA_USER_ID =
            "com.lihacovs.android.beepass.credentials.user_email";

    private boolean doubleBackToExitPressedOnce = false;

    public static Intent newIntent(Context packageContext, String userId) {
        Intent intent = new Intent(packageContext, CredentialsActivity.class);
        intent.putExtra(EXTRA_USER_ID, userId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credentials);

        //Customize some ActionBar properties related to this activity
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(R.string.app_name);
        }

        String userId = (String) getIntent().getSerializableExtra(EXTRA_USER_ID);
        CredentialsFragment credentialsFragment = (CredentialsFragment)
                getSupportFragmentManager().findFragmentById(R.id.fl_credentials_content);
        if (credentialsFragment == null) {
            // Create the fragment
            credentialsFragment = CredentialsFragment.newInstance();
            AppUtils.addFragmentToActivity(
                    getSupportFragmentManager(),
                    credentialsFragment,
                    R.id.fl_credentials_content);
        }

        AppRepository repository = AppRepository.getInstance(
                AppRemoteDataSource.getInstance(),
                AppLocalDataSource.getInstance(getApplicationContext()));

        // Create the presenter
        new CredentialsPresenter(
                this,
                repository,
                credentialsFragment,
                userId);

        //Initialize shared preferences
        SharedPrefHelper.init(getApplicationContext());
    }

    //back button double click to exit
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            SharedPrefHelper.logoutUser();
            finish();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Snackbar.make(findViewById(R.id.cl_credentials),
                R.string.click_back_again,
                Snackbar.LENGTH_SHORT)
                .show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
