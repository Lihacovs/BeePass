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

/**
 * DB class to define structure for SQLite schema
 */

class AppDbSchema {

    static final class UsersTable{
        static final String NAME = "users";

        // we can access column like: CredentialsTable.Cols.NAME
        static final class Cols{
            static final String ID = "id";
            static final String REMOTE_ID = "remote_id";
            static final String EMAIL = "email";
            static final String MASTER_PASSWORD = "master_password";
        }
    }

    static final class CategoriesTable{
        static final String NAME = "categories";

        static final class Cols{
            static final String ID = "id";
            static final String USER_ID = "user_id";
            static final String IMAGE_NAME = "image_name";
            static final String FIELD_ARRAY_NAME = "field_array_name";
            static final String NAME = "name";
            static final String UPDATE_DATE = "update_date";
        }
    }

    static final class CredentialsTable{
        static final String NAME = "credentials";

        static final class Cols{
            static final String ID = "id";
            static final String USER_ID = "user_id";
            static final String CATEGORY_ID = "category_id";
            static final String TITLE = "title";
            static final String UPDATE_DATE = "update_date";
        }
    }

    static final class FieldsTable {
        static final String NAME = "fields";

        static final class Cols {
            static final String ID = "id";
            static final String USER_ID = "user_id";
            static final String CREDENTIAL_ID = "credential_id";
            static final String NAME = "name";
            static final String TEXT = "text";
            static final String UPDATE_DATE = "update_date";
        }
    }
}
