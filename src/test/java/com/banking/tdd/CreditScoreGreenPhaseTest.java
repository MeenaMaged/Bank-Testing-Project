package com.banking.tdd;

import com.banking.entities.CreditScoreAccount;
import com.banking.entities.Account.AccountStatus;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD GREEN PHASE Tests
 * These tests verify the implemented Credit Score feature
 */
@DisplayName("TDD Green Phase - Credit Score Implementation Tests")
public class CreditScoreGreenPhaseTest {
    
    private CreditScoreAccount account;
    
    @BeforeEach
    void setUp() {
        account = new CreditScoreAccount(1, "TDD Test User", 1000);
        account.setStatus(AccountStatus.Verified);
    }
    
    // ==================== Credit Score Calculation Tests ====================
    
    @Nested
    @DisplayName("Credit Score Calculation (GREEN)")
    class CreditScoreCalculationGreen {
        
        @Test
        @DisplayName("TDD01-G - Initial credit score should be 700")
        void testInitialCreditScore() {
            assertEquals(700, account.getCreditScore());
        }
        
        @Test
        @DisplayName("TDD02-G - Credit score should improve after multiple deposits")
        void testCreditScoreAfterDeposits() {
            int initialScore = account.getCreditScore();
            
            // Make 3 deposits to trigger score improvement
            account.deposit(100);
            account.deposit(100);
            account.deposit(100);
            
            assertTrue(account.getCreditScore() > initialScore);
        }
        
        @Test
        @DisplayName("TDD03-G - Credit score should decrease after overdraft attempt")
        void testCreditScoreAfterOverdraftAttempt() {
            int initialScore = account.getCreditScore();
            
            // Attempt overdraft
            account.withdraw(2000);
            
            assertTrue(account.getCreditScore() < initialScore);
        }
    }
    
    // ==================== Credit Score Based Limits Tests ====================
    
    @Nested
    @DisplayName("Credit Score Based Limits (GREEN)")
    class CreditScoreBasedLimitsGreen {
        
        @Test
        @DisplayName("TDD05-G - Transaction limit should be based on credit score")
        void testTransactionLimitBasedOnScore() {
            // Score 700 = $7000 limit
            assertEquals(7000.0, account.getTransactionLimit());
        }
        
        @Test
        @DisplayName("TDD07-G - Transaction should be blocked if exceeds credit-based limit")
        void testTransactionBlockedByLimit() {
            account.deposit(10000); // Add enough balance
            
            // Try to withdraw more than credit limit allows
            boolean result = account.withdrawWithCreditCheck(8000);
            
            assertFalse(result);
        }
        
        @Test
        @DisplayName("TDD07-G2 - Transaction within limit should succeed")
        void testTransactionWithinLimit() {
            account.deposit(5000);
            
            boolean result = account.withdrawWithCreditCheck(3000);
            
            assertTrue(result);
        }
    }
    
    // ==================== Credit Score Updates Tests ====================
    
    @Nested
    @DisplayName("Credit Score Updates (GREEN)")
    class CreditScoreUpdatesGreen {
        
        @Test
        @DisplayName("TDD09-G - Account suspension should decrease credit score")
        void testScoreDecreaseAfterSuspension() {
            int beforeSuspension = account.getCreditScore();
            
            account.suspend();
            
            assertTrue(account.getCreditScore() < beforeSuspension);
        }
        
        @Test
        @DisplayName("TDD10-G - Successful appeal should partially restore credit score")
        void testScoreAfterAppeal() {
            account.suspend();
            int suspendedScore = account.getCreditScore();
            
            account.appeal();
            
            assertTrue(account.getCreditScore() > suspendedScore);
        }
        
        @Test
        @DisplayName("TDD-G - Credit score should stay within bounds (300-850)")
        void testCreditScoreBounds() {
            // Try to decrease score significantly
            for (int i = 0; i < 50; i++) {
                account.withdraw(10000); // Overdraft attempts
            }
            
            assertTrue(account.getCreditScore() >= 300);
            assertTrue(account.getCreditScore() <= 850);
        }
    }
    
    // ==================== Recalculation Tests ====================
    
    @Nested
    @DisplayName("Credit Score Recalculation")
    class CreditScoreRecalculation {
        
        @Test
        @DisplayName("Recalculate should consider balance")
        void testRecalculateWithHighBalance() {
            account.deposit(5000); // Total 6000
            account.recalculateCreditScore();
            
            assertTrue(account.getCreditScore() > 700);
        }
        
        @Test
        @DisplayName("Recalculate should consider transaction history")
        void testRecalculateWithTransactionHistory() {
            // Make successful transactions
            for (int i = 0; i < 5; i++) {
                account.deposit(100);
            }
            
            account.recalculateCreditScore();
            
            assertTrue(account.getCreditScore() > 700);
        }
    }
}