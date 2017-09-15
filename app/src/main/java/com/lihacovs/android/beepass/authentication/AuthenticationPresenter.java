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

package com.lihacovs.android.beepass.authentication;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.util.Patterns;

import com.lihacovs.android.beepass.R;
import com.lihacovs.android.beepass.data.model.Category;
import com.lihacovs.android.beepass.data.model.User;
import com.lihacovs.android.beepass.data.source.AppRepository;
import com.lihacovs.android.beepass.utils.SharedPrefHelper;

import static android.support.v4.util.Preconditions.checkNotNull;
import static com.lihacovs.android.beepass.utils.EncryptionHelper.encryptText;

/**
 * Listens user actions in UI, updates UI as required
 */

public class AuthenticationPresenter implements AuthenticationContract.Presenter {

    private final AppRepository mAppRepository;
    private final AuthenticationContract.View mAuthenticationView;
    private Context mContext;

    AuthenticationPresenter(@NonNull Context context,
                            @NonNull AppRepository appRepository,
                            @NonNull AuthenticationContract.View authView) {
        mContext = context;
        mAppRepository = checkNotNull(appRepository, "Repository cannot be null");
        mAuthenticationView = checkNotNull(authView, "loginView cannot be null!");
        mAuthenticationView.setPresenter(this);
    }

    @Override
    public void start() {
        chooseView();
    }

    private void chooseView() {
            if (SharedPrefHelper.isRegistered()) {
                mAuthenticationView.showLoginView();
            } else {
                mAuthenticationView.showRegisterView();
            }
    }

    @Override
    public boolean validateEmail(String email) {
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mAuthenticationView.showEmailValidationError(
                    mContext.getString(R.string.email_validation_error));
            return false;
        } else {
            mAuthenticationView.hideEmailValidationError();
            return true;
        }
    }

    @Override
    public boolean validateMasterPassword(String password) {
        if (password.length() < 8) {
            mAuthenticationView.showPasswordValidationError(
                    mContext.getString(R.string.password_validation_error));
            return false;
        } else {
            mAuthenticationView.hidePasswordValidationError();
            return true;
        }
    }

    @Override
    public void registerUser(String email, String password) {
        //User data is encrypted with user master password
        String encryptedPassword = encryptText(password, password);
        User user = new User(email, encryptedPassword);

        if (mAppRepository.checkUser(email)) {
            mAuthenticationView.showEmailValidationError(
                    mContext.getString(R.string.user_already_registered));
        } else {
            saveUserToLocalDb(user);
            createCategories(user.getUserId(), password);
            //login user after registration
            loginUser(email, password);
        }
    }

    private void saveUserToLocalDb(User user) {
        mAppRepository.addUser(user);
        SharedPrefHelper.registerUser();
    }

    @Override
    public void loginUser(String email, String password) {
        String encryptedPassword = encryptText(password, password);

        //Login with local Db
        if (mAppRepository.checkUser(email, encryptedPassword)) {
            SharedPrefHelper.createUserLoginSession(email, password);
            SharedPrefHelper.saveLoginEmail(email);
            String userId = mAppRepository.getUserId(email, encryptedPassword);
            mAuthenticationView.showCredentialsView(userId);
        } else {
            mAuthenticationView.showSnackBar(R.string.wrong_email_password);
            SharedPrefHelper.logoutUser();
        }
    }

    /**
     * Creates and saves predefined categories from arrays.xml file
     *
     * @param userId registered user in {@link #registerUser(String, String)}
     */
    private void createCategories(String userId, String password) {
        //Creates two dimensional category array
        Resources res = mContext.getResources();
        TypedArray ta = res.obtainTypedArray(R.array.predefined_categories);
        int n = ta.length();
        String[][] categoryDataArray = new String[n][];
        for (int i = 0; i < n; ++i) {
            int id = ta.getResourceId(i, 0);
            if (id > 0) {
                categoryDataArray[i] = res.getStringArray(id);
            }
        }
        ta.recycle();

        //Saves predefined categories in Db encrypted with user master password
        for (String[] categoryData : categoryDataArray) {
            String encryptedCategoryName = encryptText(password, categoryData[0]);
            String encryptedCategoryImage = encryptText(password, categoryData[1]);
            String encryptedCategoryFields = encryptText(password, categoryData[2]);
            Category category = new Category(
                    userId,
                    encryptedCategoryName,
                    encryptedCategoryImage,
                    encryptedCategoryFields);
            mAppRepository.saveCategory(category);
        }
    }

}
