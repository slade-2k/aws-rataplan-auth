package de.iks.rataplan.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import de.iks.rataplan.domain.ErrorCode;
import de.iks.rataplan.exceptions.Error;
import de.iks.rataplan.exceptions.RataplanAuthException;

@ControllerAdvice
public class RataplanAuthExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(RataplanAuthException.class)
	public ResponseEntity<Error> rataplanAuthException(RataplanAuthException e) {
		Error error = new Error(e.getErrorCode(), e.toString());
		return new ResponseEntity<Error>(error, e.getHttpStatus());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Error> genericException(Exception e) {
		Error error = new Error(ErrorCode.UNEXPECTED_ERROR, e.toString());
		return new ResponseEntity<Error>(error, HttpStatus.BAD_REQUEST);
	}
}
