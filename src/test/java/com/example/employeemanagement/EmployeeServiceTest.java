package com.example.employeemanagement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
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

	@Test
	void throwsNotFoundWhenIdDoesNotExist() {

		when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

		// Act and Assert

		assertThrows(EmployeeNotFoundException.class, () -> employeeService.getEmployeeById(99L));

		// Verify
		verify(employeeRepository).findById(99L);

	}

	@Test
	void employeeUpdateSucceeds() {
		// Arrange:
		Employee newValue = new Employee();
		newValue.setId(1L);
		newValue.setFirstName("NewFirst");
		newValue.setLastName("NewLast");
		newValue.setEmail("New@gmail.com");
		newValue.setDepartment("Engineering");
		newValue.setSalary((new BigDecimal("75000.00")));

		Employee oldValues = new Employee();
		oldValues.setId(1L);
		oldValues.setFirstName("OldFirst");
		oldValues.setLastName("OldLast");
		oldValues.setEmail("Old@gmail.com");
		oldValues.setDepartment("Engineering");
		oldValues.setSalary((new BigDecimal("120000.00")));

		when(employeeRepository.findById(newValue.getId())).thenReturn(Optional.of(oldValues));
		when(employeeRepository.findByEmail(newValue.getEmail())).thenReturn(Optional.empty());
		when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> inv.getArgument(0));

		// Act
		Employee result = employeeService.updateEmployee(oldValues.getId(), newValue);

		// Assert

		assertNotNull(result);
		assertEquals(1L, (long) result.getId());
		assertEquals("NewFirst", result.getFirstName());
		assertEquals("NewLast", result.getLastName());
		assertEquals("New@gmail.com", result.getEmail());
		assertEquals("Engineering", result.getDepartment());
		assertEquals(new BigDecimal("75000.00"), result.getSalary());

		// verify
		verify(employeeRepository).findById(newValue.getId());
		verify(employeeRepository).findByEmail(newValue.getEmail());
		verify(employeeRepository).save(oldValues);

	}

	@Test
	void updateEmployeeFailsWhenEmailBelongsToAnotherEmployee() {

		Employee oldValues = new Employee();
		oldValues.setId(1L);
		oldValues.setFirstName("oldFirst");
		oldValues.setLastName("oldLast");
		oldValues.setEmail("old@gmail.com");
		oldValues.setDepartment("Engineering");
		oldValues.setSalary((new BigDecimal("120000.00")));

		Employee other = new Employee();
		other.setId(2L);
		other.setFirstName("otherFirst");
		other.setLastName("otherLast");
		other.setEmail("taken@gmail.com");
		other.setDepartment("Engineering");
		other.setSalary((new BigDecimal("75000.00")));

		Employee newValue = new Employee();
		newValue.setId(1L);
		newValue.setFirstName("oldFirst");
		newValue.setLastName("oldLast");
		newValue.setEmail("taken@gmail.com");
		newValue.setDepartment("Engineering");
		newValue.setSalary((new BigDecimal("120000.00")));

		when(employeeRepository.findById(oldValues.getId())).thenReturn(Optional.of(oldValues));
		when(employeeRepository.findByEmail(other.getEmail())).thenReturn(Optional.of(other));

		// Act & Assert

		assertThrows(EmployeeAlreadyExistsException.class, () -> employeeService.updateEmployee(1L, newValue));

		// verify
		verify(employeeRepository, never()).save(any());

	}

	@Test
	void updateEmployySucceedsWhenEmployeeKeepsTheirEmail() {

		Employee oldValues = new Employee();
		oldValues.setId(1L);
		oldValues.setFirstName("oldFirst");
		oldValues.setLastName("oldLast");
		oldValues.setEmail("old@gmail.com");
		oldValues.setDepartment("Engineering");
		oldValues.setSalary((new BigDecimal("120000.00")));

		Employee newValue = new Employee();
		newValue.setId(1L);
		newValue.setFirstName("oldFirst");
		newValue.setLastName("oldLast");
		newValue.setEmail("old@gmail.com");
		newValue.setDepartment("Engineering");
		newValue.setSalary((new BigDecimal("200000.00")));

		when(employeeRepository.findByEmail(newValue.getEmail())).thenReturn(Optional.of(oldValues));
		when(employeeRepository.findById(1L)).thenReturn(Optional.of(oldValues));
		when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> inv.getArgument(0));

		// Act
		Employee result = employeeService.updateEmployee(1L, newValue);

		// Assert
		assertEquals(new BigDecimal("200000.00"), result.getSalary());

		// verify

		verify(employeeRepository).save(oldValues);

	}

}
