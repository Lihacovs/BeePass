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
 * Model class for Category entity
 */

public class Category {
    @NonNull
    private String mCategoryId;

    @NonNull
    private String mUserId;

    @Nullable
    private String mImageName;

    @Nullable
    private String mFieldArrayName;

    @Nullable
    private String mCategoryName;

    @Nullable
    private String mUpdateDate;

    //Used to create initial category object
    public Category() {
        this("Empty User Id");
    }

    //Not used in app directly
    private Category(@NonNull String userId) {
        this(userId, null);
    }

    //Not used in app directly
    private Category(@NonNull String userId, @Nullable String categoryName) {
        this(userId, categoryName, null, null);
    }

    //Main constructor used in app to create Category
    public Category(@NonNull String userId,
                    @Nullable String categoryName,
                    @Nullable String imageName,
                    @Nullable String fieldArrayName) {
        mCategoryId = UUID.randomUUID().toString();
        mUserId = userId;
        mImageName = imageName;
        mCategoryName = categoryName;
        mFieldArrayName = fieldArrayName;
    }

    @NonNull
    public String getCategoryId() {
        return mCategoryId;
    }

    public void setCategoryId(@NonNull String categoryId) {
        mCategoryId = categoryId;
    }

    @NonNull
    public String getUserId() {
        return mUserId;
    }

    public void setUserId(@NonNull String userId) {
        mUserId = userId;
    }

    @Nullable
    public String getImageName() {
        return mImageName;
    }

    public void setImageName(@Nullable String imageName) {
        mImageName = imageName;
    }

    @Nullable
    public String getFieldArrayName() {
        return mFieldArrayName;
    }

    public void setFieldArrayName(@Nullable String fieldArrayName) {
        mFieldArrayName = fieldArrayName;
    }

    @Nullable
    public String getCategoryName() {
        return mCategoryName;
    }

    public void setCategoryName(@Nullable String categoryName) {
        mCategoryName = categoryName;
    }

    @Nullable
    public String getUpdateDate() {
        return mUpdateDate;
    }

    public void setUpdateDate(@Nullable String updateDate) {
        mUpdateDate = updateDate;
    }

    @Override
    public String toString() {
        return mCategoryName;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Category)) return false;
        Category cat = (Category) obj;
        return this.mCategoryId.equals(cat.mCategoryId);
    }
}
