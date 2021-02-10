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

	@ResponseBody
	@ExceptionHandler(BadRequestException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	String invalidAnummerHandler(BadRequestException ex) {
		return ex.getMessage();
	}

	@ResponseBody
	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String invalidAnummerHandler(NotFoundException ex) {
		return ex.getMessage();
	}

	@ResponseBody
	@ExceptionHandler(AlreadyExistsException.class)
	@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
	String invalidAnummerHandler(AlreadyExistsException ex) {
		return ex.getMessage();
	}
}
