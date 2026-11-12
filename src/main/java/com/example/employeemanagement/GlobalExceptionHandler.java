package com.example.employeemanagement;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();

		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			errors.put(error.getField(), error.getDefaultMessage());
		}

		return ResponseEntity.badRequest().body(errors);
	}

	@ExceptionHandler(EmployeeAlreadyExistsException.class)
	public ResponseEntity<Map<String, String>> handleAlreadyExists(EmployeeAlreadyExistsException ex) {
		Map<String, String> body = Map.of("error", ex.getMessage());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
	}

	@ExceptionHandler(EmployeeNotFoundException.class)
	public ResponseEntity<Map<String, String>> handleNotFound(EmployeeNotFoundException ex) {
		Map<String, String> body = Map.of("error", ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
	}
}
