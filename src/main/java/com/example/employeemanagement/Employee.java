package com.example.employeemanagement;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "First Name cannot be blank")
	@Column(nullable = false)
	private String firstName;

	@NotBlank(message = "Last Name cannot be blank")
	@Column(nullable = false)
	private String lastName;

	@NotBlank(message = "Email cannot be blank")
	@Email(message = "Please provide a valid email address format")
	@Column(nullable = false, unique = true)
	private String email;

	@NotBlank(message = "Department cannot be blank")
	@Column(nullable = false)
	private String department;

	@NotNull(message = "Salary cannot be null")
	@Positive(message = "Salary must be greater than 0")
	private BigDecimal salary;

	public Employee() {

	}

	public Employee(Long id, String firstName, String lastName, String email, String department, BigDecimal salary) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.department = department;
		this.salary = salary;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public BigDecimal getSalary() {
		return salary;
	}

	public void setSalary(BigDecimal salary) {
		this.salary = salary;
	}

}
