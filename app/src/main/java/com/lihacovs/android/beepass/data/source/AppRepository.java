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

package com.lihacovs.android.beepass.data.source;

import android.support.annotation.NonNull;

import com.lihacovs.android.beepass.data.model.Category;
import com.lihacovs.android.beepass.data.model.Credential;
import com.lihacovs.android.beepass.data.model.Field;
import com.lihacovs.android.beepass.data.model.User;

import java.util.List;

import static android.support.v4.util.Preconditions.checkNotNull;

/**
 * Concrete implementation to load credentials
 */

public class AppRepository implements AppDataSource{

    private static AppRepository INSTANCE = null;

    private final AppDataSource mRemoteDataSource;

    private final AppDataSource mLocalDataSource;

    // Prevent direct instantiation.
    private AppRepository(@NonNull AppDataSource appRemoteDataSource,
                                  @NonNull AppDataSource appLocalDataSource) {
        mRemoteDataSource = checkNotNull(appRemoteDataSource);
        mLocalDataSource = checkNotNull(appLocalDataSource);
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param appRemoteDataSource the backend data source
     * @param appLocalDataSource  the device storage data source
     * @return the {@link AppRepository} instance
     */
    public static AppRepository getInstance(AppDataSource appRemoteDataSource,
                                                    AppDataSource appLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new AppRepository(appRemoteDataSource, appLocalDataSource);
        }
        return INSTANCE;
    }

    /**
     * Used to force {@link #getInstance(AppDataSource, AppDataSource)} to create a new instance
     * next time it's called.
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void addUser(@NonNull User user) {
        checkNotNull(user);
        mLocalDataSource.addUser(user);
        mRemoteDataSource.addUser(user);
    }

    @Override
    public String getUserId(@NonNull String email, @NonNull String password) {
        return mLocalDataSource.getUserId(email, password);
    }

    @Override
    public User getUserByRemoteId(String remoteId) {
        return mLocalDataSource.getUserByRemoteId(remoteId);
    }

    @Override
    public void saveCategory(@NonNull Category category) {
        mLocalDataSource.saveCategory(category);
    }

    @Override
    public void saveCredential(@NonNull Credential credential) {
        mLocalDataSource.saveCredential(credential);
    }

    @Override
    public void saveField(@NonNull Field field) {
        mLocalDataSource.saveField(field);
    }

    @Override
    public Credential getCredential(@NonNull String credentialId) {
        return mLocalDataSource.getCredential(credentialId);
    }

    @Override
    public Category getCategory(@NonNull String credentialId) {
        return mLocalDataSource.getCategory(credentialId);
    }

    @Override
    public List<Field> getFields(@NonNull String credentialId) {
        return mLocalDataSource.getFields(credentialId);
    }

    @Override
    public void deleteCredential(String credentialId) {
        mLocalDataSource.deleteCredential(credentialId);
    }

    @Override
    public void deleteField(String credentialId) {
        mLocalDataSource.deleteField(credentialId);
    }

    @Override
    public void updateCredential(Credential credential) {
        mLocalDataSource.updateCredential(credential);
    }

    @Override
    public void updateField(Field field) {
        mLocalDataSource.updateField(field);
    }

    @Override
    public List<Category> getCategories(String userId) {
        return mLocalDataSource.getCategories(userId);
    }

    @Override
    public List<Credential> getCredentials(String categoryId) {
        return mLocalDataSource.getCredentials(categoryId);
    }

    @Override
    public Category getDefaultCategory(String userId, String defaultCategoryName) {
        return mLocalDataSource.getDefaultCategory(userId, defaultCategoryName);
    }

    @Override
    public Category getCategoryById(String categoryId) {
        return mLocalDataSource.getCategoryById(categoryId);
    }

    @Override
    public void updateCategory(Category category) {
        mLocalDataSource.updateCategory(category);
    }

    @Override
    public void deleteCategory(String categoryId) {
        mLocalDataSource.deleteCategory(categoryId);
    }

    @Override
    public void deleteFieldById(String fieldId) {
        mLocalDataSource.deleteFieldById(fieldId);
    }

    @Override
    public boolean checkUser(String email) {
        return mLocalDataSource.checkUser(email);
    }

    @Override
    public boolean checkUser(String email, String password) {
        return mLocalDataSource.checkUser(email, password);
    }
}