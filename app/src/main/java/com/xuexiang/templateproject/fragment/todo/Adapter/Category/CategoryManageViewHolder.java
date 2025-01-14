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

package com.xuexiang.templateproject.fragment.todo.Adapter.Category;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.xuexiang.templateproject.R;
import com.xuexiang.xui.widget.button.SmoothCheckBox;
import com.xuexiang.xui.widget.button.shadowbutton.ShadowButton;
import com.xuexiang.xui.widget.textview.supertextview.SuperButton;

public class CategoryManageViewHolder extends RecyclerView.ViewHolder{
    AppCompatImageView categoryColor;
    TextView categoryName;
    ShadowButton categoryDelete;
    View itemView;


    public CategoryManageViewHolder(@NonNull View itemView) {
        super(itemView);
        categoryDelete = itemView.findViewById(R.id.category_delete);
        categoryColor = itemView.findViewById(R.id.category_color);
        categoryName = itemView.findViewById(R.id.category_name);
        this.itemView = itemView;
    }
}
