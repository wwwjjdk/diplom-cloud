package com.example.refactordip.controller;

import com.example.refactordip.exception.FileInputException;
import com.example.refactordip.exception.InsideException;
import com.example.refactordip.exception.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MyExceptionHandler {

    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public String HandlerUnauthorized (UnauthorizedException ex) {
        return ex.getMessage();
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(FileInputException.class)
    public String HandlerFileInputException(FileInputException ex){
        return  ex.getMessage();
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(InsideException.class)
    public String HandlerInsideException(InsideException ex){
        return ex.getMessage();
    }
}
