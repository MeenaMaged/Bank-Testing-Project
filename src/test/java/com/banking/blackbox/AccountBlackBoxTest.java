package com.banking.blackbox;

import com.banking.dao.AccountDAO;
import com.banking.entities.Account;
import com.banking.entities.Account.AccountStatus;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Black-box testing for Account class
 * Tests functionality without knowledge of internal implementation
 */
@DisplayName("Account Black-Box Tests")
public class AccountBlackBoxTest {
    
    private Account account;
    
    @BeforeEach
    void setUp() {
        AccountDAO.clear();
        account = new Account(1);
        account.setStatus(AccountStatus.Verified);
    }
    
    // ==================== Deposit Tests ====================
    
    @Nested
    @DisplayName("Deposit Tests")
    class DepositTests {
        
        @Test
        @DisplayName("BB01 - Deposit negative amount should fail")
        void testDepositNegativeAmount() {
            assertFalse(account.deposit(-100));
        }
        
        @Test
        @DisplayName("BB02 - Deposit zero amount should fail")
        void testDepositZeroAmount() {
            assertFalse(account.deposit(0));
        }
        
        @Test
        @DisplayName("BB03 - Deposit valid amount should succeed")
        void testDepositValidAmount() {
            assertTrue(account.deposit(200));
            assertEquals(200, account.getBalance());
        }
        
        @Test
        @DisplayName("BB04 - Deposit minimum valid amount should succeed")
        void testDepositMinimumAmount() {
            assertTrue(account.deposit(0.01));
            assertEquals(0.01, account.getBalance(), 0.001);
        }
        
        @Test
        @DisplayName("BB05 - Deposit on closed account should fail")
        void testDepositClosedAccount() {
            account.setStatus(AccountStatus.Closed);
            assertFalse(account.deposit(100));
        }
        
        @Test
        @DisplayName("BB06 - Deposit on suspended account should succeed")
        void testDepositSuspendedAccount() {
            account.setStatus(AccountStatus.Suspended);
            assertTrue(account.deposit(100));
        }
        
        @Test
        @DisplayName("BB07 - Deposit on unverified account should succeed")
        void testDepositUnverifiedAccount() {
            account.setStatus(AccountStatus.Unverified);
            assertTrue(account.deposit(100));
        }
    }
    
    // ==================== Withdrawal Tests ====================
    
    @Nested
    @DisplayName("Withdrawal Tests")
    class WithdrawalTests {
        
        @BeforeEach
        void setUpBalance() {
            account.deposit(500);
        }
        
        @Test
        @DisplayName("BB08 - Withdraw valid amount should succeed")
        void testWithdrawValidAmount() {
            assertTrue(account.withdraw(200));
            assertEquals(300, account.getBalance());
        }
        
        @Test
        @DisplayName("BB09 - Withdraw exact balance should succeed")
        void testWithdrawExactBalance() {
            assertTrue(account.withdraw(500));
            assertEquals(0, account.getBalance());
        }
        
        @Test
        @DisplayName("BB10 - Withdraw overdraft should fail")
        void testWithdrawOverdraft() {
            assertFalse(account.withdraw(600));
            assertEquals(500, account.getBalance());
        }
        
        @Test
        @DisplayName("BB11 - Withdraw negative amount should fail")
        void testWithdrawNegativeAmount() {
            assertFalse(account.withdraw(-100));
        }
        
        @Test
        @DisplayName("BB12 - Withdraw zero amount should fail")
        void testWithdrawZeroAmount() {
            assertFalse(account.withdraw(0));
        }
        
        @Test
        @DisplayName("BB13 - Withdraw from suspended account should fail")
        void testWithdrawSuspendedAccount() {
            account.setStatus(AccountStatus.Suspended);
            assertFalse(account.withdraw(100));
            assertEquals(500, account.getBalance());
        }
        
        @Test
        @DisplayName("BB14 - Withdraw from closed account should fail")
        void testWithdrawClosedAccount() {
            account.setStatus(AccountStatus.Closed);
            assertFalse(account.withdraw(100));
        }
        
        @Test
        @DisplayName("BB15 - Withdraw from unverified account should fail")
        void testWithdrawUnverifiedAccount() {
            account.setStatus(AccountStatus.Unverified);
            assertFalse(account.withdraw(100));
        }
    }
    
    // ==================== Transfer Tests ====================
    
    @Nested
    @DisplayName("Transfer Tests")
    class TransferTests {
        
        private Account sender;
        private Account receiver;
        
        @BeforeEach
        void setUpAccounts() {
            sender = new Account(1);
            receiver = new Account(2);
            sender.setStatus(AccountStatus.Verified);
            receiver.setStatus(AccountStatus.Verified);
            sender.deposit(1000);
            receiver.deposit(500);
            AccountDAO.add(sender);
            AccountDAO.add(receiver);
        }
        
        @Test
        @DisplayName("BB16 - Successful transfer between verified accounts")
        void testSuccessfulTransfer() {
            boolean result = sender.transfer(receiver.getCardNumber(), 300, "Test Transfer");
            
            assertTrue(result);
            assertEquals(700, sender.getBalance());
            assertEquals(800, receiver.getBalance());
        }
        
        @Test
        @DisplayName("BB17 - Transfer fails when sender is unverified")
        void testTransferFailsWhenSenderUnverified() {
            sender.setStatus(AccountStatus.Unverified);
            
            boolean result = sender.transfer(receiver.getCardNumber(), 200, "Fail Transfer");
            
            assertFalse(result);
            assertEquals(1000, sender.getBalance());
            assertEquals(500, receiver.getBalance());
        }
        
        @Test
        @DisplayName("BB18 - Transfer fails when sender is suspended")
        void testTransferFailsWhenSenderSuspended() {
            sender.setStatus(AccountStatus.Suspended);
            
            boolean result = sender.transfer(receiver.getCardNumber(), 200, "Fail Transfer");
            
            assertFalse(result);
            assertEquals(1000, sender.getBalance());
        }
        
        @Test
        @DisplayName("BB19 - Transfer fails with insufficient balance")
        void testTransferFailsWithInsufficientBalance() {
            boolean result = sender.transfer(receiver.getCardNumber(), 1500, "Insufficient");
            
            assertFalse(result);
            assertEquals(1000, sender.getBalance());
            assertEquals(500, receiver.getBalance());
        }
        
        @Test
        @DisplayName("BB20 - Transfer fails if recipient not found")
        void testTransferFailsIfRecipientNotFound() {
            boolean result = sender.transfer("9999 9999 9999 9999", 100, "Invalid Recipient");
            
            assertFalse(result);
            assertEquals(1000, sender.getBalance());
        }
        
        @Test
        @DisplayName("BB21 - Transfer fails if recipient account is closed")
        void testTransferFailsIfRecipientClosed() {
            receiver.setStatus(AccountStatus.Closed);
            
            boolean result = sender.transfer(receiver.getCardNumber(), 100, "Closed Recipient");
            
            assertFalse(result);
            assertEquals(1000, sender.getBalance());
        }
        
        @Test
        @DisplayName("BB22 - Transfer with zero amount should fail")
        void testTransferZeroAmount() {
            boolean result = sender.transfer(receiver.getCardNumber(), 0, "Zero Transfer");
            
            assertFalse(result);
        }
        
        @Test
        @DisplayName("BB23 - Transfer with negative amount should fail")
        void testTransferNegativeAmount() {
            boolean result = sender.transfer(receiver.getCardNumber(), -100, "Negative Transfer");
            
            assertFalse(result);
        }
    }
}