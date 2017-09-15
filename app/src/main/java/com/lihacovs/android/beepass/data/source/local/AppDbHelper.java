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

package com.lihacovs.android.beepass.data.source.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.lihacovs.android.beepass.data.source.local.AppDbSchema.*;

/**
 * DB helper class to create database
 */

class AppDbHelper extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "beepass.db";

    AppDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + UsersTable.NAME + " (" +
                " _id integer primary key autoincrement, " +
                UsersTable.Cols.ID + ", " +
                UsersTable.Cols.REMOTE_ID + ", " +
                UsersTable.Cols.EMAIL + ", " +
                UsersTable.Cols.MASTER_PASSWORD +
                ")"
        );

        db.execSQL("create table " + CategoriesTable.NAME + " (" +
                " _id integer primary key autoincrement, " +
                CategoriesTable.Cols.ID + ", " +
                CategoriesTable.Cols.USER_ID + ", " +
                CategoriesTable.Cols.IMAGE_NAME + ", " +
                CategoriesTable.Cols.FIELD_ARRAY_NAME + ", " +
                CategoriesTable.Cols.NAME + ", " +
                CategoriesTable.Cols.UPDATE_DATE +
                ")"
        );

        db.execSQL("create table " + CredentialsTable.NAME + " (" +
                " _id integer primary key autoincrement, " +
                CredentialsTable.Cols.ID + ", " +
                CredentialsTable.Cols.USER_ID + ", " +
                CredentialsTable.Cols.CATEGORY_ID + ", " +
                CredentialsTable.Cols.TITLE + ", " +
                CredentialsTable.Cols.UPDATE_DATE +
                ")"
        );

        db.execSQL("create table " + FieldsTable.NAME + " (" +
                " _id integer primary key autoincrement, " +
                FieldsTable.Cols.ID + ", " +
                FieldsTable.Cols.USER_ID + ", " +
                FieldsTable.Cols.CREDENTIAL_ID + ", " +
                FieldsTable.Cols.NAME + ", " +
                FieldsTable.Cols.TEXT + ", " +
                FieldsTable.Cols.UPDATE_DATE +
                ")"
        );


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // Not required as at version 1
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
}
