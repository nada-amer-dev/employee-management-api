package com.example.employeemanagement;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

	private final EmployeeRepository employeeRepository;

	public EmployeeService(EmployeeRepository employeeRepository) {
		this.employeeRepository = employeeRepository;

	}

	public Employee createEmployee(Employee employee) {

		if (employeeRepository.findByEmail(employee.getEmail()).isPresent()) {
			throw new EmployeeAlreadyExistsException("Email already exists:" + employee.getEmail());
		}
		return employeeRepository.save(employee);
	}

	public List<Employee> getAllEmployees() {

		return employeeRepository.findAll();

	}

	public Employee getEmployeeById(Long id) {

		return employeeRepository.findById(id)
				.orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));

	}

	public Employee updateEmployee(Long id, Employee updated) {
		Employee existing = getEmployeeById(id);

		Optional<Employee> sameEmail = employeeRepository.findByEmail(updated.getEmail());
		if (sameEmail.isPresent() && !sameEmail.get().getId().equals(id)) {
			throw new EmployeeAlreadyExistsException("Email already exists: " + updated.getEmail());
		}

		existing.setFirstName(updated.getFirstName());
		existing.setLastName(updated.getLastName());
		existing.setEmail(updated.getEmail());
		existing.setDepartment(updated.getDepartment());
		existing.setSalary(updated.getSalary());
		return employeeRepository.save(existing);
	}

	public void deleteEmployee(Long id) {
		if (!employeeRepository.existsById(id)) {
			throw new EmployeeNotFoundException("Employee not found with id: " + id);
		}
		employeeRepository.deleteById(id);
	}
}
