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

package com.lihacovs.android.beepass.data.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.UUID;

/**
 * Model class for Field entity
 */

public class Field {

    @NonNull
    private String mFieldId;

    @NonNull
    private String mUserId;

    @NonNull
    private String mCredentialId;

    @Nullable
    private String mFieldName;

    @Nullable
    private String mFieldText;

    @Nullable
    private String mUpdateDate;

    //Used to create initial category object
    public Field() {
        this("Empty Credential Id");
    }

    public Field(@NonNull String credentialId){
        this(credentialId, "Empty User Id");
    }

    public Field(@NonNull String credentialId, @NonNull String userId){
        this(credentialId, userId, null, null);
    }

    //Main constructor used in app to create Field
    public Field(@NonNull String credentialId,
                 @NonNull String userId,
                 @Nullable String fieldName,
                 @Nullable String fieldText){
        mFieldId = UUID.randomUUID().toString();
        mCredentialId = credentialId;
        mUserId = userId;
        mFieldName = fieldName;
        mFieldText = fieldText;
    }

    @NonNull
    public String getFieldId() {
        return mFieldId;
    }

    public void setFieldId(@NonNull String fieldId) {
        mFieldId = fieldId;
    }

    @NonNull
    public String getUserId() {
        return mUserId;
    }

    public void setUserId(@NonNull String userId) {
        mUserId = userId;
    }

    @NonNull
    public String getCredentialId() {
        return mCredentialId;
    }

    public void setCredentialId(@NonNull String credentialId) {
        mCredentialId = credentialId;
    }

    @Nullable
    public String getFieldName() {
        return mFieldName;
    }

    public void setFieldName(@Nullable String fieldName) {
        mFieldName = fieldName;
    }

    @Nullable
    public String getFieldText() {
        return mFieldText;
    }

    public void setFieldText(@Nullable String fieldText) {
        mFieldText = fieldText;
    }

    @Nullable
    public String getUpdateDate() {
        return mUpdateDate;
    }

    public void setUpdateDate(@Nullable String updateDate) {
        mUpdateDate = updateDate;
    }
}
