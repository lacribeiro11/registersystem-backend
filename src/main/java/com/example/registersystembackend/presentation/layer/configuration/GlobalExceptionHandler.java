package com.example.registersystembackend.presentation.layer.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@ControllerAdvice
class GlobalExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            ConstraintViolationException.class,
            IllegalArgumentException.class,
            MethodArgumentTypeMismatchException.class,
            DuplicateKeyException.class,
            HttpMessageNotReadableException.class
    })
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    GlobalExceptionResponse handleBadRequestException(Exception ex) {
        LOG.warn("User did something wrong", ex);
        return new GlobalExceptionResponse(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        LOG.warn("User did something wrong", ex);
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fe -> errors.put(fe.getField(), fe.getDefaultMessage()));
        return errors;
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    GlobalExceptionResponse handleNotFound(NoSuchElementException ex) {
        LOG.warn("No such element", ex);
        return new GlobalExceptionResponse(ex.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    GlobalExceptionResponse handleExceptionInternal(HttpRequestMethodNotSupportedException ex) {
        LOG.warn("The user made an unsupported call", ex);
        return new GlobalExceptionResponse(ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    GlobalExceptionResponse handleExceptionInternal(RuntimeException ex) {
        LOG.error("Something went wrong", ex);
        return new GlobalExceptionResponse();
    }

}
