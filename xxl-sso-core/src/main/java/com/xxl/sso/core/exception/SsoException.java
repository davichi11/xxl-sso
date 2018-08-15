package com.xxl.sso.core.exception;

/**
 * @author xuxueli 2018-04-02 21:01:41
 */
public class SsoException extends RuntimeException {

    private static final long serialVersionUID = 42L;

    public SsoException(String msg) {
        super(msg);
    }

    public SsoException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public SsoException(Throwable cause) {
        super(cause);
    }

}
