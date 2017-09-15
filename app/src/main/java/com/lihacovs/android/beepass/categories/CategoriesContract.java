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

import com.lihacovs.android.beepass.BasePresenter;
import com.lihacovs.android.beepass.BaseView;
import com.lihacovs.android.beepass.data.model.Category;

/**
 * This specifies the contract between the view and the presenter.
 */

interface CategoriesContract {

    interface View extends BaseView<Presenter> {

        void showSnackBar(int strId);

        void showCredentialDetailScreen();

        void showLoginScreen();

        void showCategoryName(String categoryName);

    }

    interface Presenter extends BasePresenter {

        Category getCategory();

        void updateCategoryName(String name);

        void updateCategoryImageName(String image);

        void deleteCategory();

        void logoutUser();

        String decryptCategoryName();

        String decryptCategoryImageName();

    }
}
