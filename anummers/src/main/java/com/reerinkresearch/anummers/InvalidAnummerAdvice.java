package com.reerinkresearch.anummers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class InvalidAnummerAdvice {

	@ResponseBody
	@ExceptionHandler(InvalidAnummerException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String invalidAnummerHandler(InvalidAnummerException ex) {
		return ex.getMessage();
	}
}
