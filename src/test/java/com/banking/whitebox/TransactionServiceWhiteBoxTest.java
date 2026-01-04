package com.banking.whitebox;

import com.banking.dao.AccountDAO;
import com.banking.entities.Account;
import com.banking.entities.Account.AccountStatus;
import com.banking.services.TransactionService;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * White-box testing for TransactionService
 * Tests internal logic paths and code coverage
 */
@DisplayName("TransactionService White-Box Tests")
public class TransactionServiceWhiteBoxTest {
    
    private TransactionService transactionService;
    private Account account;
    
    @BeforeEach
    void setUp() {
        AccountDAO.clear();
        transactionService = new TransactionService();
        account = new Account(1, "Test User", 1000);
        account.setStatus(AccountStatus.Verified);
        AccountDAO.add(account);
    }
    
    // ==================== Decision Coverage Tests ====================
    
    @Nested
    @DisplayName("Decision Coverage Tests")
    class DecisionCoverageTests {
        
        @Test
        @DisplayName("WB01 - processDeposit: null account path")
        void testProcessDepositNullAccount() {
            assertFalse(transactionService.processDeposit(null, 100));
        }
        
        @Test
        @DisplayName("WB02 - processDeposit: valid deposit path")
        void testProcessDepositValidPath() {
            assertTrue(transactionService.processDeposit(account, 100));
            assertEquals(1100, account.getBalance());
        }
        
        @Test
        @DisplayName("WB03 - processDeposit: invalid amount path")
        void testProcessDepositInvalidAmount() {
            assertFalse(transactionService.processDeposit(account, -50));
        }
        
        @Test
        @DisplayName("WB04 - processDeposit: closed account path")
        void testProcessDepositClosedAccount() {
            account.setStatus(AccountStatus.Closed);
            assertFalse(transactionService.processDeposit(account, 100));
        }
        
        @Test
        @DisplayName("WB05 - processWithdrawal: null account path")
        void testProcessWithdrawalNullAccount() {
            assertFalse(transactionService.processWithdrawal(null, 100));
        }
        
        @Test
        @DisplayName("WB06 - processWithdrawal: valid withdrawal path")
        void testProcessWithdrawalValidPath() {
            assertTrue(transactionService.processWithdrawal(account, 200));
            assertEquals(800, account.getBalance());
        }
        
        @Test
        @DisplayName("WB07 - processWithdrawal: insufficient balance path")
        void testProcessWithdrawalInsufficientBalance() {
            assertFalse(transactionService.processWithdrawal(account, 2000));
        }
        
        @Test
        @DisplayName("WB08 - processWithdrawal: suspended account path")
        void testProcessWithdrawalSuspendedAccount() {
            account.setStatus(AccountStatus.Suspended);
            assertFalse(transactionService.processWithdrawal(account, 100));
        }
    }
    
    // ==================== Condition Coverage Tests ====================
    
    @Nested
    @DisplayName("Condition Coverage Tests")
    class ConditionCoverageTests {
        
        @Test
        @DisplayName("WB09 - validateTransaction: null account condition")
        void testValidateTransactionNullAccount() {
            assertFalse(transactionService.validateTransaction(null, 100, "deposit"));
        }
        
        @Test
        @DisplayName("WB10 - validateTransaction: zero amount condition")
        void testValidateTransactionZeroAmount() {
            assertFalse(transactionService.validateTransaction(account, 0, "deposit"));
        }
        
        @Test
        @DisplayName("WB11 - validateTransaction: negative amount condition")
        void testValidateTransactionNegativeAmount() {
            assertFalse(transactionService.validateTransaction(account, -100, "deposit"));
        }
        
        @Test
        @DisplayName("WB12 - validateTransaction: exceeds max limit condition")
        void testValidateTransactionExceedsLimit() {
            assertFalse(transactionService.validateTransaction(account, 15000, "deposit"));
        }
        
        @Test
        @DisplayName("WB13 - validateTransaction: deposit on verified account")
        void testValidateTransactionDepositVerified() {
            assertTrue(transactionService.validateTransaction(account, 500, "deposit"));
        }
        
        @Test
        @DisplayName("WB14 - validateTransaction: deposit on closed account")
        void testValidateTransactionDepositClosed() {
            account.setStatus(AccountStatus.Closed);
            assertFalse(transactionService.validateTransaction(account, 500, "deposit"));
        }
        
        @Test
        @DisplayName("WB15 - validateTransaction: withdraw on verified with sufficient balance")
        void testValidateTransactionWithdrawVerified() {
            assertTrue(transactionService.validateTransaction(account, 500, "withdraw"));
        }
        
