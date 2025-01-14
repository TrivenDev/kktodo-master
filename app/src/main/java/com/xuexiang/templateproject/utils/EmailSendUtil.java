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

package com.xuexiang.templateproject.utils;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSendUtil {
    public void send(int code,String toEmail) throws MessagingException {
        //使用JavaMail发送邮件的5个步骤
        //1、创建 Session 对象
        Properties prop = new Properties();
        //设置QQ邮件服务器
        prop.setProperty("mail.host", "smtp.qq.com");
        // 邮件发送协议
        prop.setProperty("mail.transport.protocol", "smtp");
        // 需要验证用户名密码
        prop.setProperty("mail.smtp.auth", "true");
        Session session = Session.getDefaultInstance(prop);

        //开启Session的debug模式，这样就可以查看到程序发送Email的运行状态
        session.setDebug(true);

        //2、通过session得到transport对象
        Transport ts = session.getTransport();

        //3、使用邮箱的用户名和授权码连上SMTP邮件服务器，即登陆
        ts.connect("smtp.163.com", "Lmysgczzxhxh@163.com", "JXBHOFYFAJZLHMGS");

        //4、创建邮件对象MimeMessage——点击网页上的写信

        //创建一个邮件对象
        MimeMessage message = new MimeMessage(session);
        //指明邮件的发件人——在网页上填写发件人
        message.setFrom(new InternetAddress("Lmysgczzxhxh@163.com"));
        //指明邮件的收件人，现在发件人和收件人是一样的，那就是自己给自己发——在网页上填写收件人
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
        //邮件的标题
        message.setSubject("【K.K.Todo】|验证码");
        String verifyCode = "<h3 style=\"display: inline\">【K.K.Todo】你正在进行账号验证，验证码为</h3>\n" +
                "<a href=\"#\" style=\"display: inline;font-size: 35px\">"+code+"</a>\n" +
                "<h3 style=\"display: inline\">，请勿将验证码泄露于他人，本条验证码有效期1分钟</h3>";
        //邮件的文本内容
        message.setContent(verifyCode, "text/html;charset=UTF-8");

        //5、发送邮件
        ts.sendMessage(message, message.getAllRecipients());

        //6、关闭连接对象，即关闭服务器上的连接资源
        ts.close();
    }
}
