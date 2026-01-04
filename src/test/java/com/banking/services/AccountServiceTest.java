package com.banking.services;

import com.banking.dao.AccountDAO;
import com.banking.entities.Account;
import com.banking.entities.Account.AccountStatus;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Additional tests for AccountService to improve code coverage
 * Tests admin operations and edge cases
 */
@DisplayName("AccountService Additional Tests")
public class AccountServiceTest {
    
    private AccountService accountService;
    
    @BeforeEach
    void setUp() {
        AccountDAO.clear();
        accountService = new AccountService();
    }
    
    // ==================== Account Creation Tests ====================
    
    @Nested
    @DisplayName("Account Creation Tests")
    class AccountCreationTests {
        
        @Test
        @DisplayName("AS01 - Create account with valid parameters")
        void testCreateAccountValid() {
            Account account = accountService.createAccount(1, "John Doe", 1000);
            
            assertNotNull(account);
            assertEquals(1, account.getId());
            assertEquals("John Doe", account.getClientName());
            assertEquals(1000, account.getBalance());
            assertEquals(AccountStatus.Unverified, account.getStatus());
        }
        
        @Test
        @DisplayName("AS02 - Create account with zero balance")
        void testCreateAccountZeroBalance() {
            Account account = accountService.createAccount(1, "Zero Balance", 0);
            
            assertNotNull(account);
            assertEquals(0, account.getBalance());
        }
        
        @Test
        @DisplayName("AS03 - Create account with large balance")
        void testCreateAccountLargeBalance() {
            Account account = accountService.createAccount(1, "Rich User", 1000000);
            
            assertNotNull(account);
            assertEquals(1000000, account.getBalance());
        }
        
        @Test
        @DisplayName("AS04 - Created account is retrievable from DAO")
        void testCreatedAccountInDAO() {
            Account created = accountService.createAccount(1, "DAO Test", 500);
            
            Account retrieved = AccountDAO.findById(1);
            assertNotNull(retrieved);
            assertSame(created, retrieved);
        }
    }
    
    // ==================== Account Retrieval Tests ====================
    
    @Nested
    @DisplayName("Account Retrieval Tests")
    class AccountRetrievalTests {
        
        @Test
        @DisplayName("AS05 - Get existing account by ID")
        void testGetAccountExists() {
            accountService.createAccount(1, "Test User", 1000);
            
            Account retrieved = accountService.getAccount(1);
            
            assertNotNull(retrieved);
            assertEquals("Test User", retrieved.getClientName());
        }
        
        @Test
        @DisplayName("AS06 - Get non-existent account returns null")
        void testGetAccountNotExists() {
            Account retrieved = accountService.getAccount(999);
            
            assertNull(retrieved);
        }
        
        @Test
        @DisplayName("AS07 - Get account with negative ID")
        void testGetAccountNegativeId() {
            Account retrieved = accountService.getAccount(-1);
            
            assertNull(retrieved);
        }
    }
    
    // ==================== Statement Generation Tests ====================
    
    @Nested
    @DisplayName("Statement Generation Tests")
    class StatementGenerationTests {
        
        @Test
        @DisplayName("AS08 - Generate statement for valid account")
        void testGenerateStatementValid() {
            Account account = accountService.createAccount(1, "Statement Test", 2500);
            account.setStatus(AccountStatus.Verified);
            
            String statement = accountService.generateStatement(account);
            
            assertTrue(statement.contains("ACCOUNT STATEMENT"));
            assertTrue(statement.contains("Statement Test"));
            assertTrue(statement.contains("2500.00"));
            assertTrue(statement.contains("Verified"));
        }
        
        @Test
        @DisplayName("AS09 - Generate statement for null account")
        void testGenerateStatementNull() {
            String statement = accountService.generateStatement(null);
            
            assertEquals("Account not found", statement);
        }
        
        @Test
        @DisplayName("AS10 - Statement contains card number")
        void testStatementContainsCardNumber() {
            Account account = accountService.createAccount(1, "Card Test", 1000);
            
            String statement = accountService.generateStatement(account);
            
            assertTrue(statement.contains(account.getCardNumber()));
        }
        
