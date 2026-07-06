package cn.iocoder.yudao.module.system.service.auth;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.AUTH_LOGIN_CAPTCHA_CODE_ERROR;

@Service
@Slf4j
public class EmailCodeServiceImpl implements EmailCodeService {

    private static final String REDIS_KEY_PREFIX = "email_code:";
    private static final long CODE_EXPIRE_MINUTES = 5;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Value("${yudao.email.smtp-host:smtp.qq.com}")
    private String smtpHost;

    @Value("${yudao.email.smtp-port:587}")
    private int smtpPort;

    @Value("${yudao.email.username:3265629648@qq.com}")
    private String emailUsername;

    @Value("${yudao.email.password:gesbijdcaggjciej}")
    private String emailPassword;

    @Override
    public void sendEmailCode(String email) {
        String code = RandomUtil.randomNumbers(6);

        stringRedisTemplate.opsForValue().set(
                REDIS_KEY_PREFIX + email,
                code,
                CODE_EXPIRE_MINUTES,
                TimeUnit.MINUTES
        );

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(smtpHost);
        mailSender.setPort(smtpPort);
        mailSender.setUsername(emailUsername);
        mailSender.setPassword(emailPassword);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", smtpHost);
        props.put("mail.debug", "false");

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailUsername);
            message.setTo(email);
            message.setSubject("AI伴学平台 - 邮箱验证码");
            message.setText("您的验证码是：" + code + "，有效期 " + CODE_EXPIRE_MINUTES + " 分钟。请勿泄露给他人。");
            mailSender.send(message);
            log.info("[sendEmailCode][邮箱({}) 验证码发送成功]", email);
        } catch (Exception e) {
            stringRedisTemplate.delete(REDIS_KEY_PREFIX + email);
            log.error("[sendEmailCode][邮箱({}) 验证码发送失败]", email, e);
            throw exception(AUTH_LOGIN_CAPTCHA_CODE_ERROR);
        }
    }

    @Override
    public void validateEmailCode(String email, String code) {
        String storedCode = stringRedisTemplate.opsForValue().get(REDIS_KEY_PREFIX + email);
        if (storedCode == null || !storedCode.equals(code)) {
            throw exception(AUTH_LOGIN_CAPTCHA_CODE_ERROR);
        }
        stringRedisTemplate.delete(REDIS_KEY_PREFIX + email);
    }

}