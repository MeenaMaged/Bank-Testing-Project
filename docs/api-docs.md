# API Documentation

## Account Entity

### Class: `com.banking.entities.Account`

The core entity representing a bank account with state-based behavior.

#### Constructors

```java
Account(int id)
Account(int id, String clientName, double initialBalance)
```

#### Account Status Enum

```java
enum AccountStatus {
    Unverified,  // Initial state, limited operations
    Verified,    // Full access to all operations
    Suspended,   // View and deposit only
    Closed       // View only, no transactions
}
```

#### Transaction Methods

| Method | Parameters | Returns | Description |
|--------|------------|---------|-------------|
| `deposit(double amount)` | amount > 0 | boolean | Add funds to account |
| `withdraw(double amount)` | amount > 0, ≤ balance | boolean | Remove funds from account |
| `transfer(String cardNumber, double amount, String desc)` | recipient card, amount, description | boolean | Transfer funds to another account |

#### State Transition Methods

| Method | Valid From | Transitions To | Returns |
|--------|------------|----------------|---------|
| `verify()` | Unverified | Verified | boolean |
| `suspend()` | Verified | Suspended | boolean |
| `appeal()` | Suspended | Verified | boolean |
| `close()` | Any (except Closed) | Closed | boolean |

#### Getters

| Method | Returns |
|--------|---------|
| `getId()` | int |
| `getClientName()` | String |
| `getCardNumber()` | String (format: "XXXX XXXX XXXX XXXX") |
| `getBalance()` | double |
| `getStatus()` | AccountStatus |

---

## CreditScoreAccount Entity

### Class: `com.banking.entities.CreditScoreAccount`

Extended Account with credit scoring functionality (TDD Feature).

#### Constructor

```java
CreditScoreAccount(int id, String clientName, double initialBalance)
```

#### Credit Score Methods

| Method | Returns | Description |
|--------|---------|-------------|
| `getCreditScore()` | int (300-850) | Current credit score |
| `getTransactionLimit()` | double | Max transaction based on score |
| `withdrawWithCreditCheck(double amount)` | boolean | Withdraw with credit limit check |
| `recalculateCreditScore()` | void | Recalculate based on account factors |

#### Credit Score Constants

| Constant | Value | Description |
|----------|-------|-------------|
| INITIAL_SCORE | 700 | Starting credit score |
| MIN_SCORE | 300 | Minimum possible score |
| MAX_SCORE | 850 | Maximum possible score |
| DEPOSIT_BONUS | +5 | Score increase per 3 deposits |
| OVERDRAFT_PENALTY | -20 | Score decrease per overdraft attempt |
| SUSPENSION_PENALTY | -50 | Score decrease on suspension |
| APPEAL_RECOVERY | +25 | Score increase on successful appeal |

---

## TransactionService

### Class: `com.banking.services.TransactionService`

Service layer for processing transactions.

#### Methods

| Method | Parameters | Returns | Description |
|--------|------------|---------|-------------|
| `processDeposit(Account, double)` | account, amount | boolean | Process deposit transaction |
| `processWithdrawal(Account, double)` | account, amount | boolean | Process withdrawal transaction |
| `processTransfer(Account, String, double, String)` | sender, recipientCard, amount, desc | boolean | Process transfer transaction |
| `validateTransaction(Account, double, String)` | account, amount, type | boolean | Validate transaction is allowed |

---

## AccountService

### Class: `com.banking.services.AccountService`

Service layer for account management.

#### Methods

| Method | Parameters | Returns | Description |
|--------|------------|---------|-------------|
| `createAccount(int, String, double)` | id, clientName, initialBalance | Account | Create new account |
| `getAccount(int)` | id | Account | Retrieve account by ID |
| `generateStatement(Account)` | account | String | Generate account statement |
| `isOperationAllowed(Account, String)` | account, operation | boolean | Check if operation is permitted |
| `verifyAccount(int)` | id | boolean | Admin: verify account |
| `suspendAccount(int)` | id | boolean | Admin: suspend account |
| `closeAccount(int)` | id | boolean | Admin: close account |

---

## ClientController

### Class: `com.banking.controllers.ClientController`

Controller layer orchestrating client operations.

#### Constructor

```java
ClientController()
ClientController(AccountService accountService, TransactionService transactionService)
```

#### Methods

| Method | Parameters | Returns | Description |
|--------|------------|---------|-------------|
| `createAccount(int, String, double)` | id, name, balance | Account | Create account |
| `processDeposit(Account, double)` | account, amount | String | Process deposit, return message |
| `processWithdrawal(Account, double)` | account, amount | String | Process withdrawal, return message |
| `processTransfer(Account, String, double, String)` | sender, card, amount, desc | String | Process transfer, return message |
| `getAccountStatement(Account)` | account | String | Get formatted statement |
| `validateOperation(Account, String)` | account, operation | boolean | Validate operation allowed |

---

## AccountDAO

### Class: `com.banking.dao.AccountDAO`

Data access object for account persistence (in-memory).

#### Static Methods

| Method | Parameters | Returns | Description |
|--------|------------|---------|-------------|
| `add(Account)` | account | void | Store account |
| `findById(int)` | id | Account | Find by ID |
| `findByCardNumber(String)` | cardNumber | Account | Find by card number |
| `remove(int)` | id | void | Remove account |
| `clear()` | none | void | Clear all accounts |
| `count()` | none | int | Get account count |
