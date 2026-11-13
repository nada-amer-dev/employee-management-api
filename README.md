# Employee Management API

A REST API for managing employees, built with **Spring Boot 4** and **Java 21**. It supports full CRUD, input validation, unique emails, clear JSON error responses, and unit tests for the business logic.

## Features

- Create, read, update and delete employees
- Bean Validation on every request (names, email format, department, salary)
- Unique email addresses, enforced in the service and in the database
- Global exception handling with clear `400`, `404` and `409` responses
- Layered design: controller, service, repository
- Unit tests with JUnit 5 and Mockito

## Tech stack

| Area | Technology |
| --- | --- |
| Language | Java 21 |
| Framework | Spring Boot 4.1 (Spring MVC) |
| Persistence | Spring Data JPA, Hibernate, H2 (in-memory) |
| Validation | Jakarta Bean Validation |
| Testing | JUnit 5, Mockito |
| Build | Maven (wrapper included) |

## Project structure

| Class | Responsibility |
| --- | --- |
| `EmployeeManagementApplication` | Starts the application |
| `Employee` | JPA entity with validation rules |
| `EmployeeRepository` | Database access, including `findByEmail` |
| `EmployeeService` | Business rules: unique email, not-found handling, update logic |
| `EmployeeController` | REST endpoints under `/employees` |
| `EmployeeNotFoundException` | Thrown when an id does not exist (404) |
| `EmployeeAlreadyExistsException` | Thrown when an email is already used (409) |
| `GlobalExceptionHandler` | Converts exceptions into JSON error responses |
| `EmployeeServiceTest` | Unit tests for the service |

## Getting started

### Requirements

- JDK 21
- No database setup needed: the app uses an in-memory H2 database

### Run

```bash
git clone https://github.com/nada-amer-dev/employee-management-api.git
cd employee-management-api
./mvnw spring-boot:run
```

On Windows use `mvnw spring-boot:run`.

The API starts on **http://localhost:8081**. Data is kept in memory and is reset every time the app restarts.

### Run the tests

```bash
./mvnw test
```

## API reference

Base path: `/employees`

| Method | Path | Description | Success |
| --- | --- | --- | --- |
| `POST` | `/employees` | Create an employee | `201 Created` |
| `GET` | `/employees` | List all employees | `200 OK` |
| `GET` | `/employees/{id}` | Get one employee | `200 OK` |
| `PUT` | `/employees/{id}` | Update an employee | `200 OK` |
| `DELETE` | `/employees/{id}` | Delete an employee | `204 No Content` |

### Employee fields

| Field | Type | Rules |
| --- | --- | --- |
| `id` | number | Generated automatically |
| `firstName` | string | Required, not blank |
| `lastName` | string | Required, not blank |
| `email` | string | Required, valid email format, unique |
| `department` | string | Required, not blank |
| `salary` | decimal | Required, greater than 0 |

### Error responses

| Situation | Status |
| --- | --- |
| A field fails validation | `400 Bad Request` |
| Employee id does not exist | `404 Not Found` |
| Email already belongs to another employee | `409 Conflict` |

## Examples

### Create an employee

```http
POST /employees
Content-Type: application/json

{
  "firstName": "Sara",
  "lastName": "Ali",
  "email": "sara@mail.com",
  "department": "Engineering",
  "salary": 5000
}
```

Response `201 Created`:

```json
{
  "id": 1,
  "firstName": "Sara",
  "lastName": "Ali",
  "email": "sara@mail.com",
  "department": "Engineering",
  "salary": 5000.00
}
```

### Validation error (`400`)

```json
{
  "firstName": "First Name cannot be blank"
}
```

### Duplicate email (`409`)

```json
{
  "error": "Email already exists: sara@mail.com"
}
```

### Employee not found (`404`)

```json
{
  "error": "Employee not found with id: 999"
}
```

## Design notes

- **Separation of concerns.** The controller handles HTTP, the service holds the rules, the repository handles the database. Each layer can be changed or tested on its own.
- **Validation at the edge.** `@Valid` on the request body rejects bad input with a `400` before the service runs. The service only handles business rules.
- **Safe updates.** `updateEmployee` loads the existing employee first, so a missing id returns `404` and the primary key is kept. It then copies the new values onto it, so Hibernate updates the row instead of inserting a new one.
- **Email uniqueness.** An email is a conflict only if it belongs to a *different* employee, so an employee can update other fields while keeping their own email. The database column is also `unique` as a second line of defence.
- **Exact money type.** Salary uses `BigDecimal`, not `double`, to avoid floating-point rounding errors.
- **Centralised errors.** One `@RestControllerAdvice` class maps exceptions to responses, so controllers contain no try/catch.

## Tests

`EmployeeServiceTest` uses Mockito to replace the repository, so the tests need no database and no Spring context. They cover:

- Get by id: employee found, and id not found (`404`)
- Create: email free (saved), email already used (`409`, nothing saved)
- Update: success with new values saved
- Update: email belongs to another employee (`409`, nothing saved)
- Update: employee keeps their own email (allowed)

## Possible next steps

- Switch from H2 to PostgreSQL or MySQL, with connection details in environment variables
- Use separate request and response DTOs instead of exposing the entity
- Add pagination and filtering (for example by department)
- Add controller tests with `MockMvc`
- Add a GitHub Actions workflow that runs the tests on every push
