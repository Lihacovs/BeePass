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

package com.lihacovs.android.beepass.data.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.UUID;

/**
 * Model class for User entity
 */

public class User {

    @NonNull
    private String mUserId;

    @Nullable
    private String mUserRemoteId;

    @NonNull
    private String mUserEmail;

    @NonNull
    private String mMasterPassword;

    public User(@NonNull String email, @NonNull String masterPassword) {
        mUserId = UUID.randomUUID().toString();
        mUserEmail = email;
        mMasterPassword = masterPassword;
    }

    @NonNull
    public String getUserId() {
        return mUserId;
    }

    public void setUserId(@NonNull String userId) {
        mUserId = userId;
    }

    @Nullable
    public String getUserRemoteId() {
        return mUserRemoteId;
    }

    public void setUserRemoteId(@Nullable String userRemoteId) {
        mUserRemoteId = userRemoteId;
    }

    @NonNull
    public String getUserEmail() {
        return mUserEmail;
    }

    public void setUserEmail(@NonNull String userEmail) {
        mUserEmail = userEmail;
    }

    @NonNull
    public String getMasterPassword() {
        return mMasterPassword;
    }

    public void setMasterPassword(@NonNull String masterPassword) {
        mMasterPassword = masterPassword;
    }
}
