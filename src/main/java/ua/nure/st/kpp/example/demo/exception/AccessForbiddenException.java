package ua.nure.st.kpp.example.demo.exception;

/**
 * @author Stanislav Hlova
 */
public class AccessForbiddenException extends RuntimeException {
    public AccessForbiddenException() {
    }

    public AccessForbiddenException(String message) {
        super(message);
    }

}
