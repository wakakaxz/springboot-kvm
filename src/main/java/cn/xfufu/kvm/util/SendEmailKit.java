package cn.xfufu.kvm.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Random;

/**
 * @author Xz
 * 邮件发送
 */
@Component
public class SendEmailKit {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${mail.fromMail.addr}")
    private String from;

    /**
     * 发送普通文件
     *
     * @param to      接受人
     * @param subject 主题
     * @param content 验证码
     * @return flag
     */
    public Boolean sendCommonEmail(String to, String subject, String content) {
        content = "您的验证码为: " + content + ", 若不是本人在操作, 请无视!";
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        try {
            mailSender.send(message);
        } catch (MailException e) {
            return false;
        }
        return true;
    }

    /**
     * 发送HTML文件
     *
     * @param to      接收人
     * @param subject 主题
     * @param content 验证码内容
     * @return flag
     */
    public Boolean sendHTMLEmail(String to, String subject, String content) {

        content = "<html><body>" +
                "您的验证码为: <span style=\"color:red\"> " +
                content +
                "</span>, 若不是本人在操作, 请无视!" +
                "</body></html>" + " ";
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            return false;
        }
        return true;
    }
}
