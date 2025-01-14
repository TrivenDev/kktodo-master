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

package com.xuexiang.templateproject.fragment.todo.Adapter.Repeat;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xuexiang.templateproject.R;
import com.xuexiang.xui.widget.button.SmoothCheckBox;
import com.xuexiang.xui.widget.button.SwitchIconView;
import com.xuexiang.xui.widget.button.shadowbutton.ShadowButton;
import com.xuexiang.xui.widget.textview.supertextview.SuperButton;


public class RepeatViewHolder extends RecyclerView.ViewHolder {
    SuperButton todoCategory;
    TextView todoThings;
    TextView todoCategoryName;
    ShadowButton todoEdit;
    ShadowButton todoDelete;
    ProgressBar todoPercent;
    View itemView;

    public RepeatViewHolder(@NonNull View itemView) {
        super(itemView);
        todoCategory = itemView.findViewById(R.id.repeat_category);
        todoCategoryName = itemView.findViewById(R.id.repeat_category_name);
        todoThings = itemView.findViewById(R.id.repeat_things);
        todoEdit = itemView.findViewById(R.id.repeat_edit);
        todoDelete = itemView.findViewById(R.id.repeat_delete);
        todoPercent = itemView.findViewById(R.id.repeat_percent);
        this.itemView = itemView;
    }
}