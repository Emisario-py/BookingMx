# BookingMx

Minimal vanilla JS + Spring Boot project to practice unit tests.

## Run backend
```bash
cd backend
mvn spring-boot:run
```

## Run frontend
```bash
cd frontend
npm i
npm run serve
# http://localhost:5173
```

---

# BookingMx
## 📦 Reservations & City Graph System

A multi-module project combining a Java Reservation Management API with a JavaScript Graph Engine for city-distance calculations.
This project demonstrates clean architecture, validation logic, and complete unit testing across both ecosystems (JUnit 5 and Jest).

---

### 🚀 Project Overview

This project is composed of two main parts:

1. Reservation Management Module (Java)

A small backend domain that manages hotel reservations.
It includes:

- Creating, updating, listing, and canceling reservations

- Validation of dates (null checks, chronological order, past-date prevention)

- In-memory repository and service logic

- Custom exceptions and centralized exception handling

- Full unit test coverage (JUnit 5)

2. City Graph Module (JavaScript)

A lightweight graph implementation used for:

- Representing cities and distances

- Building graphs dynamically from datasets

- Validating input datasets

- Querying nearby cities based on distance

- Fully tested logic using Jest

These two modules serve as training examples for backend logic, graph algorithms, and automated testing best practices.

---

### 🛠️ Installation & Setup
#### Prerequisites

Make sure you have installed:

- Java 17+

- Maven (or Gradle, if you adapt the project)

- Node.js 18+

- npm or yarn

---
### 📌 1. Java Module Setup (Reservations)
#### Clone the repository
```bash
git clone https://github.com/Emisario-py/BookingMx.git
cd project
```
#### Compile the project
```bash
mvn clean install
```
#### Run all Java tests
```bash
mvn test
```
If successful, you should see an output similar to:
```yaml
[INFO] Results:
[INFO] Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```
---

### 📌 2. JavaScript Module Setup (City Graph)
#### Install dependencies
```bash
npm install
```
#### Run Jest tests
```bash
npm test
```
Typical successful output:
```pgsql
 PASS  graph.test.js
  Graph class
    ✓ addCity should add new cities (5 ms)
    ✓ addEdge should reject invalid distances
  validateGraphData
    ✓ should validate correct dataset
  getNearbyCities
    ✓ should return nearby cities sorted by distance

Test Suites: 1 passed, 1 total
Tests:       15 passed, 15 total
```
---

### 🧪 Tests Included
This project includes two groups of unit tests, implemented over two development sprints.

---

#### ✅ Sprint 1 – Java (JUnit 5)
Covers the entire reservation system:

| Class Tested                | Description                                          |
|-----------------------------|------------------------------------------------------|
| `ReservationTest`           | Model behavior, constructors, equality, status logic |
| `ReservationRepositoryTest` | In-memory CRUD operations, ID assignment             |
| `ReservationServiceTest`    | Business logic, validation, exceptions               |
| `ApiExceptionHandlerTest`   | HTTP mapping of custom exceptions                    |
| `ReservationControllerTest` | Mapping between service and controller responses     |

Example test command:
```bash
mvn test
```
---
#### ✅ Sprint 2 – JavaScript (Jest)
Covers the graph system:

| File Tested     | Description                                                |
|-----------------|------------------------------------------------------------|
| `graph.test.js` | Graph class behavior, validation utilities, city filtering |

The tests check:

- Adding valid/invalid cities

- Adding edges and rejecting invalid distances

- Detecting unknown cities

- Validating datasets (duplicates, missing data, invalid edges)

- Sorting nearby cities

- Rejecting invalid graph objects

#### Example Jest command:
```bash
npm test
```
---
### 📊 Example Output Snippets
#### Java Test Example
```bash
ReservationServiceTest > create_ShouldThrow_WhenDatesAreNull PASSED
ReservationRepositoryTest > delete_ShouldNotFail PASSED
ApiExceptionHandlerTest > badRequest_ShouldReturn400 PASSED

BUILD SUCCESS
```
#### JavaScript Test Example
```bash
Graph class ✓ addCity should add new cities
Graph class ✓ neighbors should fail for unknown city
validateGraphData ✓ should fail for duplicate cities
getNearbyCities ✓ should respect maxDistance
```
---

### 📘 Summary

This repository provides:

- A complete, tested Java domain module

- A clean, modular JavaScript graph engine

- Full unit testing coverage using JUnit 5 and Jest

- Good code practices: exceptions, validation, repository patterns, and data structures

- Documented and readable test suites

It serves as an excellent learning foundation for:

- Clean backend design

- Test-driven development (TDD)

- Cross-language module structuring

- Graph algorithms