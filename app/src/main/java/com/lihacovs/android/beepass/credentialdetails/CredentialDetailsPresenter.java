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
import android.support.annotation.NonNull;

import com.lihacovs.android.beepass.R;
import com.lihacovs.android.beepass.data.model.Category;
import com.lihacovs.android.beepass.data.model.Credential;
import com.lihacovs.android.beepass.data.model.Field;
import com.lihacovs.android.beepass.data.source.AppRepository;
import com.lihacovs.android.beepass.utils.SharedPrefHelper;

import java.util.List;

import static android.support.v4.util.Preconditions.checkNotNull;
import static com.lihacovs.android.beepass.utils.EncryptionHelper.decryptText;
import static com.lihacovs.android.beepass.utils.EncryptionHelper.encryptText;

/**
 * Listens to user actions from the UI ({@link CredentialDetailsFragment}), retrieves the data and updates
 * the UI as required.
 */

class CredentialDetailsPresenter implements CredentialDetailsContract.Presenter {

    private final AppRepository mAppRepository;
    private final CredentialDetailsContract.View mCredentialDetailView;

    @NonNull
    private String mCredentialId;

    private Credential mCredential;
    private List<Field> mCredentialFields;
    private List<Category> mCategories;
    private Category mCategory;

    @NonNull
    private String mUserId;

    @NonNull
    private Context mContext;

    private String mMasterPassword;

    CredentialDetailsPresenter(@NonNull Context context,
                               @NonNull String credentialId,
                               @NonNull String userId,
                               @NonNull AppRepository appRepository,
                               @NonNull CredentialDetailsContract.View credentialDetailView) {
        mCredentialId = credentialId;
        mUserId = userId;
        mContext = context;
        mAppRepository = checkNotNull(appRepository, "Repository cannot be null!");
        mCredentialDetailView = checkNotNull(credentialDetailView, "DetailView cannot be null!");
        mCredentialDetailView.setPresenter(this);
        mMasterPassword = SharedPrefHelper.getMasterPassword();
    }

    @Override
    public void start() {
        loadCredential();
    }

    @Override
    public void loadCredential() {
        mCredential = mAppRepository.getCredential(mCredentialId);
        mCredentialDetailView.showCredentialTitle(decryptCredentialName());

        mCredentialFields = mAppRepository.getFields(mCredentialId);
        mCredentialDetailView.showFields(mCredentialFields);

        mCategories = mAppRepository.getCategories(mUserId);
        mCredentialDetailView.showSpinnerCategories(mCategories);

        mCategory = mAppRepository.getCategory(mCredentialId);
        mCredentialDetailView.showSpinnerCategoryValue(mCategory);

    }

    @Override
    public void updateCredential(String title) {
        String encryptedCredentialName = encryptText(mMasterPassword, title);
        mCredential.setTitle(encryptedCredentialName);
        mAppRepository.updateCredential(mCredential);
    }

    @Override
    public void deleteCredential() {
        mAppRepository.deleteCredential(mCredentialId);
        mCredentialDetailView.showCredentialListScreen();
    }

    @Override
    public void logoutUser() {
        SharedPrefHelper.logoutUser();
        mCredentialDetailView.showLoginScreen();
    }

    @Override
    public Field addNewField() {
        Field field = new Field(mCredentialId, mUserId);
        mAppRepository.saveField(field);
        return field;
    }

    //Updates fields RecyclerView
    @Override
    public void updateFieldsRv() {
        mCredentialFields = mAppRepository.getFields(mCredentialId);
        mCredentialDetailView.showFields(mCredentialFields);
    }

    @Override
    public void updateField(Field field) {
        mAppRepository.updateField(field);
    }

    @Override
    public List<Category> getCategories() {
        return mCategories;
    }

    @Override
    public void addCategory() {
        String encryptedCategoryImage = encryptText(mMasterPassword, "ic_category_folder_24px");
        String encryptedCategoryFields = encryptText(mMasterPassword, "default_fields");
        Category category = new Category(
                mUserId,
                null,
                encryptedCategoryImage,
                encryptedCategoryFields);
        mAppRepository.saveCategory(category);
        mCredentialDetailView.showAddCategoryScreen(category.getCategoryId(), mUserId);
        updateCredentialCategory(category.getCategoryId());
    }

    @Override
    public void editCategory() {
        mCredentialDetailView.showEditCategoryScreen(mCategory.getCategoryId(), mUserId);
    }

    @Override
    public void updateCredentialCategory(String categoryId) {
        mCredential.setCategoryId(categoryId);
        mAppRepository.updateCredential(mCredential);
    }


    @Override
    public void deleteField(Field field) {
        String fieldName;
        if (field.getFieldName() == null || decryptFieldName(field).trim().equals("")) {
            fieldName = mContext.getString(R.string.field);
        } else {
            fieldName = decryptFieldName(field);
        }
        mCredentialDetailView.showSnackBar(fieldName + " " + mContext.getString(R.string.deleted));

        String fieldId = field.getFieldId();
        mAppRepository.deleteFieldById(fieldId);
    }

    @Override
    public String decryptFieldName(Field field) {
        return decryptText(mMasterPassword, field.getFieldName());
    }

    @Override
    public String decryptFieldText(Field field) {
        return decryptText(mMasterPassword, field.getFieldText());
    }

    @Override
    public String decryptCategoryName(Category category) {
        return decryptText(mMasterPassword, category.getCategoryName());
    }

    @Override
    public String encryptFieldText(String text) {
        return encryptText(mMasterPassword, text);
    }

    @Override
    public String decryptCredentialName() {
        return decryptText(mMasterPassword, mCredential.getTitle());
    }
}
