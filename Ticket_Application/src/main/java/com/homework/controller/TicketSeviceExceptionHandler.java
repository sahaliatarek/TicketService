package com.homework.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.homework.Constants;
/**
 * catch NumberFormatExceptionHandler 
 * @author sahaltxpjf
 *
 */
@ControllerAdvice
class TicketSeviceExceptionHandler {
    @ExceptionHandler(NumberFormatException.class)
    public String NumberFormatExceptionHandler(HttpServletRequest request) {
    	request.getSession().setAttribute("NotValid", Constants.VALIDATION_MESSAGE);
    	return "index";
    }
    
}