        @Test
        @DisplayName("AS11 - Statement reflects all statuses")
        void testStatementAllStatuses() {
            Account account = accountService.createAccount(1, "Status Test", 1000);
            
            // Unverified
            String stmt1 = accountService.generateStatement(account);
            assertTrue(stmt1.contains("Unverified"));
            
            // Verified
            account.verify();
            String stmt2 = accountService.generateStatement(account);
            assertTrue(stmt2.contains("Verified"));
            
            // Suspended
            account.suspend();
            String stmt3 = accountService.generateStatement(account);
            assertTrue(stmt3.contains("Suspended"));
            
            // Closed
            account.close();
            String stmt4 = accountService.generateStatement(account);
            assertTrue(stmt4.contains("Closed"));
        }
    }
    
    // ==================== Operation Validation Tests ====================
    
    @Nested
    @DisplayName("Operation Validation Tests")
    class OperationValidationTests {
        
        @Test
        @DisplayName("AS12 - Validate operations for null account")
        void testOperationValidationNullAccount() {
            assertFalse(accountService.isOperationAllowed(null, "deposit"));
            assertFalse(accountService.isOperationAllowed(null, "withdraw"));
            assertFalse(accountService.isOperationAllowed(null, "transfer"));
            assertFalse(accountService.isOperationAllowed(null, "view"));
        }
        
        @Test
        @DisplayName("AS13 - Validate deposit operation by status")
        void testDepositOperationByStatus() {
            Account account = accountService.createAccount(1, "Test", 1000);
            
            // Unverified - deposit allowed
            assertTrue(accountService.isOperationAllowed(account, "deposit"));
            
            // Verified - deposit allowed
            account.verify();
            assertTrue(accountService.isOperationAllowed(account, "deposit"));
            
            // Suspended - deposit allowed
            account.suspend();
            assertTrue(accountService.isOperationAllowed(account, "deposit"));
            
            // Closed - deposit NOT allowed
            account.close();
            assertFalse(accountService.isOperationAllowed(account, "deposit"));
        }
        
        @Test
        @DisplayName("AS14 - Validate withdraw operation by status")
        void testWithdrawOperationByStatus() {
            Account account = accountService.createAccount(1, "Test", 1000);
            
            // Unverified - withdraw NOT allowed
            assertFalse(accountService.isOperationAllowed(account, "withdraw"));
            
            // Verified - withdraw allowed
            account.verify();
            assertTrue(accountService.isOperationAllowed(account, "withdraw"));
            
            // Suspended - withdraw NOT allowed
            account.suspend();
            assertFalse(accountService.isOperationAllowed(account, "withdraw"));
            
            // Closed - withdraw NOT allowed
            account.close();
            assertFalse(accountService.isOperationAllowed(account, "withdraw"));
        }
        
        @Test
        @DisplayName("AS15 - Validate transfer operation by status")
        void testTransferOperationByStatus() {
            Account account = accountService.createAccount(1, "Test", 1000);
            
            // Unverified - transfer NOT allowed
            assertFalse(accountService.isOperationAllowed(account, "transfer"));
            
            // Verified - transfer allowed
            account.verify();
            assertTrue(accountService.isOperationAllowed(account, "transfer"));
            
            // Suspended - transfer NOT allowed
            account.suspend();
            assertFalse(accountService.isOperationAllowed(account, "transfer"));
        }
        
        @Test
        @DisplayName("AS16 - View operation always allowed")
        void testViewOperationAlwaysAllowed() {
            Account account = accountService.createAccount(1, "Test", 1000);
            
            assertTrue(accountService.isOperationAllowed(account, "view"));
            
            account.verify();
            assertTrue(accountService.isOperationAllowed(account, "view"));
            
            account.suspend();
            assertTrue(accountService.isOperationAllowed(account, "view"));
            
            account.close();
            assertTrue(accountService.isOperationAllowed(account, "view"));
        }
        
