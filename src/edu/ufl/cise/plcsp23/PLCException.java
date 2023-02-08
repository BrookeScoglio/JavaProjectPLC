package edu.ufl.cise.plcsp23;

public class PLCException extends Exception {
    public PLCException(String message) {
        super(message);
    }

    public PLCException(Throwable cause) {
        super(cause);
    }

    public PLCException(String error_message, int line, int column) {
        super(line + ":" + column + "  " + error_message);
    }
}

