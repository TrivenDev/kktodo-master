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

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.xuexiang.templateproject.R;
import com.xuexiang.templateproject.activity.MainActivity;
import com.xuexiang.templateproject.core.BaseFragment;
import com.xuexiang.templateproject.databinding.FragmentLoginBinding;
import com.xuexiang.templateproject.mysqlAdapter.Entry.User;
import com.xuexiang.templateproject.mysqlAdapter.Service.UserService;
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


/**
 * 登录页面
 *
 * @author wen
 * @since 2024.12.23
 */
@Page(anim = CoreAnim.none)
public class LoginFragment extends BaseFragment<FragmentLoginBinding> implements View.OnClickListener {

    private View mJumpView;
    private CountDownButtonHelper mCountDownHelper;

    @NonNull
    @Override
    protected FragmentLoginBinding viewBindingInflate(LayoutInflater inflater, ViewGroup container) {
        return FragmentLoginBinding.inflate(inflater, container, false);
    }

    @Override
    protected TitleBar initTitle() {
        TitleBar titleBar = super.initTitle()
                .setImmersive(true);
        titleBar.setBackgroundColor(Color.TRANSPARENT);
        titleBar.setTitle("");
        titleBar.setLeftImageDrawable(ResUtils.getVectorDrawable(getContext(), R.drawable.ic_login_close));
        titleBar.setActionTextColor(ThemeUtils.resolveColor(getContext(), R.attr.colorAccent));
        mJumpView = titleBar.addAction(new TitleBar.TextAction(R.string.title_jump_login) {
            @Override
            public void performAction(View view) {
                onLoginSuccess();
            }
        });
        return titleBar;
    }

    @Override
    protected void initViews() {
        //mCountDownHelper = new CountDownButtonHelper(binding.btnGetVerifyCode, 60);
        //隐私政策弹窗
        if (!SettingUtils.isAgreePrivacy()) {
            Utils.showPrivacyDialog(getContext(), (dialog, which) -> {
                dialog.dismiss();
                handleSubmitPrivacy();
            });
        }
        boolean isAgreePrivacy = SettingUtils.isAgreePrivacy();
        binding.cbProtocol.setChecked(isAgreePrivacy);
        refreshButton(isAgreePrivacy);
        binding.cbProtocol.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SettingUtils.setIsAgreePrivacy(isChecked);
            refreshButton(isChecked);
        });
    }

    @Override
    protected void initListeners() {
        binding.btnLogin.setOnClickListener(this);
        binding.signUser.setOnClickListener(this);
        binding.tvForgetPassword.setOnClickListener(this);
        binding.tvUserProtocol.setOnClickListener(this);
        binding.tvPrivacyProtocol.setOnClickListener(this);
    }

    private void refreshButton(boolean isChecked) {
        ViewUtils.setEnabled(binding.btnLogin, isChecked);
        ViewUtils.setEnabled(mJumpView, isChecked);
    }

    private void handleSubmitPrivacy() {
        SettingUtils.setIsAgreePrivacy(true);
        UMengInit.init();
        // 应用市场不让默认勾选
//        ViewUtils.setChecked(cbProtocol, true);
    }

    class LoginThread extends Thread implements Runnable{
        User user;

        @Override
        public void run() {
            user = loginByVerifyCode(binding.etPhoneNumber.getEditValue(), binding.etVerifyCode.getEditValue());
        }

        public User getUser() {
            return user;
        }
    }

    @SingleClick
    @Override
    public void onClick(View v) {//识别不同的按钮
        int id = v.getId();
        if (id == R.id.btn_login) {//登录按钮
            if (binding.etPhoneNumber.validate()) {
                if (binding.etVerifyCode.validate()) {
                    LoginThread login = new LoginThread();
                    login.start();
                    try {
                        login.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    User user = login.getUser();
                    if(user == null||user.getUsername()==null){
                        XToastUtils.error("用户不存在");
                    }else if(!user.getPassword().equals(binding.etVerifyCode.getEditValue())){
                        XToastUtils.error("密码错误");
                    }else {
                        onLoginSuccess();
                    }
                }
            }
        } else if (id == R.id.sign_user) {//其他登录方式
            openPage(SignFragment.class);
        } else if (id == R.id.tv_forget_password) {//忘记密码
            openPage(ForgetFragment.class);
        } else if (id == R.id.tv_user_protocol) {//用户协议
            Utils.gotoProtocol(this, false, true);
        } else if (id == R.id.tv_privacy_protocol) {//隐私政策
            Utils.gotoProtocol(this, true, true);
        }

    }

    /**
     * 获取验证码
     */
    private void getVerifyCode(String phoneNumber) {
        // TODO: 2020/8/29 这里只是界面演示而已
        XToastUtils.warning("只是演示，验证码请随便输");
        mCountDownHelper.start();
    }

    /**
     * 根据验证码登录
     *
     * @param phoneNumber 手机号
     */
    private User loginByVerifyCode(String phoneNumber, String verifyCode) {
        UserService userService = new UserService();
        return userService.SelectByUsername(phoneNumber);
    }

    /**
     * 登录成功的处理
     */
    private void onLoginSuccess() {
        String token = RandomUtils.getRandomNumbersAndLetters(16);
        if (TokenUtils.handleLoginSuccess(token)) {
            popToBack();//弹出栈顶的Fragment。如果Activity中只有一个Fragment时，Activity也退出
            ActivityUtils.startActivity(MainActivity.class);
        }
    }

    @Override
    public void onDestroyView() {
        if (mCountDownHelper != null) {
            mCountDownHelper.recycle();
        }
        super.onDestroyView();
    }
}

