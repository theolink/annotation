package com.tiansi.annotation.exception;

public class TiansiException extends Exception {
    private static final long serialVersionUID = 1L;

    private int errorCode;

    public TiansiException() {
    }

    public TiansiException(String message) {
        super(message);
    }
    public TiansiException(int errorCode,String message) {
        super(message);
        this.errorCode=errorCode;
    }
    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public void printStackTrace() {
        System.out.println("errorCode: " + this.errorCode);
        super.printStackTrace();
    }
}
