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

import com.lihacovs.android.beepass.BasePresenter;
import com.lihacovs.android.beepass.BaseView;
import com.lihacovs.android.beepass.data.model.Category;
import com.lihacovs.android.beepass.data.model.Credential;

import java.util.List;

/**
 * This specifies the contract between the view and the presenter.
 */

public interface CredentialsContract {

    interface View extends BaseView<Presenter> {

        void showSnackBar(String text);

        void showAddCredential(String credentialId, String userId);

        void showCredentialDetailsScreen(String credentialId, String userId);

        void showLoginScreen();

        void showCategoryCredentials(List<CategoryGroup> groups);

        void showEditCategoryScreen(String categoryId, String userId);
    }

    interface Presenter extends BasePresenter {

        void loadCredentials();

        void addNewCredential(String categoryId);

        void deleteCredential(Credential credential);

        void openCredentialDetails(Credential credential);

        void logoutUser();

        void editCategory(String categoryId);

        Category getCategory(String categoryId);

        Category getDefaultCategory();

        void addNewCategory();

        String decryptCategoryName(Category category);

        String decryptCategoryImage(Category category);

        String decryptCredentialName(Credential credential);
    }
}
