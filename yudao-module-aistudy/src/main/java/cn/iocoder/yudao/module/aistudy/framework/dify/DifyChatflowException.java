package cn.iocoder.yudao.module.aistudy.framework.dify;

public class DifyChatflowException extends RuntimeException {

    public DifyChatflowException(String message) {
        super(message);
    }

    public DifyChatflowException(String message, Throwable cause) {
        super(message, cause);
    }
}
