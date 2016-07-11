package com.dogzz.pim.exception;

/**
 * Exception to be threw when it is impossible to connect to the source
 * Created by afon on 02.07.2016.
 */
public class SourceConnectException extends Exception {
    public SourceConnectException() {
        super();
    }

    public SourceConnectException(String message) {
        super(message);
    }

    public SourceConnectException(String message, Throwable cause) {
        super(message, cause);
    }

    public SourceConnectException(Throwable cause) {
        super(cause);
    }
}
