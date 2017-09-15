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

package com.lihacovs.android.beepass.data.source.remote;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lihacovs.android.beepass.data.model.Category;
import com.lihacovs.android.beepass.data.model.Credential;
import com.lihacovs.android.beepass.data.model.Field;
import com.lihacovs.android.beepass.data.model.User;
import com.lihacovs.android.beepass.data.source.AppDataSource;

import java.util.List;

/**
 * Implementation of the remote data source - Not implemented in app Version 1.0
 */

public class AppRemoteDataSource implements AppDataSource {

    private static AppRemoteDataSource INSTANCE;
    private boolean mFirebaseConnected;

    // Prevent direct instantiation.
    private AppRemoteDataSource() {

    }

    public static AppRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AppRemoteDataSource();
        }
        return INSTANCE;
    }


    @Override
    public void addUser(@NonNull User user) {
        /*String userId = user.getUserId();
        String userEmail = user.getUserEmail();
        mDatabase.child("users").child(userId).setValue(user);
        mDatabase.child("emails").setValue(userEmail);*/
    }

    @Override
    public boolean checkUser(@Nullable String email) {
        //implementation not required, check is completed on addUser()
        return false;
    }

    @Override
    public boolean checkUser(@Nullable String email, @Nullable String password) {
        return false;
    }

    @Override
    public String getUserId(@NonNull String email, @NonNull String password) {
        return null;
    }

    @Override
    public User getUserByRemoteId(String remoteId) {
        return null;
    }

    @Override
    public void saveCategory(@NonNull Category category) {

    }

    @Override
    public void saveCredential(@NonNull Credential credential) {

    }

    @Override
    public void saveField(@NonNull Field field) {

    }

    @Override
    public Credential getCredential(@NonNull String credentialId) {
        return null;
    }

    @Override
    public Category getCategory(@NonNull String credentialId) {
        return null;
    }

    @Override
    public List<Field> getFields(@NonNull String credentialId) {
        return null;
    }

    @Override
    public void deleteCredential(String credentialId) {

    }

    @Override
    public void deleteField(String credentialId) {

    }

    @Override
    public void updateCredential(Credential credential) {

    }

    @Override
    public void updateField(Field field) {

    }

    @Override
    public List<Category> getCategories(String userId) {
        return null;
    }

    @Override
    public List<Credential> getCredentials(String categoryId) {
        return null;
    }

    @Override
    public Category getDefaultCategory(String userId, String defaultCategoryName) {
        return null;
    }

    @Override
    public Category getCategoryById(String categoryId) {
        return null;
    }

    @Override
    public void updateCategory(Category category) {

    }

    @Override
    public void deleteCategory(String categoryId) {

    }

    @Override
    public void deleteFieldById(String fieldId) {

    }
}
