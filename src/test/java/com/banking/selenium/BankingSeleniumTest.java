package com.banking.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Selenium WebDriver UI Tests for Banking System
 * Tests the web-based banking interface using browser automation
 * 
 * These tests verify:
 * - Button states based on account status
 * - Input validation
 * - Transaction processing
 * - UI element rendering
 * - User interaction flows
 */
@DisplayName("Selenium UI Automation Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BankingSeleniumTest {
    
    private static WebDriver driver;
    private static WebDriverWait wait;
    private static String testPageUrl;
    
    @BeforeAll
    static void setUpClass() {
        // Setup ChromeDriver automatically
        WebDriverManager.chromedriver().setup();
        
        // Configure Chrome options
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run in headless mode for CI/CD
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        // Get the test page URL
        File testPage = new File("src/test/resources/banking-test-page.html");
        testPageUrl = "file:///" + testPage.getAbsolutePath().replace("\\", "/");
    }
    
    @AfterAll
    static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }
    
    @BeforeEach
    void setUp() {
        driver.get(testPageUrl);
        // Reset to Verified status before each test
        WebElement setVerifiedBtn = driver.findElement(By.id("setVerifiedBtn"));
        setVerifiedBtn.click();
        // Clear amount field
        WebElement amountField = driver.findElement(By.id("amount"));
        amountField.clear();
    }
    
    // ==================== Page Load Tests ====================
    
    @Nested
    @DisplayName("Page Load Tests")
    class PageLoadTests {
        
        @Test
        @Order(1)
        @DisplayName("SEL01 - Page title is correct")
        void testPageTitle() {
            assertEquals("Banking System - Client Dashboard", driver.getTitle());
        }
        
        @Test
        @Order(2)
        @DisplayName("SEL02 - Client name field displays correctly")
        void testClientNameDisplay() {
            WebElement clientName = driver.findElement(By.id("clientName"));
            assertEquals("John Doe", clientName.getAttribute("value"));
            assertFalse(clientName.isEnabled()); // Should be disabled
        }
        
        @Test
        @Order(3)
        @DisplayName("SEL03 - Account number field displays correctly")
        void testAccountNumberDisplay() {
            WebElement accountNumber = driver.findElement(By.id("accountNumber"));
            assertEquals("0001 0001 0001 0001", accountNumber.getAttribute("value"));
            assertFalse(accountNumber.isEnabled()); // Should be disabled
        }
        
        @Test
        @Order(4)
        @DisplayName("SEL04 - Initial balance displays correctly")
        void testInitialBalanceDisplay() {
            WebElement balance = driver.findElement(By.id("balance"));
            assertTrue(balance.getAttribute("value").contains("1000.00"));
        }
        
        @Test
        @Order(5)
        @DisplayName("SEL05 - All operation buttons are present")
        void testOperationButtonsPresent() {
            assertNotNull(driver.findElement(By.id("depositBtn")));
            assertNotNull(driver.findElement(By.id("withdrawBtn")));
            assertNotNull(driver.findElement(By.id("transferBtn")));
            assertNotNull(driver.findElement(By.id("viewStatementBtn")));
        }
    }
    
    // ==================== Button State Tests ====================
    
    @Nested
    @DisplayName("Button State Tests Based on Account Status")
    class ButtonStateTests {
        
        @Test
        @Order(10)
        @DisplayName("SEL06 - Verified status: all buttons enabled")
        void testVerifiedStatusButtons() {
            // Click Set Verified
            driver.findElement(By.id("setVerifiedBtn")).click();
            
            // Check button states
            assertTrue(driver.findElement(By.id("depositBtn")).isEnabled());
            assertTrue(driver.findElement(By.id("withdrawBtn")).isEnabled());
            assertTrue(driver.findElement(By.id("transferBtn")).isEnabled());
            assertTrue(driver.findElement(By.id("viewStatementBtn")).isEnabled());
        }
        
        @Test
        @Order(11)
        @DisplayName("SEL07 - Suspended status: withdraw/transfer disabled")
        void testSuspendedStatusButtons() {
            // Click Set Suspended
            driver.findElement(By.id("setSuspendedBtn")).click();
            
            // Check button states
            assertTrue(driver.findElement(By.id("depositBtn")).isEnabled());
            assertFalse(driver.findElement(By.id("withdrawBtn")).isEnabled());
            assertFalse(driver.findElement(By.id("transferBtn")).isEnabled());
            assertTrue(driver.findElement(By.id("viewStatementBtn")).isEnabled());
        }
        
        @Test
        @Order(12)
        @DisplayName("SEL08 - Closed status: all transaction buttons disabled")
        void testClosedStatusButtons() {
            // Click Set Closed
            driver.findElement(By.id("setClosedBtn")).click();
            
            // Check button states
            assertFalse(driver.findElement(By.id("depositBtn")).isEnabled());
            assertFalse(driver.findElement(By.id("withdrawBtn")).isEnabled());
            assertFalse(driver.findElement(By.id("transferBtn")).isEnabled());
            assertTrue(driver.findElement(By.id("viewStatementBtn")).isEnabled());
        }
        
        @Test
        @Order(13)
        @DisplayName("SEL09 - Unverified status: withdraw/transfer disabled")
        void testUnverifiedStatusButtons() {
            // Click Set Unverified
            driver.findElement(By.id("setUnverifiedBtn")).click();
            
            // Check button states
            assertTrue(driver.findElement(By.id("depositBtn")).isEnabled());
            assertFalse(driver.findElement(By.id("withdrawBtn")).isEnabled());
            assertFalse(driver.findElement(By.id("transferBtn")).isEnabled());
            assertTrue(driver.findElement(By.id("viewStatementBtn")).isEnabled());
        }
    }
    
    // ==================== Status Label Tests ====================
    
    @Nested
    @DisplayName("Status Label Display Tests")
    class StatusLabelTests {
        
        @Test
        @Order(20)
        @DisplayName("SEL10 - Status label shows Verified")
        void testVerifiedStatusLabel() {
            driver.findElement(By.id("setVerifiedBtn")).click();
            
            WebElement statusLabel = driver.findElement(By.id("statusLabel"));
            assertEquals("Verified", statusLabel.getText());
            assertTrue(statusLabel.getAttribute("class").contains("status-verified"));
        }
        
        @Test
        @Order(21)
        @DisplayName("SEL11 - Status label shows Suspended")
        void testSuspendedStatusLabel() {
            driver.findElement(By.id("setSuspendedBtn")).click();
            
            WebElement statusLabel = driver.findElement(By.id("statusLabel"));
            assertEquals("Suspended", statusLabel.getText());
            assertTrue(statusLabel.getAttribute("class").contains("status-suspended"));
        }
        
        @Test
        @Order(22)
        @DisplayName("SEL12 - Status label shows Closed")
        void testClosedStatusLabel() {
            driver.findElement(By.id("setClosedBtn")).click();
            
            WebElement statusLabel = driver.findElement(By.id("statusLabel"));
            assertEquals("Closed", statusLabel.getText());
            assertTrue(statusLabel.getAttribute("class").contains("status-closed"));
        }
        
        @Test
        @Order(23)
        @DisplayName("SEL13 - Status label shows Unverified")
        void testUnverifiedStatusLabel() {
            driver.findElement(By.id("setUnverifiedBtn")).click();
            
            WebElement statusLabel = driver.findElement(By.id("statusLabel"));
            assertEquals("Unverified", statusLabel.getText());
            assertTrue(statusLabel.getAttribute("class").contains("status-unverified"));
        }
    }
    
    // ==================== Transaction Tests ====================
    
    @Nested
    @DisplayName("Transaction Processing Tests")
    class TransactionTests {
        
        @Test
        @Order(30)
        @DisplayName("SEL14 - Successful deposit updates balance")
        void testSuccessfulDeposit() {
            // Enter amount
            WebElement amountField = driver.findElement(By.id("amount"));
            amountField.sendKeys("500");
            
            // Click deposit
            driver.findElement(By.id("depositBtn")).click();
            
            // Verify balance updated
            WebElement balance = driver.findElement(By.id("balance"));
            assertEquals("$1500.00", balance.getAttribute("value"));
            
            // Verify success notification
            WebElement notification = driver.findElement(By.id("notification"));
            assertTrue(notification.getText().contains("Deposit successful"));
            assertTrue(notification.getAttribute("class").contains("notification-success"));
        }
        
        @Test
        @Order(31)
        @DisplayName("SEL15 - Successful withdrawal updates balance")
        void testSuccessfulWithdrawal() {
            // Ensure Verified status
            driver.findElement(By.id("setVerifiedBtn")).click();
            
            // Enter amount
            WebElement amountField = driver.findElement(By.id("amount"));
            amountField.sendKeys("200");
            
            // Click withdraw
            driver.findElement(By.id("withdrawBtn")).click();
            
            // Verify balance updated
            WebElement balance = driver.findElement(By.id("balance"));
            assertEquals("$800.00", balance.getAttribute("value"));
            
            // Verify success notification
            WebElement notification = driver.findElement(By.id("notification"));
            assertTrue(notification.getText().contains("Withdrawal successful"));
        }
        
        @Test
        @Order(32)
        @DisplayName("SEL16 - Overdraft withdrawal fails")
        void testOverdraftWithdrawal() {
            // Enter amount greater than balance
            WebElement amountField = driver.findElement(By.id("amount"));
            amountField.sendKeys("5000");
            
            // Click withdraw
            driver.findElement(By.id("withdrawBtn")).click();
            
            // Verify balance unchanged
            WebElement balance = driver.findElement(By.id("balance"));
            assertEquals("$1000.00", balance.getAttribute("value"));
            
            // Verify error notification
            WebElement notification = driver.findElement(By.id("notification"));
            assertTrue(notification.getText().contains("Insufficient funds"));
            assertTrue(notification.getAttribute("class").contains("notification-error"));
        }
        
        @Test
        @Order(33)
        @DisplayName("SEL17 - Withdrawal blocked when suspended")
        void testWithdrawalBlockedWhenSuspended() {
            // Set to suspended
            driver.findElement(By.id("setSuspendedBtn")).click();
            
            // Enter amount
            WebElement amountField = driver.findElement(By.id("amount"));
            amountField.sendKeys("100");
            
            // Withdraw button should be disabled
            WebElement withdrawBtn = driver.findElement(By.id("withdrawBtn"));
            assertFalse(withdrawBtn.isEnabled());
        }
    }
    
    // ==================== Input Validation Tests ====================
    
    @Nested
    @DisplayName("Input Validation Tests")
    class InputValidationTests {
        
        @Test
        @Order(40)
        @DisplayName("SEL18 - Empty amount shows error")
        void testEmptyAmountValidation() {
            // Clear amount field and click deposit
            WebElement amountField = driver.findElement(By.id("amount"));
            amountField.clear();
            
            driver.findElement(By.id("depositBtn")).click();
            
            // Verify error notification
            WebElement notification = driver.findElement(By.id("notification"));
            assertTrue(notification.getText().contains("valid amount"));
            assertTrue(notification.getAttribute("class").contains("notification-error"));
        }
        
        @Test
        @Order(41)
        @DisplayName("SEL19 - Amount field accepts valid input")
        void testValidAmountInput() {
            WebElement amountField = driver.findElement(By.id("amount"));
            amountField.sendKeys("250.50");
            
            assertEquals("250.50", amountField.getAttribute("value"));
        }
        
        @Test
        @Order(42)
        @DisplayName("SEL20 - Amount field clears after transaction")
        void testAmountFieldClearsAfterTransaction() {
            WebElement amountField = driver.findElement(By.id("amount"));
            amountField.sendKeys("100");
            
            driver.findElement(By.id("depositBtn")).click();
            
            // Amount field should be cleared
            assertEquals("", amountField.getAttribute("value"));
        }
    }
    
    // ==================== View Statement Tests ====================
    
    @Nested
    @DisplayName("View Statement Tests")
    class ViewStatementTests {
        
        @Test
        @Order(50)
        @DisplayName("SEL21 - View statement button always enabled")
        void testViewStatementAlwaysEnabled() {
            // Test all statuses
            String[] statuses = {"setVerifiedBtn", "setSuspendedBtn", "setClosedBtn", "setUnverifiedBtn"};
            
            for (String statusBtn : statuses) {
                driver.findElement(By.id(statusBtn)).click();
                assertTrue(driver.findElement(By.id("viewStatementBtn")).isEnabled(),
                        "View Statement should be enabled for all statuses");
            }
        }
        
        @Test
        @Order(51)
        @DisplayName("SEL22 - View statement shows notification")
        void testViewStatementNotification() {
            driver.findElement(By.id("viewStatementBtn")).click();
            
            // Verify notification appears
            WebElement notification = driver.findElement(By.id("notification"));
            assertTrue(notification.isDisplayed());
            assertTrue(notification.getText().contains("Statement"));
        }
    }
    
    // ==================== End-to-End Workflow Tests ====================
    
    @Nested
    @DisplayName("End-to-End Workflow Tests")
    class E2EWorkflowTests {
        
        @Test
        @Order(60)
        @DisplayName("SEL23 - Complete deposit-withdraw workflow")
        void testDepositWithdrawWorkflow() {
            // Deposit
            WebElement amountField = driver.findElement(By.id("amount"));
            amountField.sendKeys("500");
            driver.findElement(By.id("depositBtn")).click();
            
            // Verify balance after deposit
            WebElement balance = driver.findElement(By.id("balance"));
            assertEquals("$1500.00", balance.getAttribute("value"));
            
            // Withdraw
            amountField = driver.findElement(By.id("amount"));
            amountField.sendKeys("300");
            driver.findElement(By.id("withdrawBtn")).click();
            
            // Verify final balance
            balance = driver.findElement(By.id("balance"));
            assertEquals("$1200.00", balance.getAttribute("value"));
        }
        
        @Test
        @Order(61)
        @DisplayName("SEL24 - Status change workflow")
        void testStatusChangeWorkflow() {
            WebElement statusLabel = driver.findElement(By.id("statusLabel"));
            
            // Start Verified -> Suspended
            driver.findElement(By.id("setSuspendedBtn")).click();
            assertEquals("Suspended", statusLabel.getText());
            assertFalse(driver.findElement(By.id("withdrawBtn")).isEnabled());
            
            // Suspended -> Verified (appeal)
            driver.findElement(By.id("setVerifiedBtn")).click();
            assertEquals("Verified", statusLabel.getText());
            assertTrue(driver.findElement(By.id("withdrawBtn")).isEnabled());
            
            // Verified -> Closed
            driver.findElement(By.id("setClosedBtn")).click();
            assertEquals("Closed", statusLabel.getText());
            assertFalse(driver.findElement(By.id("depositBtn")).isEnabled());
        }
    }
}
