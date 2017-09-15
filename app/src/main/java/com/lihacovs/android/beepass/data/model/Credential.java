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

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.UUID;

/**
 * Model class for Credential entity
 */

public class Credential implements Parcelable {

    private String mCredentialId;

    private String mUserId;

    private String mCategoryId;

    @Nullable
    private String mTitle;

    @Nullable
    private String mUpdateDate;

    //Used to create initial credential object
    public Credential() {
        this("Empty User ID", "Empty Category Id");
    }

    public Credential(@NonNull String userId, @NonNull String categoryId){
        this(userId, categoryId, null);
    }

    public Credential(@NonNull String userId, @NonNull String categoryId, @Nullable String title){
        mCredentialId = UUID.randomUUID().toString();
        mUserId = userId;
        mCategoryId = categoryId;
        mTitle = title;
    }

    protected Credential(Parcel in) {
        mTitle = in.readString();
    }

    @NonNull
    public String getCredentialId() {
        return mCredentialId;
    }

    public void setCredentialId(@NonNull String credentialId) {
        mCredentialId = credentialId;
    }

    @NonNull
    public String getUserId() {
        return mUserId;
    }

    public void setUserId(@NonNull String userId) {
        mUserId = userId;
    }

    @NonNull
    public String getCategoryId() {
        return mCategoryId;
    }

    public void setCategoryId(@NonNull String categoryId) {
        mCategoryId = categoryId;
    }

    @Nullable
    public String getTitle() {
        return mTitle;
    }

    public void setTitle(@Nullable String title) {
        mTitle = title;
    }

    @Nullable
    public String getUpdateDate() {
        return mUpdateDate;
    }

    public void setUpdateDate(@Nullable String updateDate) {
        mUpdateDate = updateDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mTitle);
    }

    public static final Creator<Credential> CREATOR = new Creator<Credential>() {
        @Override
        public Credential createFromParcel(Parcel in) {
            return new Credential(in);
        }

        @Override
        public Credential[] newArray(int size) {
            return new Credential[size];
        }
    };
}
