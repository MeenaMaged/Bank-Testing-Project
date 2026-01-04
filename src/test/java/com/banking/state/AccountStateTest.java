package com.banking.state;

import com.banking.dao.AccountDAO;
import com.banking.entities.Account;
import com.banking.entities.Account.AccountStatus;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * State-based testing for Account lifecycle
 * Tests all state transitions and state-dependent behavior
 */
@DisplayName("Account State-Based Tests")
public class AccountStateTest {
    
    private Account account;
    
    @BeforeEach
    void setUp() {
        AccountDAO.clear();
        account = new Account(1, "Test User", 1000);
    }
    
    // ==================== State Transition Matrix Tests ====================
    
    @Nested
    @DisplayName("Valid State Transitions")
    class ValidStateTransitions {
        
        @Test
        @DisplayName("ST01 - Unverified -> Verified (verify)")
        void testUnverifiedToVerified() {
            assertEquals(AccountStatus.Unverified, account.getStatus());
            
            assertTrue(account.verify());
            
            assertEquals(AccountStatus.Verified, account.getStatus());
        }
        
        @Test
        @DisplayName("ST02 - Verified -> Suspended (suspend)")
        void testVerifiedToSuspended() {
            account.verify();
            assertEquals(AccountStatus.Verified, account.getStatus());
            
            assertTrue(account.suspend());
            
            assertEquals(AccountStatus.Suspended, account.getStatus());
        }
        
        @Test
        @DisplayName("ST03 - Suspended -> Verified (appeal)")
        void testSuspendedToVerified() {
            account.verify();
            account.suspend();
            assertEquals(AccountStatus.Suspended, account.getStatus());
            
            assertTrue(account.appeal());
            
            assertEquals(AccountStatus.Verified, account.getStatus());
        }
        
        @Test
        @DisplayName("ST04 - Verified -> Closed (close)")
        void testVerifiedToClosed() {
            account.verify();
            
            assertTrue(account.close());
            
            assertEquals(AccountStatus.Closed, account.getStatus());
        }
        
        @Test
        @DisplayName("ST05 - Suspended -> Closed (close)")
        void testSuspendedToClosed() {
            account.verify();
            account.suspend();
            
            assertTrue(account.close());
            
            assertEquals(AccountStatus.Closed, account.getStatus());
        }
        
        @Test
        @DisplayName("ST06 - Unverified -> Closed (close)")
        void testUnverifiedToClosed() {
            assertTrue(account.close());
            
            assertEquals(AccountStatus.Closed, account.getStatus());
        }
    }
    
    // ==================== Invalid State Transitions ====================
    
    @Nested
    @DisplayName("Invalid State Transitions")
    class InvalidStateTransitions {
        
        @Test
        @DisplayName("ST07 - Verify already verified account should fail")
        void testVerifyAlreadyVerified() {
            account.verify();
            
            assertFalse(account.verify());
            
            assertEquals(AccountStatus.Verified, account.getStatus());
        }
        
        @Test
        @DisplayName("ST08 - Suspend unverified account should fail")
        void testSuspendUnverified() {
            assertFalse(account.suspend());
            
            assertEquals(AccountStatus.Unverified, account.getStatus());
        }
        
        @Test
        @DisplayName("ST09 - Appeal verified account should fail")
        void testAppealVerified() {
            account.verify();
            
            assertFalse(account.appeal());
            
            assertEquals(AccountStatus.Verified, account.getStatus());
        }
        
        @Test
        @DisplayName("ST10 - Appeal unverified account should fail")
        void testAppealUnverified() {
            assertFalse(account.appeal());
            
            assertEquals(AccountStatus.Unverified, account.getStatus());
        }
        
        @Test
        @DisplayName("ST11 - Verify closed account should fail")
        void testVerifyClosed() {
            account.close();
            
            assertFalse(account.verify());
            
            assertEquals(AccountStatus.Closed, account.getStatus());
        }
        
        @Test
        @DisplayName("ST12 - Suspend closed account should fail")
        void testSuspendClosed() {
            account.verify();
            account.close();
            
            assertFalse(account.suspend());
            
            assertEquals(AccountStatus.Closed, account.getStatus());
        }
        
        @Test
        @DisplayName("ST13 - Appeal closed account should fail")
        void testAppealClosed() {
            account.verify();
            account.close();
            
            assertFalse(account.appeal());
            
            assertEquals(AccountStatus.Closed, account.getStatus());
        }
        
