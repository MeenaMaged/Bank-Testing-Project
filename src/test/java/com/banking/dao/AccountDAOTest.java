package com.banking.dao;

import com.banking.entities.Account;
import com.banking.entities.Account.AccountStatus;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Additional tests for AccountDAO to improve code coverage
 * Tests edge cases and error handling in data access layer
 */
@DisplayName("AccountDAO Additional Tests")
public class AccountDAOTest {
    
    @BeforeEach
    void setUp() {
        AccountDAO.clear();
    }
    
    // ==================== Basic CRUD Operations ====================
    
    @Nested
    @DisplayName("CRUD Operations Tests")
    class CRUDOperationsTests {
        
        @Test
        @DisplayName("DAO01 - Add account successfully")
        void testAddAccount() {
            Account account = new Account(1, "Test User", 1000);
            AccountDAO.add(account);
            
            Account retrieved = AccountDAO.findById(1);
            assertNotNull(retrieved);
            assertEquals("Test User", retrieved.getClientName());
        }
        
        @Test
        @DisplayName("DAO02 - Find by ID returns null for non-existent account")
        void testFindByIdNotFound() {
            Account result = AccountDAO.findById(999);
            assertNull(result);
        }
        
        @Test
        @DisplayName("DAO03 - Find by card number successfully")
        void testFindByCardNumber() {
            Account account = new Account(1, "Test User", 1000);
            AccountDAO.add(account);
            
            Account retrieved = AccountDAO.findByCardNumber(account.getCardNumber());
            assertNotNull(retrieved);
            assertEquals(1, retrieved.getId());
        }
        
        @Test
        @DisplayName("DAO04 - Find by card number returns null for non-existent")
        void testFindByCardNumberNotFound() {
            Account result = AccountDAO.findByCardNumber("9999 9999 9999 9999");
            assertNull(result);
        }
        
        @Test
        @DisplayName("DAO05 - Clear removes all accounts")
        void testClearAccounts() {
            AccountDAO.add(new Account(1, "User 1", 100));
            AccountDAO.add(new Account(2, "User 2", 200));
            AccountDAO.add(new Account(3, "User 3", 300));
            
            AccountDAO.clear();
            
            assertNull(AccountDAO.findById(1));
            assertNull(AccountDAO.findById(2));
            assertNull(AccountDAO.findById(3));
        }
    }
    
    // ==================== Edge Cases ====================
    
    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {
        
        @Test
        @DisplayName("DAO06 - Add multiple accounts with different IDs")
        void testAddMultipleAccounts() {
            Account acc1 = new Account(1, "User 1", 100);
            Account acc2 = new Account(2, "User 2", 200);
            Account acc3 = new Account(3, "User 3", 300);
            
            AccountDAO.add(acc1);
            AccountDAO.add(acc2);
            AccountDAO.add(acc3);
            
            assertNotNull(AccountDAO.findById(1));
            assertNotNull(AccountDAO.findById(2));
            assertNotNull(AccountDAO.findById(3));
        }
        
        @Test
        @DisplayName("DAO07 - Find account with zero ID")
        void testFindByZeroId() {
            Account account = new Account(0, "Zero ID User", 500);
            AccountDAO.add(account);
            
            Account retrieved = AccountDAO.findById(0);
            assertNotNull(retrieved);
            assertEquals("Zero ID User", retrieved.getClientName());
        }
        
        @Test
        @DisplayName("DAO08 - Find account with negative ID")
        void testFindByNegativeId() {
            Account result = AccountDAO.findById(-1);
            assertNull(result);
        }
        
        @Test
        @DisplayName("DAO09 - Add account with same ID overwrites")
        void testAddDuplicateId() {
            Account acc1 = new Account(1, "Original User", 100);
            Account acc2 = new Account(1, "New User", 200);
            
            AccountDAO.add(acc1);
            AccountDAO.add(acc2);
            
            Account retrieved = AccountDAO.findById(1);
            assertNotNull(retrieved);
        }
        
        @Test
        @DisplayName("DAO10 - Find by null card number")
        void testFindByNullCardNumber() {
            Account result = AccountDAO.findByCardNumber(null);
            assertNull(result);
        }
        
        @Test
        @DisplayName("DAO11 - Find by empty card number")
        void testFindByEmptyCardNumber() {
            Account result = AccountDAO.findByCardNumber("");
            assertNull(result);
        }
    }
    
    // ==================== Data Integrity Tests ====================
    
    @Nested
    @DisplayName("Data Integrity Tests")
    class DataIntegrityTests {
        
        @Test
        @DisplayName("DAO12 - Retrieved account maintains data integrity")
        void testDataIntegrity() {
            Account original = new Account(1, "Integrity Test", 5000);
            original.setStatus(AccountStatus.Verified);
            AccountDAO.add(original);
            
            Account retrieved = AccountDAO.findById(1);
            
            assertEquals(original.getId(), retrieved.getId());
            assertEquals(original.getClientName(), retrieved.getClientName());
            assertEquals(original.getBalance(), retrieved.getBalance());
            assertEquals(original.getStatus(), retrieved.getStatus());
            assertEquals(original.getCardNumber(), retrieved.getCardNumber());
        }
        
        @Test
        @DisplayName("DAO13 - Modifications to retrieved account persist")
        void testModificationPersistence() {
            Account account = new Account(1, "Test User", 1000);
            AccountDAO.add(account);
            
            Account retrieved = AccountDAO.findById(1);
            retrieved.deposit(500);
            retrieved.setStatus(AccountStatus.Verified);
            
            Account retrievedAgain = AccountDAO.findById(1);
            assertEquals(1500, retrievedAgain.getBalance());
            assertEquals(AccountStatus.Verified, retrievedAgain.getStatus());
        }
        
        @Test
        @DisplayName("DAO14 - Card number format is consistent")
        void testCardNumberFormat() {
            Account account = new Account(42, "Format Test", 1000);
            AccountDAO.add(account);
            
            String cardNumber = account.getCardNumber();
            assertTrue(cardNumber.matches("\\d{4} \\d{4} \\d{4} \\d{4}"));
        }
    }
    
    // ==================== Multiple Operations Tests ====================
    
    @Nested
    @DisplayName("Multiple Operations Tests")
    class MultipleOperationsTests {
        
        @Test
        @DisplayName("DAO15 - Sequential add and find operations")
        void testSequentialOperations() {
            for (int i = 1; i <= 10; i++) {
                AccountDAO.add(new Account(i, "User " + i, i * 100));
            }
            
            for (int i = 1; i <= 10; i++) {
                Account acc = AccountDAO.findById(i);
                assertNotNull(acc);
                assertEquals("User " + i, acc.getClientName());
                assertEquals(i * 100, acc.getBalance());
            }
        }
        
        @Test
        @DisplayName("DAO16 - Clear and repopulate")
        void testClearAndRepopulate() {
            AccountDAO.add(new Account(1, "First", 100));
            assertNotNull(AccountDAO.findById(1));
            
            AccountDAO.clear();
            assertNull(AccountDAO.findById(1));
            
            AccountDAO.add(new Account(1, "Second", 200));
            Account retrieved = AccountDAO.findById(1);
            assertNotNull(retrieved);
            assertEquals("Second", retrieved.getClientName());
            assertEquals(200, retrieved.getBalance());
        }
    }
}
