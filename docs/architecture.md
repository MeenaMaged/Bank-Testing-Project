# System Architecture

## Overview

The Banking System follows a layered architecture pattern with clear separation of concerns.

```
┌─────────────────────────────────────────────────────┐
│                    GUI Layer                         │
│              (BankingGUI.java)                       │
└─────────────────────┬───────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────┐
│                Controller Layer                      │
│            (ClientController.java)                   │
└─────────────────────┬───────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────┐
│                 Service Layer                        │
│    (AccountService.java, TransactionService.java)    │
└─────────────────────┬───────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────┐
│               Data Access Layer                      │
│              (AccountDAO.java)                       │
└─────────────────────┬───────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────┐
│                 Entity Layer                         │
│      (Account.java, CreditScoreAccount.java)         │
└─────────────────────────────────────────────────────┘
```

## Components

### 1. GUI Layer
- **BankingGUI.java**: Swing-based user interface
- Handles user input and displays account information
- Communicates with Controller layer

### 2. Controller Layer
- **ClientController.java**: Orchestrates business operations
- Validates user requests
- Coordinates between GUI and Service layers

### 3. Service Layer
- **AccountService.java**: Account management operations
- **TransactionService.java**: Transaction processing
- Contains business logic

### 4. Data Access Layer
- **AccountDAO.java**: In-memory data storage
- Provides CRUD operations for accounts
- Maintains account index by ID and card number

### 5. Entity Layer
- **Account.java**: Core account entity with state machine
- **CreditScoreAccount.java**: Extended account with credit scoring (TDD feature)

## State Machine

```
                    ┌──────────┐
                    │Unverified│
                    └────┬─────┘
                         │ verify()
                         ▼
         appeal() ┌──────────┐ suspend()
           ┌──────│ Verified │──────┐
           │      └────┬─────┘      │
           │           │            │
           ▼           │ close()    ▼
     ┌──────────┐      │      ┌──────────┐
     │Suspended │──────┼──────│  Closed  │
     └──────────┘      │      └──────────┘
           close()     │
                       ▼
                    (Any state can close)
```

## Data Flow

1. **Deposit Flow**:
   ```
   GUI → Controller → TransactionService → Account.deposit() → DAO update
   ```

2. **Withdrawal Flow**:
   ```
   GUI → Controller → TransactionService → Account.withdraw() → DAO update
   ```

3. **Transfer Flow**:
   ```
   GUI → Controller → TransactionService → Account.transfer() → 
        → DAO lookup recipient → Update both accounts
   ```
