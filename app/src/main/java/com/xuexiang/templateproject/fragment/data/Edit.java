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
 * 用于传递数据的对象类
 * */
public class Edit {
    String category;
    int finish;
    String things;
    int fire;
    long id;
    String deadline;
    String beginTime;
    int repeat;

    public Edit(long id,String category, int finish, String things, int fire, String deadline, String beginTime, int repeat) {
        this.id = id;
        this.category = category;
        this.finish = finish;
        this.things = things;
        this.fire = fire;
        this.deadline = deadline;
        this.beginTime = beginTime;
        this.repeat = repeat;
    }

    public Edit() {

    }

    public String getCategory() {
        return category;
    }

    public int getFinish() {
        return finish;
    }

    public String getThings() {
        return things;
    }

    public long getId() {
        return id;
    }

    public int getFire() {
        return fire;
    }

    public String getDeadline() {
        return deadline;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public int getRepeat() {
        return repeat;
    }
}
