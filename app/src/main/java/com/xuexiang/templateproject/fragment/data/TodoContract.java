/*
 * Copyright (C) 2022 xuexiangjys(xuexiangjys@163.com)
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
 *
 */

package com.xuexiang.templateproject.fragment.data;

import android.provider.BaseColumns;


/**
 * 定义数据库表项名及表名
 * */
public class TodoContract {
    private TodoContract() {
    }

    public class TodoEntry implements BaseColumns {
        public static final String TABLE_NAME = "TODOITEM";
        public static final String COLUMN_CATEGORY = "TODO_CATEGORY";
        public static final String COLUMN_FINISH= "TODO_FINISH";
        public static final String COLUMN_THINGS = "TODO_THINGS";
        public static final String COLUMN_FIRE = "TODO_FIRE";
        public static final String COLUMN_DELETE = "TODO_DELETE";
        public static final String COLUMN_BEGIN_TIME = "TODO_BEGIN_TIME";
        public static final String COLUMN_DEADLINE = "TODO_DEADLINE";
        public static final String COLUMN_REPEAT = "TODO_REPEAT";
        public static final String COLUMN_HANDLE_TIME = "TODO_HANDLE_TIME";
    }

    public class Category implements BaseColumns {
        public static final String TABLE_NAME = "CATEGORYITEM";
        public static final String COLUMN_CATEGORY_COLOR = "CATEGORY_COLOR";
        public static final String COLUMN_CATEGORY_NAME = "CATEGORY_NAME";
    }


    public class Summary implements BaseColumns {
        public static final String TABLE_NAME = "SUMMARYITEM";
        public static final String COLUMN_SUMMARY_HEAD = "SUMMARY_HEAD";
        public static final String COLUMN_SUMMARY_NICKNAME = "SUMMARY_NICKNAME";
        public static final String COLUMN_SUMMARY_TITLE = "SUMMARY_TITLE";
        public static final String COLUMN_SUMMARY_TEXT = "SUMMARY_TEXT";
        public static final String COLUMN_SUMMARY_EDIT_TIME = "SUMMARY_EDIT_TIME";
    }

    public class CompletionRate implements BaseColumns {
        public static final String TABLE_NAME = "COMPLETION_RATE";
        public static final String COLUMN_DAY1 = "DAY1";
        public static final String COLUMN_DAY2 = "DAY2";
        public static final String COLUMN_DAY3 = "DAY3";
        public static final String COLUMN_DAY4 = "DAY4";
        public static final String COLUMN_DAY5 = "DAY5";
        public static final String COLUMN_DAY6 = "DAY6";
        public static final String COLUMN_DAY7 = "DAY7";
        public static final String COLUMN_UPDATE_TIME = "UPDATE_TIME";
    }

    public static int REPEAT_NONE = 0;
    public static int REPEAT_DAY = 1;
    public static int REPEAT_WEEK= 2;
    public static int REPEAT_MONTH = 3;
}
