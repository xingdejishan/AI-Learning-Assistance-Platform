package cn.iocoder.yudao.module.system.service.auth;

public interface EmailCodeService {

    /**
     * 发送邮箱验证码
     *
     * @param email 邮箱
     */
    void sendEmailCode(String email);

    /**
     * 校验邮箱验证码
     *
     * @param email 邮箱
     * @param code  验证码
     */
    void validateEmailCode(String email, String code);

}