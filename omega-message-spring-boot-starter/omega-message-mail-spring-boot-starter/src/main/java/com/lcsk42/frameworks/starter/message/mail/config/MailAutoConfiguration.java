package com.lcsk42.frameworks.starter.message.mail.config;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Slf4j
@AllArgsConstructor
@EnableConfigurationProperties({MailConfiguration.class})
public class MailAutoConfiguration {

    @Bean
    JavaMailSenderImpl mailSender(MailConfiguration properties) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setProtocol(properties.getProtocol());
        Validate.notBlank(properties.getHost(), "邮件配置不正确：服务器地址不能为空");
        sender.setHost(properties.getHost());
        Validate.notNull(properties.getPort(), "邮件配置不正确：邮件配置不正确：服务器端口不能为空");
        sender.setPort(properties.getPort());
        Validate.notBlank(properties.getUsername(), "邮件配置不正确：用户名不能为空");
        sender.setUsername(properties.getUsername());
        Validate.notBlank(properties.getPassword(), "邮件配置不正确：密码不能为空");
        sender.setPassword(properties.getPassword());
        sender.setDefaultEncoding(properties.getDefaultEncoding().name());

        Properties javaMailProperties = properties.getJavaMailProperties();
        javaMailProperties.put("mail.from", properties.getFrom());
        javaMailProperties.put("mail.smtp.auth", true);
        if (properties.getSslEnabled()) {
            javaMailProperties.put("mail.smtp.ssl.enable", "true");
            javaMailProperties.put("mail.smtp.ssl.protocols", "TLSv1.2");
            javaMailProperties.put("mail.smtp.ssl.trust", properties.getHost());
        } else {
            javaMailProperties.put("mail.smtp.ssl.enable", "false");
            javaMailProperties.put("mail.smtp.starttls.enable", "false");
        }
        sender.setJavaMailProperties(javaMailProperties);

        return sender;
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("[Omega] - Auto Configuration 'Mail' completed initialization.");
    }
}
