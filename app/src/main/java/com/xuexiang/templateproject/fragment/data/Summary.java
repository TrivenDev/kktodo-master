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


/**
 * 用户信息传递类
 * */
public class Summary {
    String head;
    String nickname;
    String title;
    String text;
    String editTime;
    long id;

    public Summary(long id,String head, String nickname, String title, String text, String editTime) {
        this.head = head;
        this.nickname = nickname;
        this.title = title;
        this.text = text;
        this.editTime = editTime;
        this.id = id;
    }

    public Summary() {

    }

    public String getHead() {
        return head;
    }

    public String getNickname() {
        return nickname;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getEditTime() {
        return editTime;
    }
}
