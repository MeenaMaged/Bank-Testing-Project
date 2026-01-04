package com.banking.controllers;

import com.banking.entities.Account;
import com.banking.services.AccountService;
import com.banking.services.TransactionService;

/**
 * Controller for client operations
 */
public class ClientController {
    
    private final AccountService accountService;
    private final TransactionService transactionService;
    
    public ClientController() {
        this.accountService = new AccountService();
        this.transactionService = new TransactionService();
    }
    
    public ClientController(AccountService accountService, TransactionService transactionService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
    }
    
    /**
     * Create new account
     */
    public Account createAccount(int id, String clientName, double initialBalance) {
        return accountService.createAccount(id, clientName, initialBalance);
    }
    
    /**
     * Process deposit
     */
    public String processDeposit(Account account, double amount) {
        boolean success = transactionService.processDeposit(account, amount);
        return success ? "Deposit successful" : "Deposit failed";
    }
    
    /**
     * Process withdrawal
     */
    public String processWithdrawal(Account account, double amount) {
        boolean success = transactionService.processWithdrawal(account, amount);
        return success ? "Withdrawal successful" : "Withdrawal failed";
    }
    
    /**
     * Process transfer
     */
    public String processTransfer(Account sender, String recipientCardNumber, 
                                  double amount, String description) {
        boolean success = transactionService.processTransfer(sender, recipientCardNumber, 
                                                             amount, description);
        return success ? "Transfer successful" : "Transfer failed";
    }
    
    /**
     * Get account statement
     */
    public String getAccountStatement(Account account) {
        return accountService.generateStatement(account);
    }
    
    /**
     * Validate operation
     */
    public boolean validateOperation(Account account, String operation) {
        return accountService.isOperationAllowed(account, operation);
    }
}