        @Test
        @DisplayName("ST14 - Close already closed account should fail")
        void testCloseAlreadyClosed() {
            account.close();
            
            assertFalse(account.close());
            
            assertEquals(AccountStatus.Closed, account.getStatus());
        }
    }
    
    // ==================== State-Dependent Operations ====================
    
    @Nested
    @DisplayName("State-Dependent Operations")
    class StateDependentOperations {
        
        @Test
        @DisplayName("ST15 - Unverified: deposit allowed, withdraw blocked")
        void testUnverifiedOperations() {
            assertTrue(account.deposit(100));
            assertFalse(account.withdraw(50));
            assertEquals(1100, account.getBalance());
        }
        
        @Test
        @DisplayName("ST16 - Verified: all operations allowed")
        void testVerifiedOperations() {
            account.verify();
            
            assertTrue(account.deposit(100));
            assertTrue(account.withdraw(200));
            assertEquals(900, account.getBalance());
        }
        
        @Test
        @DisplayName("ST17 - Suspended: deposit allowed, withdraw blocked")
        void testSuspendedOperations() {
            account.verify();
            account.suspend();
            
            assertTrue(account.deposit(100));
            assertFalse(account.withdraw(50));
            assertEquals(1100, account.getBalance());
        }
        
        @Test
        @DisplayName("ST18 - Closed: all operations blocked")
        void testClosedOperations() {
            account.verify();
            account.close();
            
            assertFalse(account.deposit(100));
            assertFalse(account.withdraw(50));
            assertEquals(1000, account.getBalance());
        }
        
        @Test
        @DisplayName("ST19 - Transfer only allowed in Verified state")
        void testTransferStateRestrictions() {
            Account receiver = new Account(2, "Receiver", 500);
            receiver.setStatus(AccountStatus.Verified);
            AccountDAO.add(account);
            AccountDAO.add(receiver);
            
            // Unverified - should fail
            assertFalse(account.transfer(receiver.getCardNumber(), 100, "Test"));
            
            // Verified - should succeed
            account.verify();
            assertTrue(account.transfer(receiver.getCardNumber(), 100, "Test"));
            
            // Suspended - should fail
            account.suspend();
            assertFalse(account.transfer(receiver.getCardNumber(), 100, "Test"));
        }
    }
    
    // ==================== Complete Lifecycle Scenarios ====================
    
    @Nested
    @DisplayName("Lifecycle Scenarios")
    class LifecycleScenarios {
        
        @Test
        @DisplayName("ST20 - Complete account lifecycle")
        void testCompleteLifecycle() {
            // Initial state
            assertEquals(AccountStatus.Unverified, account.getStatus());
            
            // Verify
            account.verify();
            assertEquals(AccountStatus.Verified, account.getStatus());
            
            // Perform transactions
            assertTrue(account.deposit(500));
            assertTrue(account.withdraw(200));
            assertEquals(1300, account.getBalance());
            
            // Suspend
            account.suspend();
            assertEquals(AccountStatus.Suspended, account.getStatus());
            
            // Try transaction while suspended
            assertFalse(account.withdraw(100));
            
            // Appeal
            account.appeal();
            assertEquals(AccountStatus.Verified, account.getStatus());
            
            // Resume transactions
            assertTrue(account.withdraw(100));
            assertEquals(1200, account.getBalance());
            
            // Close
            account.close();
            assertEquals(AccountStatus.Closed, account.getStatus());
            
            // All operations blocked
            assertFalse(account.deposit(100));
            assertFalse(account.withdraw(100));
        }
        
        @Test
        @DisplayName("ST21 - Suspension and recovery scenario")
        void testSuspensionRecovery() {
            account.verify();
            account.deposit(500);
            
            double balanceBeforeSuspension = account.getBalance();
            
            // Suspend
            account.suspend();
            
            // Deposit still works
            assertTrue(account.deposit(100));
            
            // Withdraw blocked
            assertFalse(account.withdraw(100));
            
            // Appeal and recover
            account.appeal();
            
            // Withdraw now works
            assertTrue(account.withdraw(100));
            
            assertEquals(balanceBeforeSuspension + 100 - 100, account.getBalance());
        }
        
        @Test
        @DisplayName("ST22 - Violation scenario - suspended account transaction attempt")
        void testViolationScenario() {
            account.verify();
            account.deposit(500);
            account.suspend();
            
            double balanceBefore = account.getBalance();
            
            // Attempt withdrawal (violation)
            boolean result = account.withdraw(100);
            
            assertFalse(result);
            assertEquals(balanceBefore, account.getBalance());
            assertEquals(AccountStatus.Suspended, account.getStatus());
        }
    }
}