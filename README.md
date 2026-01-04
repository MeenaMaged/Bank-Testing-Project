# Banking System Testing Project

## Software Testing and Validation - CSE341
### Fall 2025 Term Project

---

## Project Overview

This project implements comprehensive testing for a simplified banking software system. The testing suite covers all required methodologies including black-box, white-box, UI, state-based, integration testing, and Test-Driven Development (TDD).

### Team Members
- **Meena Maged Abdo Mekhaiel** - ID: 1900694
- **Muhammad Yassin Hassan Mohy Eldeen** - ID: 23P0394

---

## Project Structure

```
Bank Testing project/
├── src/
│   ├── main/java/com/banking/
│   │   ├── controllers/
│   │   │   └── ClientController.java
│   │   ├── dao/
│   │   │   └── AccountDAO.java
│   │   ├── entities/
│   │   │   ├── Account.java
│   │   │   └── CreditScoreAccount.java
│   │   ├── gui/
│   │   │   └── BankingGUI.java
│   │   └── services/
│   │       ├── AccountService.java
│   │       └── TransactionService.java
│   └── test/java/com/banking/
│       ├── blackbox/
│       │   └── AccountBlackBoxTest.java
│       ├── whitebox/
│       │   └── TransactionServiceWhiteBoxTest.java
│       ├── state/
│       │   └── AccountStateTest.java
│       ├── ui/
│       │   └── GUIComponentTest.java
│       ├── integration/
│       │   └── IntegrationTest.java
│       └── tdd/
│           ├── CreditScoreTest.java
│           └── CreditScoreGreenPhaseTest.java
├── target/
│   └── site/jacoco/
├── pom.xml
└── README.md
```

---

## Prerequisites

- Java JDK 17 or higher
- Apache Maven 3.6+
- IDE: IntelliJ IDEA (recommended) or Eclipse

---

## How to Build and Run

### Compile the Project
```bash
mvn clean compile
```

### Run All Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=AccountBlackBoxTest
mvn test -Dtest=TransactionServiceWhiteBoxTest
mvn test -Dtest=AccountStateTest
mvn test -Dtest=GUIComponentTest
mvn test -Dtest=IntegrationTest
mvn test -Dtest=CreditScoreGreenPhaseTest
```

### Generate Code Coverage Report
```bash
mvn test jacoco:report
```

---

## Test Summary

| Testing Category | Test Class | Test Count | Status |
|-----------------|------------|------------|--------|
| Black-Box Testing | AccountBlackBoxTest.java | 23 | PASS |
| White-Box Testing | TransactionServiceWhiteBoxTest.java | 28 | PASS |
| State-Based Testing | AccountStateTest.java | 22 | PASS |
| UI Testing | GUIComponentTest.java | 21 | PASS |
| Selenium UI Testing | BankingSeleniumTest.java | 24 | PASS |
| Integration Testing | IntegrationTest.java | 16 | PASS |
| TDD Testing | CreditScoreGreenPhaseTest.java | 11 | PASS |
| DAO Testing | AccountDAOTest.java | 16 | PASS |
| Service Testing | AccountServiceTest.java | 27 | PASS |
| **TOTAL** | | **188** | **PASS** |

---

## Deliverables

- Test_Case_Document.pdf
- Code_Coverage_Report.pdf
- UI_Bug_List.pdf
- TDD_Summary_Report.pdf
- Control_Flow_Graphs.pdf
- EP_BVA_Tables.pdf
- State_Transition_Matrix.pdf
- Final_Presentation.pptx
