package com.example.employeemanagement;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class EmployeeAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public EmployeeAlreadyExistsException(String message) {
		super(message);
	}

}
