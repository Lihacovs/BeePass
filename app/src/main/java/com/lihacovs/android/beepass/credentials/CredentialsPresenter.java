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
import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.lihacovs.android.beepass.R;
import com.lihacovs.android.beepass.data.model.Category;
import com.lihacovs.android.beepass.data.model.Credential;
import com.lihacovs.android.beepass.data.model.Field;
import com.lihacovs.android.beepass.data.source.AppRepository;
import com.lihacovs.android.beepass.utils.SharedPrefHelper;

import java.util.ArrayList;
import java.util.List;

import static android.support.v4.util.Preconditions.checkNotNull;
import static com.lihacovs.android.beepass.utils.EncryptionHelper.decryptText;
import static com.lihacovs.android.beepass.utils.EncryptionHelper.encryptText;

/**
 * Listens to user actions from the UI ({@link CredentialsFragment}), retrieves the data and updates the
 * UI as required.
 */

@SuppressWarnings("FieldCanBeLocal")
class CredentialsPresenter implements CredentialsContract.Presenter {

    private final AppRepository mAppRepository;

    private final CredentialsContract.View mCredentialsView;

    private String mUserId;

    private Context mContext;

    private String mMasterPassword;

    private List<CategoryGroup> mCategoryGroup;

    CredentialsPresenter(@NonNull Context context,
                         @NonNull AppRepository appRepository,
                         @NonNull CredentialsContract.View credentialsView,
                         @NonNull String userId) {
        mContext = context;
        mAppRepository = checkNotNull(appRepository, "Repository cannot be null");
        mCredentialsView = checkNotNull(credentialsView, "credentialsView cannot be null!");
        mMasterPassword = SharedPrefHelper.getMasterPassword();
        mUserId = checkNotNull(userId, "userId cannot be null!");
        mCredentialsView.setPresenter(this);
    }

    @Override
    public void start() {
        loadCredentials();
    }

    @Override
    public void loadCredentials() {
        List<CategoryGroup> categoryGroup = new ArrayList<>();

        List<Category> categories = mAppRepository.getCategories(mUserId);

        for (Category category : categories) {
            String categoryId = category.getCategoryId();
            List<Credential> credentials = mAppRepository.getCredentials(category.getCategoryId());
            categoryGroup.add(new CategoryGroup(categoryId, credentials));
        }
        mCategoryGroup = categoryGroup;
        mCredentialsView.showCategoryCredentials(mCategoryGroup);
    }

    /**
     * Adds new credential to db with some common predefined fields from arrays.xml
     * {@link com.lihacovs.android.beepass.R.array}
     *
     * @param categoryId sets category for new credential
     */
    @Override
    public void addNewCredential(String categoryId) {
        Credential newCredential = new Credential(mUserId, categoryId);
        String newCredentialId = newCredential.getCredentialId();

        //Gets predefined filed array from arrays.xml for particular category
        String encryptedFieldArrayName = mAppRepository.getCategoryById(categoryId).getFieldArrayName();
        String filedArrayName = decryptText(mMasterPassword, encryptedFieldArrayName);
        Resources res = mContext.getResources();
        int predefinedFieldsArray = res.getIdentifier(filedArrayName, "array", mContext.getPackageName());
        String[] predefinedFields = res.getStringArray(predefinedFieldsArray);

        for (String fieldName : predefinedFields) {
            String encryptedFieldName = encryptText(mMasterPassword, fieldName);
            Field newCredentialField1 = new Field(
                    newCredentialId,
                    mUserId,
                    encryptedFieldName,
                    null);
            mAppRepository.saveField(newCredentialField1);
        }

        mAppRepository.saveCredential(newCredential);
        mCredentialsView.showAddCredential(newCredentialId, mUserId);
    }

    @Override
    public void deleteCredential(Credential credential) {
        String credentialName;
        if(credential.getTitle() == null || decryptCredentialName(credential).trim().equals("")){
            credentialName = mContext.getString(R.string.credential);
        }else{
            credentialName = decryptCredentialName(credential);
        }
        mCredentialsView.showSnackBar(credentialName + " " + mContext.getString(R.string.deleted));
        mAppRepository.deleteCredential(credential.getCredentialId());
    }

    @Override
    public void openCredentialDetails(Credential credential) {
        checkNotNull(credential, "credential cannot be null!");
        mCredentialsView.showCredentialDetailsScreen(credential.getCredentialId(), mUserId);
    }

    @Override
    public void logoutUser() {
        SharedPrefHelper.logoutUser();
        mCredentialsView.showLoginScreen();
    }

    @Override
    public void editCategory(String categoryId) {
        Category category = mAppRepository.getCategoryById(categoryId);
        mCredentialsView.showEditCategoryScreen(category.getCategoryId(), mUserId);
    }

    @Override
    public Category getCategory(String categoryId) {
        return mAppRepository.getCategoryById(categoryId);
    }

    /**
     * Searches for default category in local DB by encrypted category name.
     * Creates default category if it doesn't exist.
     *
     * @return default category for credentials
     */
    @Override
    public Category getDefaultCategory() {
        String defaultCategoryName = mContext.getString(R.string.default_category);
        Category defaultCategory;
        String encryptedDefaultCategoryName = encryptText(mMasterPassword, defaultCategoryName);
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
    public void addNewCategory() {
        String encryptedCategoryImage = encryptText(mMasterPassword, "ic_category_folder_24px");
        String encryptedCategoryFields = encryptText(mMasterPassword, "default_fields");
        Category category = new Category(
                mUserId,
                null,
                encryptedCategoryImage,
                encryptedCategoryFields);
        mAppRepository.saveCategory(category);
        mCredentialsView.showEditCategoryScreen(category.getCategoryId(), mUserId);
    }

    @Override
    public String decryptCategoryName(Category category) {
        return decryptText(mMasterPassword, category.getCategoryName());
    }

    @Override
    public String decryptCategoryImage(Category category) {
        return decryptText(mMasterPassword, category.getImageName());
    }

    @Override
    public String decryptCredentialName(Credential credential) {
        return decryptText(mMasterPassword, credential.getTitle());
    }
}
