package ua.nure.st.kpp.example.demo.exception.handlers;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ua.nure.st.kpp.example.demo.exception.AccessForbiddenException;

/**
 * @author Stanislav Hlova
 */
@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(AccessForbiddenException.class)
    public String errorPageForAccessForbidden(AccessForbiddenException accessForbiddenException){
        return "exception/accessForbidden";
    }
}
