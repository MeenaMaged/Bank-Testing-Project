package com.banking.integration;

import com.banking.controllers.ClientController;
import com.banking.dao.AccountDAO;
import com.banking.entities.Account;
import com.banking.entities.Account.AccountStatus;
import com.banking.services.AccountService;
import com.banking.services.TransactionService;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration testing for banking system
 * Tests interaction between different components
 */
@DisplayName("Integration Tests")
public class IntegrationTest {
    
    private ClientController controller;
    private AccountService accountService;
    private TransactionService transactionService;
    
    @BeforeEach
    void setUp() {
        AccountDAO.clear();
        accountService = new AccountService();
        transactionService = new TransactionService();
        controller = new ClientController(accountService, transactionService);
    }
    
    // ==================== Controller-Service Integration ====================
    
    @Nested
    @DisplayName("Controller-Service Integration")
    class ControllerServiceIntegration {
        
        @Test
        @DisplayName("IT01 - Account creation through controller")
        void testAccountCreationThroughController() {
            Account account = controller.createAccount(1, "Integration User", 1500);
            
            assertNotNull(account);
            assertEquals("Integration User", account.getClientName());
            assertEquals(1500, account.getBalance());
            assertEquals(AccountStatus.Unverified, account.getStatus());
        }
        
        @Test
        @DisplayName("IT02 - Deposit through controller")
        void testDepositThroughController() {
            Account account = controller.createAccount(1, "Test User", 1000);
            
            String result = controller.processDeposit(account, 200);
            
            assertEquals("Deposit successful", result);
            assertEquals(1200, account.getBalance());
        }
        
        @Test
        @DisplayName("IT03 - Withdrawal through controller")
        void testWithdrawalThroughController() {
            Account account = controller.createAccount(1, "Test User", 1000);
            account.verify();
            
            String result = controller.processWithdrawal(account, 300);
            
            assertEquals("Withdrawal successful", result);
            assertEquals(700, account.getBalance());
        }
        
        @Test
        @DisplayName("IT04 - Statement generation through controller")
        void testStatementThroughController() {
            Account account = controller.createAccount(1, "Test User", 1000);
            
            String statement = controller.getAccountStatement(account);
            
            assertTrue(statement.contains("ACCOUNT STATEMENT"));
            assertTrue(statement.contains("Test User"));
            assertTrue(statement.contains("1000.00"));
        }
        
        @Test
        @DisplayName("IT05 - Operation validation through controller")
        void testOperationValidationThroughController() {
            Account account = controller.createAccount(1, "Test User", 1000);
            
            assertTrue(controller.validateOperation(account, "deposit"));
            assertFalse(controller.validateOperation(account, "withdraw")); // Unverified
            
            account.verify();
            assertTrue(controller.validateOperation(account, "withdraw"));
        }
    }
    
    // ==================== End-to-End Workflows ====================
    
    @Nested
    @DisplayName("End-to-End Workflows")
    class EndToEndWorkflows {
        
        @Test
        @DisplayName("IT06 - New customer onboarding workflow")
        void testNewCustomerOnboarding() {
            // Step 1: Create account
            Account customer = controller.createAccount(1, "New Customer", 500);
            assertNotNull(customer);
            assertEquals(AccountStatus.Unverified, customer.getStatus());
            
            // Step 2: Initial deposit (should work)
            String depositResult = controller.processDeposit(customer, 200);
            assertEquals("Deposit successful", depositResult);
            assertEquals(700, customer.getBalance());
            
            // Step 3: Try withdrawal (should fail - unverified)
            String withdrawResult = controller.processWithdrawal(customer, 100);
            assertEquals("Withdrawal failed", withdrawResult);
            assertEquals(700, customer.getBalance());
            
            // Step 4: Verify account
            assertTrue(customer.verify());
            assertEquals(AccountStatus.Verified, customer.getStatus());
            
            // Step 5: Withdrawal after verification (should work)
            withdrawResult = controller.processWithdrawal(customer, 100);
            assertEquals("Withdrawal successful", withdrawResult);
            assertEquals(600, customer.getBalance());
        }
        
