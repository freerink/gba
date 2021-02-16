package com.reerinkresearch.lo3pl;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.reerinkresearch.lo3pl.model.PLException;

@ControllerAdvice
public class ExceptionAdvice {

	@ResponseBody
	@ExceptionHandler(PLException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	String invalidAnummerHandler(PLException ex) {
		return ex.getMessage();
	}

}