        @Test
        @DisplayName("WB16 - validateTransaction: withdraw exceeds balance")
        void testValidateTransactionWithdrawExceedsBalance() {
            assertFalse(transactionService.validateTransaction(account, 1500, "withdraw"));
        }
        
        @Test
        @DisplayName("WB17 - validateTransaction: withdraw on unverified account")
        void testValidateTransactionWithdrawUnverified() {
            account.setStatus(AccountStatus.Unverified);
            assertFalse(transactionService.validateTransaction(account, 500, "withdraw"));
        }
        
        @Test
        @DisplayName("WB18 - validateTransaction: view operation always allowed")
        void testValidateTransactionView() {
            assertTrue(transactionService.validateTransaction(account, 100, "view"));
            
            account.setStatus(AccountStatus.Closed);
            assertTrue(transactionService.validateTransaction(account, 100, "view"));
        }
        
        @Test
        @DisplayName("WB19 - validateTransaction: unknown operation type")
        void testValidateTransactionUnknownOperation() {
            assertFalse(transactionService.validateTransaction(account, 100, "unknown"));
        }
    }
    
    // ==================== Path Coverage Tests ====================
    
    @Nested
    @DisplayName("Path Coverage Tests")
    class PathCoverageTests {
        
        private Account sender;
        private Account receiver;
        
        @BeforeEach
        void setUpTransferAccounts() {
            sender = new Account(10, "Sender", 1000);
            receiver = new Account(20, "Receiver", 500);
            sender.setStatus(AccountStatus.Verified);
            receiver.setStatus(AccountStatus.Verified);
            AccountDAO.add(sender);
            AccountDAO.add(receiver);
        }
        
        @Test
        @DisplayName("WB20 - processTransfer: null sender path")
        void testProcessTransferNullSender() {
            assertFalse(transactionService.processTransfer(null, receiver.getCardNumber(), 100, "Test"));
        }
        
        @Test
        @DisplayName("WB21 - processTransfer: null recipient card path")
        void testProcessTransferNullRecipient() {
            assertFalse(transactionService.processTransfer(sender, null, 100, "Test"));
        }
        
        @Test
        @DisplayName("WB22 - processTransfer: successful transfer path")
        void testProcessTransferSuccessful() {
            assertTrue(transactionService.processTransfer(sender, receiver.getCardNumber(), 200, "Test"));
            assertEquals(800, sender.getBalance());
            assertEquals(700, receiver.getBalance());
        }
        
        @Test
        @DisplayName("WB23 - processTransfer: sender not verified path")
        void testProcessTransferSenderNotVerified() {
            sender.setStatus(AccountStatus.Suspended);
            assertFalse(transactionService.processTransfer(sender, receiver.getCardNumber(), 100, "Test"));
        }
        
        @Test
        @DisplayName("WB24 - processTransfer: insufficient balance path")
        void testProcessTransferInsufficientBalance() {
            assertFalse(transactionService.processTransfer(sender, receiver.getCardNumber(), 2000, "Test"));
        }
        
        @Test
        @DisplayName("WB25 - processTransfer: recipient not found path")
        void testProcessTransferRecipientNotFound() {
            assertFalse(transactionService.processTransfer(sender, "9999 9999 9999 9999", 100, "Test"));
        }
    }
    
    // ==================== Loop Coverage Tests ====================
    
    @Nested
    @DisplayName("Loop Coverage Tests")
    class LoopCoverageTests {
        
        @Test
        @DisplayName("WB26 - Multiple sequential deposits")
        void testMultipleDeposits() {
            Account testAccount = new Account(100, "Loop Test", 0);
            testAccount.setStatus(AccountStatus.Verified);
            
            for (int i = 0; i < 5; i++) {
                assertTrue(transactionService.processDeposit(testAccount, 100));
            }
            
            assertEquals(500, testAccount.getBalance());
        }
        
        @Test
        @DisplayName("WB27 - Multiple sequential withdrawals")
        void testMultipleWithdrawals() {
            Account testAccount = new Account(100, "Loop Test", 500);
            testAccount.setStatus(AccountStatus.Verified);
            
            for (int i = 0; i < 3; i++) {
                assertTrue(transactionService.processWithdrawal(testAccount, 100));
            }
            
            assertEquals(200, testAccount.getBalance());
        }
        
        @Test
        @DisplayName("WB28 - Withdrawal loop until insufficient balance")
        void testWithdrawalUntilInsufficient() {
            Account testAccount = new Account(100, "Loop Test", 250);
            testAccount.setStatus(AccountStatus.Verified);
            
            int successCount = 0;
            while (transactionService.processWithdrawal(testAccount, 100)) {
                successCount++;
            }
            
            assertEquals(2, successCount);
            assertEquals(50, testAccount.getBalance());
        }
    }
}