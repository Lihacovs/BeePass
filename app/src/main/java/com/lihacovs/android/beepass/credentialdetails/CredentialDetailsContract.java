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

import com.lihacovs.android.beepass.BasePresenter;
import com.lihacovs.android.beepass.BaseView;
import com.lihacovs.android.beepass.data.model.Category;
import com.lihacovs.android.beepass.data.model.Field;

import java.util.List;

/**
 * This specifies the contract between the view and the presenter.
 */

interface CredentialDetailsContract {

    interface View extends BaseView<Presenter>{

        void showCredentialTitle(String credentialName);

        void showFields(List<Field> fields);

        void showSpinnerCategories(List<Category> categories);

        void showSpinnerCategoryValue(Category category);

        void showCredentialListScreen();

        void showLoginScreen();

        void showSnackBar(String text);

        void showAddCategoryScreen(String categoryId, String userId);

        void showEditCategoryScreen(String categoryId, String userId);

    }

    interface Presenter extends BasePresenter{

        void loadCredential();

        void updateCredential(String title);

        void deleteCredential();

        void logoutUser();

        Field addNewField();

        void updateFieldsRv();

        void updateField(Field field);

        List<Category> getCategories();

        void addCategory();

        void editCategory();

        void updateCredentialCategory(String categoryId);

        void deleteField(Field field);

        String decryptFieldName(Field field);

        String decryptFieldText(Field field);

        String decryptCategoryName(Category category);

        String encryptFieldText(String text);

        String decryptCredentialName();
    }
}
