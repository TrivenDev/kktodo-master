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
import com.xuexiang.templateproject.core.BaseFragment;
import com.xuexiang.templateproject.databinding.FragmentForgetBinding;
import com.xuexiang.templateproject.mysqlAdapter.Service.UserService;
import com.xuexiang.templateproject.utils.EmailSendUtil;
import com.xuexiang.templateproject.utils.SettingUtils;
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

import javax.mail.MessagingException;


/**
 * 忘记密码界面
 *
 * @author wen
 * @since 2024.12.23
 */
@Page(anim = CoreAnim.slide)
public class ForgetFragment extends BaseFragment<FragmentForgetBinding> implements View.OnClickListener {

    private View mJumpView;
    private int code;

    private CountDownButtonHelper mCountDownHelper;


    @NonNull
    @Override
    protected FragmentForgetBinding viewBindingInflate(LayoutInflater inflater, ViewGroup container) {
        return FragmentForgetBinding.inflate(inflater, container, false);
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
        mCountDownHelper = new CountDownButtonHelper(binding.btnGetVerifyCodeForget, 60);
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
        binding.btnGetVerifyCodeForget.setOnClickListener(this);
        binding.btnForgetOk.setOnClickListener(this);
    }

    private void refreshButton(boolean isChecked) {
        ViewUtils.setEnabled(binding.btnForgetOk, isChecked);
        ViewUtils.setEnabled(mJumpView, isChecked);
    }

    private void handleSubmitPrivacy() {
        SettingUtils.setIsAgreePrivacy(true);
        UMengInit.init();
        // 应用市场不让默认勾选
//        ViewUtils.setChecked(cbProtocol, true);
    }

    class ForgetThread extends Thread implements Runnable{
        boolean flag;

        @Override
        public void run() {
            flag = rePassword(binding.etPhoneNumberForget.getEditValue(), binding.forgetNewPassword.getEditValue());
        }

        public boolean getFlag() {
            return flag;
        }
    }
    @SingleClick
    @Override
    public void onClick(View v) {//识别不同的按钮
        int id = v.getId();
        if (id == R.id.btn_get_verify_code_forget) {//获取验证码
            if (binding.etPhoneNumberForget.validate()) {
                code = getVerifyCode(binding.etPhoneNumberForget.getEditValue());
                new Thread(()->{
                    try {
                        System.out.println(binding.etPhoneNumberForget.getEditValue());
                        new EmailSendUtil().send(code,binding.etPhoneNumberForget.getEditValue());
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        } else if (id == R.id.btn_forget_ok) {//登录按钮
            if (binding.etPhoneNumberForget.validate()) {
                if (binding.forgetVerifyCode.validate()) {
                    if(binding.forgetNewPassword.validate()){
                        if(binding.forgetVerifyCode.getEditValue().equals(code+"")){
                            ForgetThread forget = new ForgetThread();
                            forget.start();
                            try {
                                forget.join();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            boolean success = forget.getFlag();
                            if (success) {
                                popToBack();
                            } else {
                                XToastUtils.error("该用不存在");
                            }
                        }else {
                            XToastUtils.error("验证码错误");
                        }
                    }
                }
            }
        }

    }
    /**
     * 获取验证码
     */
    private int getVerifyCode(String phoneNumber) {
        mCountDownHelper.start();
        return (int)((Math.random()*8999)+1000);
    }
    /**
     * 根据验证码登录
     *
     * @param phoneNumber 手机号
     */
    private boolean rePassword(String phoneNumber, String password) {
        return new UserService().updatePassword(phoneNumber,password);
    }

    @Override
    public void onDestroyView() {
        if (mCountDownHelper != null) {
            mCountDownHelper.recycle();
        }
        super.onDestroyView();
    }
}