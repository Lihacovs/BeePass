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

import com.lihacovs.android.beepass.BasePresenter;
import com.lihacovs.android.beepass.BaseView;

/**
 * Contract between view and presenter
 */

interface AuthenticationContract {

    interface View extends BaseView<Presenter>{

        void showRegisterView();

        void showLoginView();

        void showSnackBar(int strId);

        void showCredentialsView(String email);

        void showEmailValidationError(String message);

        void hideEmailValidationError();

        void showPasswordValidationError(String message);

        void hidePasswordValidationError();

    }

    interface Presenter extends BasePresenter {

        boolean validateEmail(String email);

        boolean validateMasterPassword(String password);

        void registerUser(String email, String password);

        void loginUser(String email, String password);
    }
}