# Test Strategy Document

## 1. Introduction

This document outlines the testing strategy for the Banking System project, covering all testing methodologies required by the CSE341 term project specification.

## 2. Test Scope

### In Scope
- Account operations (deposit, withdraw, transfer)
- State transitions (Unverified → Verified → Suspended → Closed)
- GUI functionality and user interactions
- Input validation
- Credit Score feature (TDD)
- Integration between components

### Out of Scope
- Performance/load testing
- Security penetration testing
- Database persistence (using in-memory storage)

## 3. Testing Methodologies

### 3.1 Black-Box Testing (Section A)
**Approach**: Test system behavior based on requirements without knowledge of internal implementation.

**Techniques Used**:
- Equivalence Partitioning (EP)
- Boundary Value Analysis (BVA)

**Test Classes**:
- `AccountBlackBoxTest.java` (23 tests)

**Coverage Areas**:
| Function | Valid Classes | Invalid Classes | Boundary Values |
|----------|---------------|-----------------|-----------------|
| Deposit  | amount > 0    | amount ≤ 0      | 0.01, 0, -0.01  |
| Withdraw | 0 < amt ≤ bal | amt > bal       | balance, bal+1  |
| Transfer | verified acc  | non-verified    | exact balance   |

### 3.2 White-Box Testing (Section B)
**Approach**: Test internal logic paths and achieve high code coverage.

**Techniques Used**:
- Statement Coverage
- Branch/Decision Coverage
- Path Coverage
- Condition Coverage

**Test Classes**:
- `TransactionServiceWhiteBoxTest.java` (28 tests)

**Coverage Targets**:
- Statement Coverage: 100%
- Branch Coverage: 100%
- All decision paths in validateTransaction()

### 3.3 State-Based Testing (Section D)
**Approach**: Test all state transitions and state-dependent behaviors.

**Test Classes**:
- `AccountStateTest.java` (22 tests)

**State Transition Matrix**:
| Current State | Action   | Next State | Tested |
|---------------|----------|------------|--------|
| Unverified    | verify() | Verified   | ✓      |
| Verified      | suspend()| Suspended  | ✓      |
| Suspended     | appeal() | Verified   | ✓      |
| Any           | close()  | Closed     | ✓      |

### 3.4 UI Testing (Section C)
**Approach**: Test GUI functionality, input validation, and user experience.

**Test Classes**:
- `GUIComponentTest.java` (21 tests) - Unit-level GUI logic
- `BankingSeleniumTest.java` (24 tests) - Browser automation

**Areas Covered**:
- Button state management
- Input validation
- Status display and colors
- Notification messages
- Accessibility considerations

### 3.5 Integration Testing
**Approach**: Test interaction between system components.

**Test Classes**:
- `IntegrationTest.java` (16 tests)

**Integration Points**:
- Controller ↔ Service
- Service ↔ DAO
- End-to-end workflows

### 3.6 Test-Driven Development (Section E)
**Approach**: Red-Green-Refactor cycle for Credit Score feature.

**Test Classes**:
- `CreditScoreTest.java` - RED phase (disabled)
- `CreditScoreGreenPhaseTest.java` - GREEN phase (11 tests)

**Feature**: Client Credit Score Check
- Initial score: 700
- Score range: 300-850
- Transaction limits based on score

## 4. Test Environment

### Prerequisites
- Java JDK 17+
- Apache Maven 3.6+
- Chrome Browser (for Selenium tests)

### Dependencies
- JUnit 5.10.0
- Selenium WebDriver 4.15.0
- WebDriverManager 5.6.2
- JaCoCo (code coverage)

## 5. Test Execution

### Run All Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=BankingSeleniumTest
```

### Generate Coverage Report
```bash
mvn test jacoco:report
```

## 6. Exit Criteria

| Criterion | Target | Actual |
|-----------|--------|--------|
| Test Pass Rate | 100% | 100% |
| Code Coverage | >80% | ~95% |
| Critical Bugs | 0 | 0 |

## 7. Risks and Mitigations

| Risk | Mitigation |
|------|------------|
| Selenium driver compatibility | Using WebDriverManager |
| Flaky UI tests | Headless mode, explicit waits |
| State pollution between tests | @BeforeEach reset |
