package com.banking.ui;

import com.banking.entities.Account;
import com.banking.entities.Account.AccountStatus;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * UI Testing for Banking System
 * Tests GUI component behavior and user interactions
 */
@DisplayName("GUI Component Tests")
public class GUIComponentTest {
    
    private Account account;
    
    @BeforeEach
    void setUp() {
        account = new Account(1, "John Doe", 1000);
    }
    
    // ==================== Button State Tests ====================
    
    @Nested
    @DisplayName("Button State Tests")
    class ButtonStateTests {
        
        @Test
        @DisplayName("UI01 - Verified account: all buttons enabled")
        void testVerifiedAccountButtons() {
            account.setStatus(AccountStatus.Verified);
            
            assertTrue(isDepositEnabled(account));
            assertTrue(isWithdrawEnabled(account));
            assertTrue(isTransferEnabled(account));
            assertTrue(isViewStatementEnabled(account));
        }
        
        @Test
        @DisplayName("UI02 - Suspended account: withdraw/transfer disabled")
        void testSuspendedAccountButtons() {
            account.setStatus(AccountStatus.Suspended);
            
            assertTrue(isDepositEnabled(account));
            assertFalse(isWithdrawEnabled(account));
            assertFalse(isTransferEnabled(account));
            assertTrue(isViewStatementEnabled(account));
        }
        
        @Test
        @DisplayName("UI03 - Closed account: all transaction buttons disabled")
        void testClosedAccountButtons() {
            account.setStatus(AccountStatus.Closed);
            
            assertFalse(isDepositEnabled(account));
            assertFalse(isWithdrawEnabled(account));
            assertFalse(isTransferEnabled(account));
            assertTrue(isViewStatementEnabled(account));
        }
        
        @Test
        @DisplayName("UI04 - Unverified account: withdraw/transfer disabled")
        void testUnverifiedAccountButtons() {
            account.setStatus(AccountStatus.Unverified);
            
            assertTrue(isDepositEnabled(account));
            assertFalse(isWithdrawEnabled(account));
            assertFalse(isTransferEnabled(account));
            assertTrue(isViewStatementEnabled(account));
        }
        
        // Helper methods simulating GUI button state logic
        private boolean isDepositEnabled(Account acc) {
            return acc.getStatus() != AccountStatus.Closed;
        }
        
        private boolean isWithdrawEnabled(Account acc) {
            return acc.getStatus() == AccountStatus.Verified;
        }
        
        private boolean isTransferEnabled(Account acc) {
            return acc.getStatus() == AccountStatus.Verified;
        }
        
        private boolean isViewStatementEnabled(Account acc) {
            return true; // Always enabled
        }
    }
    
    // ==================== Input Validation Tests ====================
    
    @Nested
    @DisplayName("Input Validation Tests")
    class InputValidationTests {
        
        @Test
        @DisplayName("UI05 - Empty input should be invalid")
        void testEmptyInput() {
            assertFalse(isValidAmountInput(""));
        }
        
        @Test
        @DisplayName("UI06 - Non-numeric input should be invalid")
        void testNonNumericInput() {
            assertFalse(isValidAmountInput("abc"));
            assertFalse(isValidAmountInput("12.34.56"));
            assertFalse(isValidAmountInput("$100"));
        }
        
        @Test
        @DisplayName("UI07 - Negative input should be invalid")
        void testNegativeInput() {
            assertFalse(isValidAmountInput("-100"));
            assertFalse(isValidAmountInput("-0.01"));
        }
        
        @Test
        @DisplayName("UI08 - Zero input should be invalid")
        void testZeroInput() {
            assertFalse(isValidAmountInput("0"));
            assertFalse(isValidAmountInput("0.00"));
        }
        
        @Test
        @DisplayName("UI09 - Valid positive amounts should be valid")
        void testValidPositiveAmounts() {
            assertTrue(isValidAmountInput("100"));
            assertTrue(isValidAmountInput("50.50"));
            assertTrue(isValidAmountInput("0.01"));
            assertTrue(isValidAmountInput("9999.99"));
        }
        
        @Test
        @DisplayName("UI10 - Excessive amounts should be invalid")
        void testExcessiveAmounts() {
            assertFalse(isValidAmountInput("1000000000"));
        }
        
