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

package com.lihacovs.android.beepass.categories;

import android.content.Context;
import android.support.annotation.NonNull;

import com.lihacovs.android.beepass.R;
import com.lihacovs.android.beepass.data.model.Category;
import com.lihacovs.android.beepass.data.model.Credential;
import com.lihacovs.android.beepass.data.source.AppRepository;
import com.lihacovs.android.beepass.utils.SharedPrefHelper;

import java.util.List;

import static android.support.v4.util.Preconditions.checkNotNull;
import static com.lihacovs.android.beepass.utils.EncryptionHelper.decryptText;
import static com.lihacovs.android.beepass.utils.EncryptionHelper.encryptText;

/**
 * Listens to user actions from the UI ({@link CategoriesFragment}), retrieves the data and updates
 * the UI as required.
 */

class CategoriesPresenter implements CategoriesContract.Presenter {

    @NonNull
    private Context mContext;

    private final AppRepository mAppRepository;

    private final CategoriesContract.View mCategoriesDetailView;

    private String mMasterPassword;

    @NonNull
    private String mCategoryId;


    private Category mCategory;

    @NonNull
    private String mUserId;

    CategoriesPresenter(@NonNull Context context,
                        @NonNull String categoryId,
                        @NonNull String userId,
                        @NonNull AppRepository appRepository,
                        @NonNull CategoriesContract.View categoriesView) {
        mContext = context;
        mCategoryId = categoryId;
        mUserId = userId;
        mAppRepository = checkNotNull(appRepository, "Repository cannot be null!");
        mCategoriesDetailView = checkNotNull(categoriesView, "DetailView cannot be null!");
        mCategoriesDetailView.setPresenter(this);
        mMasterPassword = SharedPrefHelper.getMasterPassword();
    }

    @Override
    public void start() {
        loadCategory();
    }


    private void loadCategory() {
        mCategory = mAppRepository.getCategoryById(mCategoryId);
        mCategoriesDetailView.showCategoryName(decryptCategoryName());
    }

    @Override
    public Category getCategory() {
        return mAppRepository.getCategoryById(mCategoryId);
    }

    @Override
    public void updateCategoryName(String name) {
        String encryptedCategoryName = encryptResourceName(name);
        mCategory.setCategoryName(encryptedCategoryName);
        mAppRepository.updateCategory(mCategory);

    }

    public void updateCategoryImageName(String image) {
        String encryptedCategoryImageName = encryptResourceName(image);
        mCategory.setImageName(encryptedCategoryImageName);
        mAppRepository.updateCategory(mCategory);

    }

    @Override
    public void deleteCategory() {
        mAppRepository.deleteCategory(mCategoryId);


        //moves credentials to default category
        List<Credential> credentials = mAppRepository.getCredentials(mCategoryId);
        if (!credentials.isEmpty()) {
            String defaultCategoryId = getDefaultCategory().getCategoryId();
            for (Credential cred : credentials) {
                cred.setCategoryId(defaultCategoryId);
                mAppRepository.updateCredential(cred);
            }
        }
        mCategoriesDetailView.showCredentialDetailScreen();
    }

    /**
     * Searches for default category in local DB by encrypted category name.
     * Creates default category if it doesn't exist.
     *
     * @return default category for credentials
     */
    private Category getDefaultCategory() {
        String defaultCategoryName = mContext.getString(R.string.default_category);
        String encryptedDefaultCategoryName = encryptText(mMasterPassword, defaultCategoryName);
        Category defaultCategory;
        if (mAppRepository.getDefaultCategory(mUserId, encryptedDefaultCategoryName) != null) {
            defaultCategory = mAppRepository.getDefaultCategory(mUserId, encryptedDefaultCategoryName);
        } else {
            String encryptedCategoryName = encryptText(mMasterPassword, defaultCategoryName);
            String encryptedCategoryImage = encryptText(mMasterPassword, "ic_category_folder_24px");
            String encryptedCategoryFields = encryptText(mMasterPassword, "default_fields");
            defaultCategory = new Category(
                    mUserId,
                    encryptedCategoryName,
                    encryptedCategoryImage,
                    encryptedCategoryFields);
            mAppRepository.saveCategory(defaultCategory);
        }
        return defaultCategory;
    }

    @Override
    public void logoutUser() {
        SharedPrefHelper.logoutUser();
        mCategoriesDetailView.showLoginScreen();
    }

    @Override
    public String decryptCategoryName() {
        return decryptText(mMasterPassword, mCategory.getCategoryName());
    }

    @Override
    public String decryptCategoryImageName() {
        return decryptText(mMasterPassword, mCategory.getImageName());
    }

    private String encryptResourceName(String name) {
        return encryptText(mMasterPassword, name);
    }
}
