package ir.proprog.enrollassist.domain.exception;

import java.util.function.Supplier;

public class BusinessException extends Exception implements Supplier<Exception> {
    private String message = null;
    private Object brokenObject;

    public BusinessException() {
        super();
    }

    public BusinessException(String message, Object brokenObject) {
        super(message);
        this.message = message;
        this.brokenObject = brokenObject;
    }

    @Override
    public Exception get() {
        return new Exception(message);
    }
}