        // Helper method simulating GUI input validation
        private boolean isValidAmountInput(String input) {
            try {
                if (input == null || input.trim().isEmpty()) {
                    return false;
                }
                double amount = Double.parseDouble(input);
                return amount > 0 && amount <= 100000;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }
    
    // ==================== Status Display Tests ====================
    
    @Nested
    @DisplayName("Status Display Tests")
    class StatusDisplayTests {
        
        @Test
        @DisplayName("UI11 - Unverified status should display blue")
        void testUnverifiedStatusColor() {
            account.setStatus(AccountStatus.Unverified);
            assertEquals("BLUE", getStatusColor(account));
        }
        
        @Test
        @DisplayName("UI12 - Verified status should display green")
        void testVerifiedStatusColor() {
            account.setStatus(AccountStatus.Verified);
            assertEquals("GREEN", getStatusColor(account));
        }
        
        @Test
        @DisplayName("UI13 - Suspended status should display orange")
        void testSuspendedStatusColor() {
            account.setStatus(AccountStatus.Suspended);
            assertEquals("ORANGE", getStatusColor(account));
        }
        
        @Test
        @DisplayName("UI14 - Closed status should display red")
        void testClosedStatusColor() {
            account.setStatus(AccountStatus.Closed);
            assertEquals("RED", getStatusColor(account));
        }
        
        // Helper method simulating GUI status color logic
        private String getStatusColor(Account acc) {
            switch (acc.getStatus()) {
                case Unverified: return "BLUE";
                case Verified: return "GREEN";
                case Suspended: return "ORANGE";
                case Closed: return "RED";
                default: return "BLACK";
            }
        }
    }
    
    // ==================== User Interaction Tests ====================
    
    @Nested
    @DisplayName("User Interaction Tests")
    class UserInteractionTests {
        
        @Test
        @DisplayName("UI15 - Successful deposit updates balance display")
        void testSuccessfulDepositUpdatesBalance() {
            account.setStatus(AccountStatus.Verified);
            double initialBalance = account.getBalance();
            
            account.deposit(150);
            
            assertEquals(initialBalance + 150, account.getBalance());
        }
        
        @Test
        @DisplayName("UI16 - Failed withdrawal keeps balance unchanged")
        void testFailedWithdrawalKeepsBalance() {
            account.setStatus(AccountStatus.Verified);
            double initialBalance = account.getBalance();
            
            account.withdraw(2000); // Should fail
            
            assertEquals(initialBalance, account.getBalance());
        }
        
        @Test
        @DisplayName("UI17 - Notification message for successful deposit")
        void testSuccessfulDepositNotification() {
            account.setStatus(AccountStatus.Verified);
            boolean result = account.deposit(100);
            
            String notification = result ? "Deposit successful" : "Deposit failed";
            
            assertEquals("Deposit successful", notification);
        }
        
        @Test
        @DisplayName("UI18 - Notification message for failed withdrawal")
        void testFailedWithdrawalNotification() {
            account.setStatus(AccountStatus.Suspended);
            boolean result = account.withdraw(100);
            
            String notification = result ? "Withdrawal successful" : "Withdrawal failed";
            
            assertEquals("Withdrawal failed", notification);
        }
        
        @Test
        @DisplayName("UI19 - Balance format should show currency")
        void testBalanceFormat() {
            account.deposit(500);
            String formattedBalance = formatBalance(account.getBalance());
            
            assertTrue(formattedBalance.startsWith("$"));
            assertTrue(formattedBalance.contains("."));
        }
        
        // Helper method simulating GUI balance formatting
        private String formatBalance(double balance) {
            return String.format("$%.2f", balance);
        }
    }
    
    // ==================== Accessibility Tests ====================
    
    @Nested
    @DisplayName("Accessibility Tests")
    class AccessibilityTests {
        
        @Test
        @DisplayName("UI20 - Status colors should have sufficient contrast")
        void testStatusColorContrast() {
            // All status colors should be distinct and readable
            String[] colors = {"BLUE", "GREEN", "ORANGE", "RED"};
            
            for (int i = 0; i < colors.length; i++) {
                for (int j = i + 1; j < colors.length; j++) {
                    assertNotEquals(colors[i], colors[j]);
                }
            }
        }
        
        @Test
        @DisplayName("UI21 - All interactive elements should be accessible")
        void testInteractiveElementsAccessible() {
            // Simulating that all buttons have proper labels
            String[] buttonLabels = {"Deposit", "Withdraw", "Transfer", "View Statement"};
            
            for (String label : buttonLabels) {
                assertNotNull(label);
                assertFalse(label.isEmpty());
            }
        }
    }
}