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

package com.xuexiang.templateproject.fragment.todo.Adapter.RecycleBin;

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

public class RecycleBinViewHolder extends RecyclerView.ViewHolder{
    SuperButton recycleCategory;
    SmoothCheckBox recycleFinish;
    TextView recycleThings;
    TextView recycle;
    TextView recycleCategoryName;
    ShadowButton recycleDelete;
    ProgressBar recyclePercent;
    View itemView;
    
    
    public RecycleBinViewHolder(@NonNull View itemView) {
        super(itemView);
        recycleCategory = itemView.findViewById(R.id.recycle_category);
        recycleFinish = itemView.findViewById(R.id.recycle_finish);
        recycleCategoryName = itemView.findViewById(R.id.recycle_category_name);
        recycleThings = itemView.findViewById(R.id.recycle_things);
        recycle = itemView.findViewById(R.id.recycle);
        recycleDelete = itemView.findViewById(R.id.recycle_delete);
        recyclePercent = itemView.findViewById(R.id.recycle_percent);
        this.itemView = itemView;
    }
}
