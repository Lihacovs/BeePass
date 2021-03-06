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

package com.lihacovs.android.beepass.credentialdetails;

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
 * Credential details activity to setup fragment and presenter
 */
public class CredentialDetailsActivity extends AppCompatActivity{
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private static final String EXTRA_CREDENTIAL_ID =
            "com.lihacovs.android.beepass.credentialdetail.credential_id";
    private static final String EXTRA_USER_ID =
            "com.lihacovs.android.beepass.credentialdetail.user_id";

    public static Intent newIntent(Context packageContext, String credentialId, String userId) {
        Intent intent = new Intent(packageContext, CredentialDetailsActivity.class);
        intent.putExtra(EXTRA_CREDENTIAL_ID, credentialId);
        intent.putExtra(EXTRA_USER_ID, userId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credentialdetails);

        //Customize some ActionBar properties related to this activity
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(R.string.credential);
            ab.setDisplayUseLogoEnabled(false);
        }

        String credentialId = (String) getIntent().getSerializableExtra(EXTRA_CREDENTIAL_ID);
        String userId = (String) getIntent().getSerializableExtra(EXTRA_USER_ID);

        CredentialDetailsFragment credentialDetailsFragment = (CredentialDetailsFragment)
                getSupportFragmentManager().findFragmentById(R.id.fl_credentialdetails_content);

        if (credentialDetailsFragment == null) {
            credentialDetailsFragment = CredentialDetailsFragment.newInstance();
            AppUtils.addFragmentToActivity(
                    getSupportFragmentManager(),
                    credentialDetailsFragment,
                    R.id.fl_credentialdetails_content);
        }

        AppRepository repository = AppRepository.getInstance(
                AppRemoteDataSource.getInstance(),
                AppLocalDataSource.getInstance(getApplicationContext()));

        // Create the presenter
        new CredentialDetailsPresenter(this,
                credentialId,
                userId,
                repository,
                credentialDetailsFragment);

        //Initialize shared preferences
        SharedPrefHelper.init(getApplicationContext());
    }
}
