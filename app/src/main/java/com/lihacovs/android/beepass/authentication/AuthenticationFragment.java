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

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lihacovs.android.beepass.R;
import com.lihacovs.android.beepass.credentials.CredentialsActivity;
import com.lihacovs.android.beepass.utils.SharedPrefHelper;

import static android.support.v4.util.Preconditions.checkNotNull;

/**
 * Fragment
 */

@SuppressWarnings("FieldCanBeLocal")
public class AuthenticationFragment extends Fragment implements AuthenticationContract.View {

    private AuthenticationContract.Presenter mPresenter;

    private NestedScrollView mRegisterView;
    private NestedScrollView mLoginView;
    private CoordinatorLayout mCoordinatorLayout;

    private TextInputLayout mLoginEmailTil;
    private TextInputLayout mLoginPasswordTil;
    private TextInputLayout mRegisterEmailTil;
    private TextInputLayout mRegisterPasswordTil;

    private TextInputEditText mLoginEmailTiet;
    private TextInputEditText mLoginPasswordTiet;
    private TextInputEditText mRegisterEmailTiet;
    private TextInputEditText mRegisterPasswordTiet;

    private AppCompatButton mLoginButton;
    private AppCompatButton mRegisterButton;

    private AppCompatTextView mRegisterLinkTextView;
    private AppCompatTextView mLoginLinkTextView;


    public static AuthenticationFragment newInstance() {
        return new AuthenticationFragment();
    }

    @Override
    public void setPresenter(@NonNull AuthenticationContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_authentication, container, false);
        mCoordinatorLayout = view.findViewById(R.id.cl_authentication);

        //set up login view
        mLoginView = view.findViewById(R.id.nsv_authentication_login_container);
        mLoginEmailTil = view.findViewById(R.id.til_authentication_login_email);
        mLoginPasswordTil = view.findViewById(R.id.til_authentication_login_password);
        mLoginEmailTiet = view.findViewById(R.id.tiet_authentication_login_email);
        mLoginEmailTiet.setText(SharedPrefHelper.getLoginEmail());
        mLoginPasswordTiet = view.findViewById(R.id.tiet_authentication_login_password);

        mLoginButton = view.findViewById(R.id.acbtn_authentication_login);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mLoginEmailTiet.getText().toString();
                String masterPassword = mLoginPasswordTiet.getText().toString();
                if (mPresenter.validateEmail(email)
                        && mPresenter.validateMasterPassword(masterPassword)) {
                    mPresenter.loginUser(email, masterPassword);
                }
            }
        });

        mRegisterLinkTextView = view.findViewById(R.id.actv_authentication_register_link);
        mRegisterLinkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRegisterView();
            }
        });

        //set up register view
        mRegisterView = view.findViewById(R.id.nsv_authentication_register_container);
        mRegisterEmailTil = view.findViewById(R.id.til_authentication_register_email);
        mRegisterPasswordTil = view.findViewById(R.id.til_authentication_register_password);
        mRegisterEmailTiet = view.findViewById(R.id.tiet_authentication_register_email);
        mRegisterPasswordTiet = view.findViewById(R.id.tiet_authentication_register_password);

        mRegisterButton = view.findViewById(R.id.acbtn_authentication_register);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mRegisterEmailTiet.getText().toString().trim();
                String masterPassword = mRegisterPasswordTiet.getText().toString();
                if (mPresenter.validateEmail(email)
                        && mPresenter.validateMasterPassword(masterPassword)) {
                    mPresenter.registerUser(email, masterPassword);
                }
            }
        });

        mLoginLinkTextView = view.findViewById(R.id.actv_authentication_login_link);
        mLoginLinkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoginView();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void showSnackBar(int strId) {
        Snackbar.make(mCoordinatorLayout, strId,
                Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void showCredentialsView(String userId) {
        Intent intent = CredentialsActivity.newIntent(getContext(), userId);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void showEmailValidationError(String message) {
        if (mLoginView.getVisibility() == View.VISIBLE) {
            mLoginEmailTil.setError(message);
        } else {
            mRegisterEmailTil.setError(message);
        }
    }

    @Override
    public void hideEmailValidationError() {
        if (mLoginView.getVisibility() == View.VISIBLE) {
            mLoginEmailTil.setErrorEnabled(false);
        } else {
            mRegisterEmailTil.setErrorEnabled(false);
        }
    }

    @Override
    public void showPasswordValidationError(String message) {
        if (mLoginView.getVisibility() == View.VISIBLE) {
            mLoginPasswordTil.setError(message);
        } else {
            mRegisterPasswordTil.setError(message);
        }
    }

    @Override
    public void hidePasswordValidationError() {
        if (mLoginView.getVisibility() == View.VISIBLE) {
            mLoginPasswordTil.setErrorEnabled(false);
        } else {
            mRegisterPasswordTil.setErrorEnabled(false);
        }
    }

    @Override
    public void showRegisterView() {
        mLoginView.setVisibility(View.GONE);
        mRegisterView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoginView() {
        mLoginView.setVisibility(View.VISIBLE);
        mRegisterView.setVisibility(View.GONE);
    }
}