        @Test
        @DisplayName("IT07 - Account suspension and recovery workflow")
        void testSuspensionRecoveryWorkflow() {
            Account customer = controller.createAccount(1, "Customer", 1000);
            customer.verify();
            
            // Normal transaction
            controller.processDeposit(customer, 200);
            assertEquals(1200, customer.getBalance());
            
            // Suspend account
            assertTrue(customer.suspend());
            assertEquals(AccountStatus.Suspended, customer.getStatus());
            
            // Transaction blocked
            String result = controller.processWithdrawal(customer, 100);
            assertEquals("Withdrawal failed", result);
            assertEquals(1200, customer.getBalance());
            
            // Appeal
            assertTrue(customer.appeal());
            assertEquals(AccountStatus.Verified, customer.getStatus());
            
            // Transaction restored
            result = controller.processWithdrawal(customer, 100);
            assertEquals("Withdrawal successful", result);
            assertEquals(1100, customer.getBalance());
        }
        
        @Test
        @DisplayName("IT08 - Transfer workflow between accounts")
        void testTransferWorkflow() {
            Account sender = controller.createAccount(1, "Sender", 1000);
            Account receiver = controller.createAccount(2, "Receiver", 500);
            sender.verify();
            receiver.verify();
            
            String result = controller.processTransfer(sender, receiver.getCardNumber(), 300, "Payment");
            
            assertEquals("Transfer successful", result);
            assertEquals(700, sender.getBalance());
            assertEquals(800, receiver.getBalance());
        }
    }
    
    // ==================== Error Propagation Tests ====================
    
    @Nested
    @DisplayName("Error Propagation Tests")
    class ErrorPropagationTests {
        
        @Test
        @DisplayName("IT09 - Insufficient funds error propagation")
        void testInsufficientFundsError() {
            Account account = controller.createAccount(1, "Test", 100);
            account.verify();
            
            String result = controller.processWithdrawal(account, 200);
            
            assertEquals("Withdrawal failed", result);
            assertEquals(100, account.getBalance());
        }
        
        @Test
        @DisplayName("IT10 - Invalid amount error propagation")
        void testInvalidAmountError() {
            Account account = controller.createAccount(1, "Test", 1000);
            
            String result = controller.processDeposit(account, -50);
            
            assertEquals("Deposit failed", result);
        }
        
        @Test
        @DisplayName("IT11 - Closed account error propagation")
        void testClosedAccountError() {
            Account account = controller.createAccount(1, "Test", 1000);
            account.verify();
            account.close();
            
            String result = controller.processDeposit(account, 100);
            
            assertEquals("Deposit failed", result);
        }
        
        @Test
        @DisplayName("IT12 - Null account error handling")
        void testNullAccountError() {
            String result = controller.processDeposit(null, 100);
            
            assertEquals("Deposit failed", result);
        }
    }
    
    // ==================== Data Consistency Tests ====================
    
    @Nested
    @DisplayName("Data Consistency Tests")
    class DataConsistencyTests {
        
        @Test
        @DisplayName("IT13 - Balance consistency across operations")
        void testBalanceConsistency() {
            Account account = controller.createAccount(1, "Test", 1000);
            account.verify();
            
            // Multiple operations
            controller.processDeposit(account, 200);
            controller.processWithdrawal(account, 150);
            controller.processDeposit(account, 50);
            controller.processWithdrawal(account, 100);
            
            // Expected: 1000 + 200 - 150 + 50 - 100 = 1000
            assertEquals(1000, account.getBalance());
        }
        
        @Test
        @DisplayName("IT14 - Transfer balance consistency")
        void testTransferBalanceConsistency() {
            Account sender = controller.createAccount(1, "Sender", 1000);
            Account receiver = controller.createAccount(2, "Receiver", 500);
            sender.verify();
            receiver.verify();
            
            double totalBefore = sender.getBalance() + receiver.getBalance();
            
            controller.processTransfer(sender, receiver.getCardNumber(), 300, "Test");
            
            double totalAfter = sender.getBalance() + receiver.getBalance();
            
            assertEquals(totalBefore, totalAfter); // Money is conserved
        }
        
        @Test
        @DisplayName("IT15 - Statement reflects current balance")
        void testStatementConsistency() {
            Account account = controller.createAccount(1, "Test", 1000);
            account.verify();
            
            controller.processDeposit(account, 500);
            controller.processWithdrawal(account, 200);
            
            String statement = controller.getAccountStatement(account);
            
            assertTrue(statement.contains("1300.00"));
        }
        
        @Test
        @DisplayName("IT16 - DAO persistence consistency")
        void testDAOConsistency() {
            Account created = controller.createAccount(1, "Test", 1000);
            
            Account retrieved = AccountDAO.findById(1);
            
            assertNotNull(retrieved);
            assertEquals(created.getClientName(), retrieved.getClientName());
            assertEquals(created.getBalance(), retrieved.getBalance());
            assertSame(created, retrieved);
        }
    }
}