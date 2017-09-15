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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lihacovs.android.beepass.data.model.Category;
import com.lihacovs.android.beepass.data.model.Credential;
import com.lihacovs.android.beepass.data.model.Field;
import com.lihacovs.android.beepass.data.model.User;
import com.lihacovs.android.beepass.data.source.AppDataSource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.support.v4.util.Preconditions.checkNotNull;
import static com.lihacovs.android.beepass.data.source.local.AppDbSchema.CategoriesTable;
import static com.lihacovs.android.beepass.data.source.local.AppDbSchema.CredentialsTable;
import static com.lihacovs.android.beepass.data.source.local.AppDbSchema.FieldsTable;
import static com.lihacovs.android.beepass.data.source.local.AppDbSchema.UsersTable;

/**
 * Concrete implementation of a data source as a SQLite db
 * Singleton pattern
 */

public class AppLocalDataSource implements AppDataSource{

    private static AppLocalDataSource INSTANCE;

    private AppDbHelper mDbHelper;

    // Prevent direct instantiation.
    private AppLocalDataSource(@NonNull Context context) {
        checkNotNull(context);
        mDbHelper = new AppDbHelper(context);
    }

    public static AppLocalDataSource getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            INSTANCE = new AppLocalDataSource(context);
        }
        return INSTANCE;
    }

    @Override
    public void addUser(@NonNull User user) {
        checkNotNull(user);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UsersTable.Cols.ID, user.getUserId());
        values.put(UsersTable.Cols.REMOTE_ID, user.getUserRemoteId());
        values.put(UsersTable.Cols.EMAIL, user.getUserEmail());
        values.put(UsersTable.Cols.MASTER_PASSWORD, user.getMasterPassword());

        db.insert(UsersTable.NAME, null, values);
        db.close();
    }

    @Override
    public boolean checkUser(@Nullable String email) {
        String[] columns = {
                UsersTable.Cols.ID
        };
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String selection = UsersTable.Cols.EMAIL + " = ?";

        String[] selectionArgs = {email};
        Cursor cursor = db.query(UsersTable.NAME, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();

        return cursorCount > 0;
    }

    @Override
    public boolean checkUser(@Nullable String email, @Nullable String password) {
        String[] columns = {
                UsersTable.Cols.ID
        };
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String selection = UsersTable.Cols.EMAIL + " = ?" + " AND " + UsersTable.Cols.MASTER_PASSWORD + " = ?";
        String[] selectionArgs = {email, password};
        Cursor cursor = db.query(UsersTable.NAME, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);                      //The sort order

        int cursorCount = cursor.getCount();
        cursor.close();

        return cursorCount > 0;
    }

    @Override
    public String getUserId(@NonNull String email, @NonNull String password) {
        String userId= "";
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] columns = {
                UsersTable.Cols.ID,
                UsersTable.Cols.REMOTE_ID,
                UsersTable.Cols.EMAIL,
                UsersTable.Cols.MASTER_PASSWORD
        };

        String selection = UsersTable.Cols.EMAIL + " LIKE ?" + " AND " + UsersTable.Cols.MASTER_PASSWORD + " LIKE ?";
        String[] selectionArgs = {email, password};

        Cursor c = db.query(UsersTable.NAME, columns, selection, selectionArgs, null, null, null);

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            userId = c.getString(c.getColumnIndexOrThrow(UsersTable.Cols.ID));
        }
        if (c != null) {
            c.close();
        }

        return userId;
    }

    @Override
    public User getUserByRemoteId(String remoteId) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] columns = {
                UsersTable.Cols.ID,
                UsersTable.Cols.REMOTE_ID,
                UsersTable.Cols.EMAIL,
                UsersTable.Cols.MASTER_PASSWORD
        };

        String selection = UsersTable.Cols.REMOTE_ID + " LIKE ?";
        String[] selectionArgs = {remoteId};

        Cursor c = db.query(
                UsersTable.NAME, columns, selection, selectionArgs, null, null, null);

        User user = null;

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();

            String userId = c.getString(c.getColumnIndexOrThrow(UsersTable.Cols.ID));
            String firebaseId = c.getString(c.getColumnIndexOrThrow(UsersTable.Cols.REMOTE_ID));
            String email = c.getString(c.getColumnIndexOrThrow(UsersTable.Cols.EMAIL));
            String password = c.getString(c.getColumnIndexOrThrow(UsersTable.Cols.MASTER_PASSWORD));

            user = new User(email, password);
            user.setUserId(userId);
            user.setUserRemoteId(firebaseId);
        }
        if (c != null) {
            c.close();
        }

        return user;
    }

    @Override
    public void saveCategory(@NonNull Category category) {
        checkNotNull(category);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CategoriesTable.Cols.ID, category.getCategoryId());
        values.put(CategoriesTable.Cols.USER_ID, category.getUserId());
        values.put(CategoriesTable.Cols.IMAGE_NAME, category.getImageName());
        values.put(CategoriesTable.Cols.FIELD_ARRAY_NAME, category.getFieldArrayName());
        values.put(CategoriesTable.Cols.NAME, category.getCategoryName());
        values.put(CategoriesTable.Cols.UPDATE_DATE, category.getUpdateDate());

        db.insert(CategoriesTable.NAME, null, values);
    }

    @Override
    public void saveCredential(@NonNull Credential credential) {
        checkNotNull(credential);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CredentialsTable.Cols.ID, credential.getCredentialId());
        values.put(CredentialsTable.Cols.USER_ID, credential.getUserId());
        values.put(CredentialsTable.Cols.CATEGORY_ID, credential.getCategoryId());
        values.put(CredentialsTable.Cols.TITLE, credential.getTitle());
        values.put(CredentialsTable.Cols.UPDATE_DATE, credential.getUpdateDate());

        db.insert(CredentialsTable.NAME, null, values);
    }

    @Override
    public void saveField(@NonNull Field field) {
        checkNotNull(field);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FieldsTable.Cols.ID, field.getFieldId());
        values.put(FieldsTable.Cols.USER_ID, field.getUserId());
        values.put(FieldsTable.Cols.CREDENTIAL_ID, field.getCredentialId());
        values.put(FieldsTable.Cols.NAME, field.getFieldName());
        values.put(FieldsTable.Cols.TEXT, field.getFieldText());
        values.put(FieldsTable.Cols.UPDATE_DATE, field.getUpdateDate());

        db.insert(FieldsTable.NAME, null, values);
    }

    @Override
    public Credential getCredential(@NonNull String credentialId) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                CredentialsTable.Cols.ID,
                CredentialsTable.Cols.USER_ID,
                CredentialsTable.Cols.CATEGORY_ID,
                CredentialsTable.Cols.TITLE,
                CredentialsTable.Cols.UPDATE_DATE
        };

        String selection = CredentialsTable.Cols.ID + " LIKE ?";
        String[] selectionArgs = {credentialId};

        Cursor c = db.query(
                CredentialsTable.NAME, projection, selection, selectionArgs, null, null, null);

        Credential credential = null;

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();

            String id = c.getString(c.getColumnIndexOrThrow(CredentialsTable.Cols.ID));
            String userId = c.getString(c.getColumnIndexOrThrow(CredentialsTable.Cols.USER_ID));
            String categoryId = c.getString(c.getColumnIndexOrThrow(CredentialsTable.Cols.CATEGORY_ID));
            String title = c.getString(c.getColumnIndexOrThrow(CredentialsTable.Cols.TITLE));
            String date = c.getString(c.getColumnIndexOrThrow(CredentialsTable.Cols.UPDATE_DATE));

            credential = new Credential();
            credential.setCredentialId(id);
            credential.setUserId(userId);
            credential.setCategoryId(categoryId);
            credential.setTitle(title);
            credential.setUpdateDate(date);
        }
        if (c != null) {
            c.close();
        }

        return credential;
    }

    @Override
    public Category getCategory(@NonNull String credentialId) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(CredentialsTable.NAME +
                " LEFT OUTER JOIN " + CategoriesTable.NAME + " ON " +
                CredentialsTable.NAME + "." + CredentialsTable.Cols.CATEGORY_ID + " = " +
                CategoriesTable.NAME + "." + CategoriesTable.Cols.ID);

        String selection = CredentialsTable.NAME + "." + CredentialsTable.Cols.ID + " LIKE ?";
        String[] selectionArgs = {credentialId};

        Cursor c = qb.query(db, null, selection, selectionArgs, null, null, null);

        Category category = null;

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();

            String id = c.getString(c.getColumnIndexOrThrow(CategoriesTable.Cols.ID));
            String userId = c.getString(c.getColumnIndexOrThrow(CategoriesTable.Cols.USER_ID));
            String imageName = c.getString(c.getColumnIndexOrThrow(CategoriesTable.Cols.IMAGE_NAME));
            String fieldArrayName = c.getString(c.getColumnIndexOrThrow(CategoriesTable.Cols.FIELD_ARRAY_NAME));
            String name = c.getString(c.getColumnIndexOrThrow(CategoriesTable.Cols.NAME));
            String date = c.getString(c.getColumnIndexOrThrow(CategoriesTable.Cols.UPDATE_DATE));

            category = new Category();
            category.setCategoryId(id);
            category.setUserId(userId);
            category.setImageName(imageName);
            category.setFieldArrayName(fieldArrayName);
            category.setCategoryName(name);
            category.setUpdateDate(date);
        }
        if (c != null) {
            c.close();
        }

        return category;
    }

    @Override
    public List<Field> getFields(@NonNull String credentialId) {
        List<Field> fields = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] columns = {
                FieldsTable.Cols.ID,
                FieldsTable.Cols.USER_ID,
                FieldsTable.Cols.CREDENTIAL_ID,
                FieldsTable.Cols.NAME,
                FieldsTable.Cols.TEXT,
                FieldsTable.Cols.UPDATE_DATE
        };

        String selection = FieldsTable.Cols.CREDENTIAL_ID + " LIKE ?";
        String[] selectionArgs = {credentialId};

        Cursor c = db.query(
                FieldsTable.NAME, columns, selection, selectionArgs, null, null, null);

        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String id = c.getString(c.getColumnIndexOrThrow(FieldsTable.Cols.ID));
                String userId = c.getString(c.getColumnIndexOrThrow(FieldsTable.Cols.USER_ID));
                String credId = c.getString(c.getColumnIndexOrThrow(FieldsTable.Cols.CREDENTIAL_ID));
                String name = c.getString(c.getColumnIndexOrThrow(FieldsTable.Cols.NAME));
                String text = c.getString(c.getColumnIndexOrThrow(FieldsTable.Cols.TEXT));
                String date = c.getString(c.getColumnIndexOrThrow(FieldsTable.Cols.UPDATE_DATE));

                Field field = new Field();
                field.setFieldId(id);
                field.setUserId(userId);
                field.setCredentialId(credId);
                field.setFieldName(name);
                field.setFieldText(text);
                field.setUpdateDate(date);

                fields.add(field);
            }
        }
        if (c != null) {
            c.close();
        }

        return fields;
    }

    @Override
    public void deleteCredential(String credentialId) {
        checkNotNull(credentialId);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        db.delete(CredentialsTable.NAME, CredentialsTable.Cols.ID + " = ?", new String[]{credentialId});
    }

    @Override
    public void deleteField(String credentialId) {
        checkNotNull(credentialId);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        db.delete(FieldsTable.NAME, FieldsTable.Cols.CREDENTIAL_ID + " = ?", new String[]{credentialId});
        //db.close();
    }

    @Override
    public void updateCredential(Credential credential) {
        checkNotNull(credential);
        String credentialId = credential.getCredentialId();
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CredentialsTable.Cols.USER_ID, credential.getUserId());
        values.put(CredentialsTable.Cols.CATEGORY_ID, credential.getCategoryId());
        values.put(CredentialsTable.Cols.TITLE, credential.getTitle());
        values.put(CredentialsTable.Cols.UPDATE_DATE, new Date().toString());

        db.update(CredentialsTable.NAME, values, CredentialsTable.Cols.ID + " = ?", new String[]{credentialId});
    }

    @Override
    public void updateField(Field field) {
        checkNotNull(field);
        String fieldId = field.getFieldId();

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FieldsTable.Cols.ID, field.getFieldId());
        values.put(FieldsTable.Cols.USER_ID, field.getUserId());
        values.put(FieldsTable.Cols.CREDENTIAL_ID, field.getCredentialId());
        values.put(FieldsTable.Cols.NAME, field.getFieldName());
        values.put(FieldsTable.Cols.TEXT, field.getFieldText());
        values.put(FieldsTable.Cols.UPDATE_DATE, new Date().toString());

        db.update(FieldsTable.NAME, values, FieldsTable.Cols.ID + " = ?", new String[]{fieldId});
    }

    @Override
    public List<Category> getCategories(String userId) {
        List<Category> categories = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] columns = {
                CategoriesTable.Cols.ID,
                CategoriesTable.Cols.USER_ID,
                CategoriesTable.Cols.IMAGE_NAME,
                CategoriesTable.Cols.FIELD_ARRAY_NAME,
                CategoriesTable.Cols.NAME,
                CategoriesTable.Cols.UPDATE_DATE
        };

        String selection = CategoriesTable.Cols.USER_ID + " LIKE ?";
        String[] selectionArgs = {userId};

        Cursor c = db.query(
                CategoriesTable.NAME, columns, selection, selectionArgs, null, null, null);

        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String id = c.getString(c.getColumnIndexOrThrow(CategoriesTable.Cols.ID));
                String catUserId = c.getString(c.getColumnIndexOrThrow(CategoriesTable.Cols.USER_ID));
                String imageName = c.getString(c.getColumnIndexOrThrow(CategoriesTable.Cols.IMAGE_NAME));
                String fieldArrayName = c.getString(c.getColumnIndexOrThrow(CategoriesTable.Cols.FIELD_ARRAY_NAME));
                String name = c.getString(c.getColumnIndexOrThrow(CategoriesTable.Cols.NAME));
                String date = c.getString(c.getColumnIndexOrThrow(CategoriesTable.Cols.UPDATE_DATE));

                Category category = new Category();
                category.setCategoryId(id);
                category.setUserId(catUserId);
                category.setImageName(imageName);
                category.setFieldArrayName(fieldArrayName);
                category.setCategoryName(name);
                category.setUpdateDate(date);

                categories.add(category);
            }
        }
        if (c != null) {
            c.close();
        }

        return categories;
    }

    @Override
    public List<Credential> getCredentials(String categoryId) {
        List<Credential> credentials = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] columns = {
                CredentialsTable.Cols.ID,
                CredentialsTable.Cols.USER_ID,
                CredentialsTable.Cols.CATEGORY_ID,
                CredentialsTable.Cols.TITLE,
                CredentialsTable.Cols.UPDATE_DATE
        };

        String selection = CredentialsTable.Cols.CATEGORY_ID + " LIKE ?";
        String[] selectionArgs = {categoryId};

        Cursor c = db.query(
                CredentialsTable.NAME, columns, selection, selectionArgs, null, null, null);

        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String id = c.getString(c.getColumnIndexOrThrow(CredentialsTable.Cols.ID));
                String userId = c.getString(c.getColumnIndexOrThrow(CredentialsTable.Cols.USER_ID));
                String catId = c.getString(c.getColumnIndexOrThrow(CredentialsTable.Cols.CATEGORY_ID));
                String title = c.getString(c.getColumnIndexOrThrow(CredentialsTable.Cols.TITLE));
                String date = c.getString(c.getColumnIndexOrThrow(CredentialsTable.Cols.UPDATE_DATE));

                Credential credential = new Credential();
                credential.setCredentialId(id);
                credential.setUserId(userId);
                credential.setCategoryId(catId);
                credential.setTitle(title);
                credential.setUpdateDate(date);

                credentials.add(credential);
            }
        }
        if (c != null) {
            c.close();
        }

        return credentials;
    }

    @Override
    public Category getDefaultCategory(String userId, String defaultCategoryName) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                CategoriesTable.Cols.ID,
                CategoriesTable.Cols.USER_ID,
                CategoriesTable.Cols.IMAGE_NAME,
                CategoriesTable.Cols.FIELD_ARRAY_NAME,
                CategoriesTable.Cols.NAME,
                CategoriesTable.Cols.UPDATE_DATE
        };

        String selection = CategoriesTable.Cols.USER_ID + " LIKE ?"
                + " AND " + CategoriesTable.Cols.NAME + " LIKE ?" ;
        String[] selectionArgs = {userId, defaultCategoryName};

        Cursor c = db.query(
                CategoriesTable.NAME, projection, selection, selectionArgs, null, null, null);

        Category category = null;

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();

            String id = c.getString(c.getColumnIndexOrThrow(CategoriesTable.Cols.ID));
            String uId = c.getString(c.getColumnIndexOrThrow(CategoriesTable.Cols.USER_ID));
            String imageName = c.getString(c.getColumnIndexOrThrow(CategoriesTable.Cols.IMAGE_NAME));
            String fieldArrayName = c.getString(c.getColumnIndexOrThrow(CategoriesTable.Cols.FIELD_ARRAY_NAME));
            String name = c.getString(c.getColumnIndexOrThrow(CategoriesTable.Cols.NAME));
            String date = c.getString(c.getColumnIndexOrThrow(CategoriesTable.Cols.UPDATE_DATE));

            category = new Category();
            category.setCategoryId(id);
            category.setUserId(uId);
            category.setImageName(imageName);
            category.setImageName(fieldArrayName);
            category.setCategoryName(name);
            category.setUpdateDate(date);
        }
        if (c != null) {
            c.close();
        }

        return category;
    }

    @Override
    public Category getCategoryById(String categoryId) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                CategoriesTable.Cols.ID,
                CategoriesTable.Cols.USER_ID,
                CategoriesTable.Cols.IMAGE_NAME,
                CategoriesTable.Cols.FIELD_ARRAY_NAME,
                CategoriesTable.Cols.NAME,
                CategoriesTable.Cols.UPDATE_DATE
        };

        String selection = CategoriesTable.Cols.ID + " LIKE ?";
        String[] selectionArgs = {categoryId};

        Cursor c = db.query(
                CategoriesTable.NAME, projection, selection, selectionArgs, null, null, null);

        Category category = null;

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();

            String id = c.getString(c.getColumnIndexOrThrow(CategoriesTable.Cols.ID));
            String uId = c.getString(c.getColumnIndexOrThrow(CategoriesTable.Cols.USER_ID));
            String imageName = c.getString(c.getColumnIndexOrThrow(CategoriesTable.Cols.IMAGE_NAME));
            String fieldArrayName = c.getString(c.getColumnIndexOrThrow(CategoriesTable.Cols.FIELD_ARRAY_NAME));
            String name = c.getString(c.getColumnIndexOrThrow(CategoriesTable.Cols.NAME));
            String date = c.getString(c.getColumnIndexOrThrow(CategoriesTable.Cols.UPDATE_DATE));

            category = new Category();
            category.setCategoryId(id);
            category.setUserId(uId);
            category.setImageName(imageName);
            category.setFieldArrayName(fieldArrayName);
            category.setCategoryName(name);
            category.setUpdateDate(date);
        }
        if (c != null) {
            c.close();
        }

        //db.close();
        return category;
    }

    @Override
    public void updateCategory(Category category) {
        checkNotNull(category);
        String categoryId = category.getCategoryId();

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CategoriesTable.Cols.ID, category.getCategoryId());
        values.put(CategoriesTable.Cols.USER_ID, category.getUserId());
        values.put(CategoriesTable.Cols.IMAGE_NAME, category.getImageName());
        values.put(CategoriesTable.Cols.FIELD_ARRAY_NAME, category.getFieldArrayName());
        values.put(CategoriesTable.Cols.NAME, category.getCategoryName());
        values.put(CategoriesTable.Cols.UPDATE_DATE, new Date().toString());

        db.update(CategoriesTable.NAME, values, CategoriesTable.Cols.ID + " = ?", new String[]{categoryId});

    }

    @Override
    public void deleteCategory(String categoryId) {
        checkNotNull(categoryId);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        db.delete(CategoriesTable.NAME, CategoriesTable.Cols.ID + " = ?", new String[]{categoryId});
    }

    @Override
    public void deleteFieldById(String fieldId) {
        checkNotNull(fieldId);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        db.delete(FieldsTable.NAME, FieldsTable.Cols.ID + " = ?", new String[]{fieldId});
    }
}
