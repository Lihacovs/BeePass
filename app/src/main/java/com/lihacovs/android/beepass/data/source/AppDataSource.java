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
import android.support.annotation.Nullable;

import com.lihacovs.android.beepass.data.model.Category;
import com.lihacovs.android.beepass.data.model.Credential;
import com.lihacovs.android.beepass.data.model.Field;
import com.lihacovs.android.beepass.data.model.User;

import java.util.List;

/**
 * Main entry point for accessing credentials data.
 */

public interface AppDataSource {

    void addUser(@NonNull User user);

    boolean checkUser(@Nullable String email);

    boolean checkUser(@Nullable String email, @Nullable String password);

    String getUserId(@NonNull String email, @NonNull String password);

    User getUserByRemoteId(String remoteId);

    void saveCategory(@NonNull Category category);

    void saveCredential(@NonNull Credential credential);

    void saveField(@NonNull Field field);

    Credential getCredential(@NonNull String credentialId);

    Category getCategory(@NonNull String credentialId);

    List<Field> getFields(@NonNull String credentialId);

    void deleteCredential(String credentialId);

    void deleteField(String credentialId);

    void updateCredential(Credential credential);

    void updateField(Field field);

    List<Category> getCategories(String userId);

    List<Credential> getCredentials(String categoryId);

    Category getDefaultCategory(String userId, String defaultCategoryName);

    Category getCategoryById(String categoryId);

    void updateCategory(Category category);

    void deleteCategory(String categoryId);

    void deleteFieldById(String fieldId);
}
