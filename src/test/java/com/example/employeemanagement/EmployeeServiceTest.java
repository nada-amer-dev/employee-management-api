package com.example.employeemanagement;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {
	@Mock
	private EmployeeRepository employeeRepository;

	@InjectMocks
	private EmployeeService employeeService;

	@Test
	void returnsEmployeeWhenIdExists() {
		// Arrange: an employee, and tell the fake repository to return it

		Employee employee = new Employee();
		employee.setId(1L);
		when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

		// Act: call the service

		Employee result = employeeService.getEmployeeById(1L);

		// Assert: same employee returned

		assertSame(employee, result);
		verify(employeeRepository).findById(1L);
	}

	@Test
	void createsEmployeeWhenEmailIsNotTaken() {

		// Arrange:an employee with an email
		Employee employee = new Employee();
		employee.setEmail("nada@hotmail.com");
		when(employeeRepository.findByEmail("nada@hotmail.com")).thenReturn(Optional.empty());
		when(employeeRepository.save(employee)).thenReturn(employee);

		// Act
		Employee result = employeeService.createEmployee(employee);

		// Assert

		assertSame(employee, result);
		verify(employeeRepository).save(employee);

	}

	@Test
	void createEmployeeFailsOnDuplicateEmail() {

		Employee existing = new Employee();
		existing.setEmail("nada@hotmail.com");

		Employee newEmployee = new Employee();
		newEmployee.setEmail("nada@hotmail.com");

		when(employeeRepository.findByEmail("nada@hotmail.com")).thenReturn(Optional.of(existing));

		// Act and Assert

		assertThrows(EmployeeAlreadyExistsException.class, () -> employeeService.createEmployee(newEmployee));

		// Verify
		verify(employeeRepository, never()).save(any());

	}

}
