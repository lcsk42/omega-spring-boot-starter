package com.lcsk42.frameworks.starter.message.mail.util;

import com.lcsk42.frameworks.starter.core.ApplicationContextHolder;
import com.lcsk42.frameworks.starter.core.constant.StringConstant;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MailUtil {

    private static class Holder {
        public static final JavaMailSenderImpl INSTANCE =
                ApplicationContextHolder.getBean(JavaMailSenderImpl.class);
    }

    private static JavaMailSenderImpl getJavaMailSender() {
        return Holder.INSTANCE;
    }


    /**
     * 发送文本邮件给单个人
     *
     * @param subject 主题
     * @param content 内容
     * @param to 收件人
     * @throws MessagingException /
     */
    public static void sendText(String to, String subject, String content)
            throws MessagingException {
        send(splitAddress(to), null, null, subject, content, false);
    }


    /**
     * 发送文本邮件给单个人
     *
     * @param subject 主题
     * @param content 内容
     * @param tos 收件人列表
     * @throws MessagingException /
     */
    public static void sendText(Collection<String> tos, String subject, String content)
            throws MessagingException {
        send(tos, null, null, subject, content, false);
    }

    /**
     * 发送文本邮件给单个人
     *
     * @param subject 主题
     * @param content 内容
     * @param tos 收件人列表
     * @throws MessagingException /
     */
    public static void sendText(Collection<String> tos,
            Collection<String> ccs,
            Collection<String> bccs,
            String subject, String content) throws MessagingException {
        send(tos, ccs, bccs, subject, content, false);
    }


    /**
     * 发送文本邮件给单个人
     *
     * @param subject 主题
     * @param content 内容
     * @param tos 收件人列表
     * @throws MessagingException /
     */
    public static void sendText(Collection<String> tos,
            Collection<String> ccs,
            String subject, String content) throws MessagingException {
        send(tos, ccs, null, subject, content, false);
    }


    /**
     * 发送 HTML 邮件给单个人
     *
     * @param subject 主题
     * @param content 内容
     * @param to 收件人
     * @throws MessagingException /
     */
    public static void sendHtml(String to, String subject, String content)
            throws MessagingException {
        send(splitAddress(to), null, null, subject, content, true);
    }

    /**
     * 发送 HTML 邮件给单个人
     *
     * @param subject 主题
     * @param content 内容
     * @param to 收件人
     * @param files 附件列表
     * @throws MessagingException /
     */
    public static void sendHtml(String to, String subject, String content, File... files)
            throws MessagingException {
        send(splitAddress(to), null, null, subject, content, true, files);
    }

    /**
     * 发送 HTML 邮件给多个人
     *
     * @param subject 主题
     * @param content 内容
     * @param tos 收件人列表
     * @param files 附件列表
     * @throws MessagingException /
     */
    public static void sendHtml(Collection<String> tos,
            String subject,
            String content,
            File... files) throws MessagingException {
        send(tos, null, null, subject, content, true, files);
    }

    /**
     * 发送 HTML 邮件给多个人
     *
     * @param subject 主题
     * @param content 内容
     * @param tos 收件人列表
     * @param ccs 抄送人列表
     * @param files 附件列表
     * @throws MessagingException /
     */
    public static void sendHtml(Collection<String> tos,
            Collection<String> ccs,
            String subject,
            String content,
            File... files) throws MessagingException {
        send(tos, ccs, null, subject, content, true, files);
    }

    /**
     * 发送 HTML 邮件给多个人
     *
     * @param subject 主题
     * @param content 内容
     * @param tos 收件人列表
     * @param ccs 抄送人列表
     * @param bccs 密送人列表
     * @param files 附件列表
     * @throws MessagingException /
     */
    public static void sendHtml(Collection<String> tos,
            Collection<String> ccs,
            Collection<String> bccs,
            String subject,
            String content,
            File... files) throws MessagingException {
        send(tos, ccs, bccs, subject, content, true, files);
    }

    /**
     * 发送邮件给多个人
     *
     * @param tos 收件人列表
     * @param ccs 抄送人列表
     * @param bccs 密送人列表
     * @param subject 主题
     * @param content 内容
     * @param html 是否是 HTML
     * @param files 附件列表
     * @throws MessagingException /
     */
    public static void send(Collection<String> tos,
            Collection<String> ccs,
            Collection<String> bccs,
            String subject,
            String content,
            boolean html,
            File... files) throws MessagingException {

        Validate.isTrue(CollectionUtils.isNotEmpty(tos), "请至少指定一名收件人");

        JavaMailSenderImpl mailSender = getJavaMailSender();
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        // 创建邮件发送器
        MimeMessageHelper messageHelper = new MimeMessageHelper(
                mimeMessage,
                true,
                StandardCharsets.UTF_8.displayName());

        // 设置基本信息
        messageHelper.setFrom(StringUtils.defaultIfBlank(
                mailSender.getJavaMailProperties().getProperty("mail.from"),
                mailSender.getUsername()));
        messageHelper.setSubject(subject);
        messageHelper.setText(content, html);

        // 设置收信人
        // 抄送人
        if (CollectionUtils.isNotEmpty(ccs)) {
            messageHelper.setCc(ccs.toArray(String[]::new));
        }
        // 密送人
        if (CollectionUtils.isNotEmpty(bccs)) {
            messageHelper.setBcc(bccs.toArray(String[]::new));
        }
        // 收件人
        messageHelper.setTo(tos.toArray(String[]::new));
        // 设置附件
        if (ArrayUtils.isNotEmpty(files)) {
            for (File file : files) {
                messageHelper.addAttachment(file.getName(), file);
            }
        }
        // 发送邮件
        mailSender.send(mimeMessage);
    }

    /**
     * 将多个联系人转为列表，分隔符为逗号或者分号
     *
     * @param addresses 多个联系人，如果为空返回null
     * @return 联系人列表
     */
    private static List<String> splitAddress(String addresses) {
        if (StringUtils.isBlank(addresses)) {
            return List.of();
        }

        // 定义分隔符优先级
        String[] delimiters = {StringConstant.COMMA, StringConstant.SEMICOLON};

        for (String delimiter : delimiters) {
            if (StringUtils.contains(addresses, delimiter)) {
                return List.of(StringUtils.split(addresses, delimiter));
            }
        }

        return List.of(addresses.trim());
    }
}
