/*
 * Copyright (C) 2021 xuexiangjys(xuexiangjys@163.com)
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

package com.xuexiang.templateproject.fragment.other;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.xuexiang.templateproject.R;
import com.xuexiang.templateproject.activity.MainActivity;
import com.xuexiang.templateproject.core.BaseFragment;
import com.xuexiang.templateproject.databinding.FragmentLoginBinding;
import com.xuexiang.templateproject.databinding.FragmentSignBinding;
import com.xuexiang.templateproject.mysqlAdapter.Entry.User;
import com.xuexiang.templateproject.mysqlAdapter.Service.UserService;
import com.xuexiang.templateproject.utils.EmailSendUtil;
import com.xuexiang.templateproject.utils.RandomUtils;
import com.xuexiang.templateproject.utils.SettingUtils;
import com.xuexiang.templateproject.utils.TokenUtils;
import com.xuexiang.templateproject.utils.Utils;
import com.xuexiang.templateproject.utils.XToastUtils;
import com.xuexiang.templateproject.utils.sdkinit.UMengInit;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xui.utils.CountDownButtonHelper;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xui.utils.ThemeUtils;
import com.xuexiang.xui.utils.ViewUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xutil.app.ActivityUtils;

import javax.mail.MessagingException;


/**
 * 登录页面
 *
 * @author wen
 * @since 2024.12.23
 */
@Page(anim = CoreAnim.slide)
public class SignFragment extends BaseFragment<FragmentSignBinding> implements View.OnClickListener {

    private View mJumpView;
    private int code = 0;
    private CountDownButtonHelper mCountDownHelper;


    @NonNull
    @Override
    protected FragmentSignBinding viewBindingInflate(LayoutInflater inflater, ViewGroup container) {
        return FragmentSignBinding.inflate(inflater, container, false);
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle()
                .setImmersive(true);
        titleBar.setBackgroundColor(Color.TRANSPARENT);
        titleBar.setTitle("");
        titleBar.setLeftImageDrawable(ResUtils.getVectorDrawable(getContext(), R.drawable.ic_login_close));
        titleBar.setActionTextColor(ThemeUtils.resolveColor(getContext(), R.attr.colorAccent));
        return titleBar;
    }

    @Override
    protected void initViews() {
        mCountDownHelper = new CountDownButtonHelper(binding.btnGetVerifyCodeSign, 60);
        //隐私政策弹窗
        if (!SettingUtils.isAgreePrivacy()) {
            Utils.showPrivacyDialog(getContext(), (dialog, which) -> {
                dialog.dismiss();
                handleSubmitPrivacy();
            });
        }
        boolean isAgreePrivacy = SettingUtils.isAgreePrivacy();
        refreshButton(isAgreePrivacy);
    }

    @Override
    protected void initListeners() {
        binding.btnGetVerifyCodeSign.setOnClickListener(this);
        binding.btnSign.setOnClickListener(this);
    }

    private void refreshButton(boolean isChecked) {
        ViewUtils.setEnabled(binding.btnSign, isChecked);
        ViewUtils.setEnabled(mJumpView, isChecked);
    }

    private void handleSubmitPrivacy() {
        SettingUtils.setIsAgreePrivacy(true);
        UMengInit.init();
        // 应用市场不让默认勾选
        // ViewUtils.setChecked(cbProtocol, true);
    }

    class SignThread extends Thread implements Runnable{
        boolean flag;

        @Override
        public void run() {
            flag = signByPhoneNumber(binding.signPhoneNumber.getEditValue(), binding.signPassword.getEditValue());
        }

        public boolean getFlag() {
            return flag;
        }
    }

    @SingleClick
    @Override
    public void onClick(View v) {//识别不同的按钮
        int id = v.getId();
        if (id == R.id.btn_sign) {//登录按钮
            if (binding.signPhoneNumber.validate()) {
                if (binding.signVerifyCode.validate()) {
                    if(binding.signVerifyCode.getEditValue().equals(code+"")){
                        if (binding.signPassword.validate()) {
                            SignThread sign = new SignThread();
                            sign.start();
                            try {
                                sign.join();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            boolean success = sign.getFlag();
                            if (success) {
                                XToastUtils.success("注册成功，请登录");
                                popToBack();
                            } else {
                                XToastUtils.error("注册失败，您已注册");
                            }
                        }
                    }else {
                        XToastUtils.error("验证码错误");
                    }
                }
            }
        }else if (id == R.id.btn_get_verify_code_sign) {//获取验证码
            if (binding.signPhoneNumber.validate()) {
                code = getVerifyCode(binding.signPhoneNumber.getEditValue());
                new Thread(()->{
                    try {
                        System.out.println(binding.signPhoneNumber.getEditValue());
                        new EmailSendUtil().send(code,binding.signPhoneNumber.getEditValue());
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }

    }
    /**
     * 获取随机验证码(1000-9999)
     */
    private int getVerifyCode(String emailAddress) {
        mCountDownHelper.start();
        return (int)((Math.random()*8999)+1000);
    }
    /**
     * 根据账户注册登录
     *
     * @param email 手机号
     */
    private boolean signByPhoneNumber(String email, String verifyCode) {
        UserService userService = new UserService();
        return userService.signByEmail(email,verifyCode);
    }


    @Override
    public void onDestroyView() {
        if (mCountDownHelper != null) {
            mCountDownHelper.recycle();
        }
        super.onDestroyView();
    }
}