        @Test
        @DisplayName("AS17 - Unknown operation returns false")
        void testUnknownOperation() {
            Account account = accountService.createAccount(1, "Test", 1000);
            account.verify();
            
            assertFalse(accountService.isOperationAllowed(account, "unknown"));
            assertFalse(accountService.isOperationAllowed(account, "delete"));
            assertFalse(accountService.isOperationAllowed(account, ""));
        }
    }
    
    // ==================== Admin Operations Tests ====================
    
    @Nested
    @DisplayName("Admin Operations Tests")
    class AdminOperationsTests {
        
        @Test
        @DisplayName("AS18 - Verify account successfully")
        void testVerifyAccountSuccess() {
            accountService.createAccount(1, "Verify Test", 1000);
            
            boolean result = accountService.verifyAccount(1);
            
            assertTrue(result);
            assertEquals(AccountStatus.Verified, AccountDAO.findById(1).getStatus());
        }
        
        @Test
        @DisplayName("AS19 - Verify non-existent account fails")
        void testVerifyAccountNotExists() {
            boolean result = accountService.verifyAccount(999);
            
            assertFalse(result);
        }
        
        @Test
        @DisplayName("AS20 - Verify already verified account fails")
        void testVerifyAlreadyVerified() {
            accountService.createAccount(1, "Test", 1000);
            accountService.verifyAccount(1);
            
            boolean result = accountService.verifyAccount(1);
            
            assertFalse(result);
        }
        
        @Test
        @DisplayName("AS21 - Suspend account successfully")
        void testSuspendAccountSuccess() {
            accountService.createAccount(1, "Suspend Test", 1000);
            accountService.verifyAccount(1);
            
            boolean result = accountService.suspendAccount(1);
            
            assertTrue(result);
            assertEquals(AccountStatus.Suspended, AccountDAO.findById(1).getStatus());
        }
        
        @Test
        @DisplayName("AS22 - Suspend non-existent account fails")
        void testSuspendAccountNotExists() {
            boolean result = accountService.suspendAccount(999);
            
            assertFalse(result);
        }
        
        @Test
        @DisplayName("AS23 - Suspend unverified account fails")
        void testSuspendUnverifiedAccount() {
            accountService.createAccount(1, "Test", 1000);
            
            boolean result = accountService.suspendAccount(1);
            
            assertFalse(result);
            assertEquals(AccountStatus.Unverified, AccountDAO.findById(1).getStatus());
        }
        
        @Test
        @DisplayName("AS24 - Close account successfully")
        void testCloseAccountSuccess() {
            accountService.createAccount(1, "Close Test", 1000);
            accountService.verifyAccount(1);
            
            boolean result = accountService.closeAccount(1);
            
            assertTrue(result);
            assertEquals(AccountStatus.Closed, AccountDAO.findById(1).getStatus());
        }
        
        @Test
        @DisplayName("AS25 - Close non-existent account fails")
        void testCloseAccountNotExists() {
            boolean result = accountService.closeAccount(999);
            
            assertFalse(result);
        }
        
        @Test
        @DisplayName("AS26 - Close already closed account fails")
        void testCloseAlreadyClosed() {
            accountService.createAccount(1, "Test", 1000);
            accountService.closeAccount(1);
            
            boolean result = accountService.closeAccount(1);
            
            assertFalse(result);
        }
        
        @Test
        @DisplayName("AS27 - Full admin workflow: verify -> suspend -> close")
        void testFullAdminWorkflow() {
            accountService.createAccount(1, "Admin Workflow", 1000);
            
            // Verify
            assertTrue(accountService.verifyAccount(1));
            assertEquals(AccountStatus.Verified, AccountDAO.findById(1).getStatus());
            
            // Suspend
            assertTrue(accountService.suspendAccount(1));
            assertEquals(AccountStatus.Suspended, AccountDAO.findById(1).getStatus());
            
            // Close
            assertTrue(accountService.closeAccount(1));
            assertEquals(AccountStatus.Closed, AccountDAO.findById(1).getStatus());
        }
    }
}
