package com.app.eventrade.s3uploadservice.exception;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.app.eventrade.s3uploadservice.DTO.Response;

@RestControllerAdvice
public class ExceptionHandlerController {

	private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerController.class);
	private static LocalDateTime timeStamp = LocalDateTime.now();

	@ExceptionHandler(value = { Exception.class, RuntimeException.class })
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseEntity<Response> defaultErrorHandler(Exception exception) {
		logger.error(exception.toString());
		Response response = new Response();
		response.setResponse(exception.getMessage());response.setTimeStamp(timeStamp);
		response.setStatus(HttpStatus.OK.value());	
		response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@ExceptionHandler(value = { CustomException.class })
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseEntity<Response> CustomHandler(CustomException exception) {
		logger.error(exception.toString());
		Response response = new Response();
		response.setTimeStamp(timeStamp);
		response.setStatus(exception.getStatus());		
		response.setCode(exception.getCode());
		response.setResponse(exception.getMessage());
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
