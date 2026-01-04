package com.banking.services;

import com.banking.dao.AccountDAO;
import com.banking.entities.Account;
import com.banking.entities.Account.AccountStatus;

/**
 * Service class for account management
 */
public class AccountService {
    
    /**
     * Create new account
     * @param id Account ID
     * @param clientName Client name
     * @param initialBalance Initial balance
     * @return Created account
     */
    public Account createAccount(int id, String clientName, double initialBalance) {
        Account account = new Account(id, clientName, initialBalance);
        AccountDAO.add(account);
        return account;
    }
    
    /**
     * Get account by ID
     * @param id Account ID
     * @return Account or null
     */
    public Account getAccount(int id) {
        return AccountDAO.findById(id);
    }
    
    /**
     * Generate account statement
     * @param account Account
     * @return Statement string
     */
    public String generateStatement(Account account) {
        if (account == null) {
            return "Account not found";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== ACCOUNT STATEMENT ===\n");
        sb.append("Client Name: ").append(account.getClientName()).append("\n");
        sb.append("Card Number: ").append(account.getCardNumber()).append("\n");
        sb.append("Balance: $").append(String.format("%.2f", account.getBalance())).append("\n");
        sb.append("Status: ").append(account.getStatus()).append("\n");
        sb.append("========================\n");
        
        return sb.toString();
    }
    
    /**
     * Check if operation is allowed for account
     * @param account Account
     * @param operation Operation type
     * @return true if allowed
     */
    public boolean isOperationAllowed(Account account, String operation) {
        if (account == null) {
            return false;
        }
        
        AccountStatus status = account.getStatus();
        
        switch (operation.toLowerCase()) {
            case "deposit":
                return status != AccountStatus.Closed;
            case "withdraw":
            case "transfer":
                return status == AccountStatus.Verified;
            case "view":
                return true;
            default:
                return false;
        }
    }
    
    /**
     * Verify account (admin action)
     * @param id Account ID
     * @return true if successful
     */
    public boolean verifyAccount(int id) {
        Account account = AccountDAO.findById(id);
        return account != null && account.verify();
    }
    
    /**
     * Suspend account (admin action)
     * @param id Account ID
     * @return true if successful
     */
    public boolean suspendAccount(int id) {
        Account account = AccountDAO.findById(id);
        return account != null && account.suspend();
    }
    
    /**
     * Close account (admin action)
     * @param id Account ID
     * @return true if successful
     */
    public boolean closeAccount(int id) {
        Account account = AccountDAO.findById(id);
        return account != null && account.close();
    }
}