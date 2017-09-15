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

package com.lihacovs.android.beepass.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.lihacovs.android.beepass.authentication.AuthenticationActivity;
import com.lihacovs.android.beepass.authentication.AuthenticationPresenter;

/**
 * Helper class to work with shared preferences
 */

public class SharedPrefHelper {

    private static final int PRIVATE_MODE = 0;
    private static final String PREF_FILE_KEY = "com.lihacovs.android.beepass.PREFERENCE_FILE_KEY";
    private static final String KEY_REGISTERED = "registered";
    private static final String KEY_INTRO_WATCHED = "introWatched";
    private static final String KEY_LOGGED = "logged";
    private static final String KEY_MASTER_PASSWORD = "masterPassword";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_LOGIN_EMAIL = "loginEmail";


    private static SharedPreferences mSharedPreferences;

    private SharedPrefHelper() {}

    public static void init(Context context){
        if(mSharedPreferences == null){
            mSharedPreferences =  context.getSharedPreferences(PREF_FILE_KEY, PRIVATE_MODE);
        }
    }

    /**
     * Puts true value if AppIntro was watched
     */
    public static void watchAppIntro(boolean watched){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(KEY_INTRO_WATCHED, watched);
        editor.apply();
    }

    public static boolean isAppIntroWatched(){
        return mSharedPreferences.getBoolean(KEY_INTRO_WATCHED, false);
    }

    /**
     * Puts registered value to check between login or register view on app run
     * {@link AuthenticationPresenter#chooseView()}
     */
    public static void registerUser(){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(KEY_REGISTERED, true);
        editor.apply();
    }

    public static boolean isRegistered(){
        return mSharedPreferences.contains(KEY_REGISTERED);
    }

    /**
     * Creates user session on login{@link AuthenticationPresenter#loginUser(String, String)}
     */
    public static void createUserLoginSession(String email, String password){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(KEY_LOGGED, true);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_MASTER_PASSWORD, password);
        editor.apply();
    }

    public static void logoutUser(){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.remove(KEY_LOGGED);
        editor.remove(KEY_USER_EMAIL);
        editor.remove(KEY_MASTER_PASSWORD);
        editor.apply();
    }

    /**
     * Checks user session on activity start
     * {@link }
     * */
    public static void checkLogin(Context context){
        if(!mSharedPreferences.getBoolean(KEY_LOGGED, false)){
            Intent intent = AuthenticationActivity.newIntent(context);
            context.startActivity(intent);
        }
    }

    public static boolean isUserLoggedIn(){
        return mSharedPreferences.getBoolean(KEY_LOGGED, false);
    }

    /**
     * @return master password to decrypt user data
     */
    public static String getMasterPassword(){
        //TODO: nullPointerException on some cases, check why this occurs
        String masterPassword = "userMasterPassword";
        try {
            masterPassword = mSharedPreferences.getString(KEY_MASTER_PASSWORD, "userMasterPassword");
        }catch (Exception e){
            e.printStackTrace();
        }
        return masterPassword;
    }

    /**
     * Saves login email to shared preferences, used on login screen
     * {@link AuthenticationPresenter#loginUser(String, String)} ()}
     */
    public static void saveLoginEmail(String loginEmail){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(KEY_LOGIN_EMAIL, loginEmail);
        editor.apply();
    }

    public static String getLoginEmail(){
        return mSharedPreferences.getString(KEY_LOGIN_EMAIL, null);
    }
}
