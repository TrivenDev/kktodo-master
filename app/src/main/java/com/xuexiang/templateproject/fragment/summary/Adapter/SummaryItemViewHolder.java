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

package com.xuexiang.templateproject.fragment.summary.Adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xuexiang.templateproject.R;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.layout.XUILinearLayout;
/**
 * 对XML文件的组件进行映射
 * */

public class SummaryItemViewHolder extends RecyclerView.ViewHolder {
    RadiusImageView head;
    TextView nickname;
    TextView title;
    TextView text;
    TextView editTime;
    XUILinearLayout total;
    View itemView;

    public SummaryItemViewHolder(@NonNull View itemView) {
        super(itemView);
        head = itemView.findViewById(R.id.summary_head);
        nickname = itemView.findViewById(R.id.summary_nickname);
        title = itemView.findViewById(R.id.summary_title);
        text = itemView.findViewById(R.id.summary_text);
        editTime = itemView.findViewById(R.id.summary_edit_time);
        total = itemView.findViewById(R.id.summary_total);
        this.itemView = itemView;
    }
}