package com.banking.services;

import com.banking.entities.Account;
import com.banking.entities.Account.AccountStatus;

/**
 * Service class for transaction processing
 */
public class TransactionService {
    
    /**
     * Process deposit transaction
     * @param account Target account
     * @param amount Amount to deposit
     * @return true if successful
     */
    public boolean processDeposit(Account account, double amount) {
        if (account == null) {
            return false;
        }
        return account.deposit(amount);
    }
    
    /**
     * Process withdrawal transaction
     * @param account Source account
     * @param amount Amount to withdraw
     * @return true if successful
     */
    public boolean processWithdrawal(Account account, double amount) {
        if (account == null) {
            return false;
        }
        return account.withdraw(amount);
    }
    
    /**
     * Process transfer transaction
     * @param sender Sender account
     * @param recipientCardNumber Recipient card number
     * @param amount Amount to transfer
     * @param description Transfer description
     * @return true if successful
     */
    public boolean processTransfer(Account sender, String recipientCardNumber, 
                                   double amount, String description) {
        if (sender == null || recipientCardNumber == null) {
            return false;
        }
        return sender.transfer(recipientCardNumber, amount, description);
    }
    
    /**
     * Validate transaction based on account status and amount
     * @param account Account to validate
     * @param amount Transaction amount
     * @param transactionType Type of transaction
     * @return true if transaction is valid
     */
    public boolean validateTransaction(Account account, double amount, String transactionType) {
        if (account == null || amount <= 0) {
            return false;
        }
        
        double maxLimit = 10000.0;
        if (amount > maxLimit) {
            return false;
        }
        
        AccountStatus status = account.getStatus();
        
        switch (transactionType.toLowerCase()) {
            case "deposit":
                return status != AccountStatus.Closed;
            case "withdraw":
                return status == AccountStatus.Verified && amount <= account.getBalance();
            case "transfer":
                return status == AccountStatus.Verified && amount <= account.getBalance();
            case "view":
                return true;
            default:
                return false;
        }
    }
}