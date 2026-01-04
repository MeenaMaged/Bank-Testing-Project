package com.banking.entities;

import com.banking.dao.AccountDAO;

/**
 * Account entity representing a client bank account with state-based behavior
 */
public class Account {
    
    public enum AccountStatus {
        Unverified,
        Verified,
        Suspended,
        Closed
    }
    
    private int id;
    private String clientName;
    private String cardNumber;
    private double balance;
    private AccountStatus status;
    
    public Account(int id) {
        this.id = id;
        this.cardNumber = generateCardNumber(id);
        this.balance = 0;
        this.status = AccountStatus.Unverified;
    }
    
    public Account(int id, String clientName, double initialBalance) {
        this.id = id;
        this.clientName = clientName;
        this.cardNumber = generateCardNumber(id);
        this.balance = initialBalance;
        this.status = AccountStatus.Unverified;
    }
    
    private String generateCardNumber(int id) {
        return String.format("%04d %04d %04d %04d", id, id, id, id);
    }
    
    /**
     * Deposit money into account
     * @param amount Amount to deposit
     * @return true if successful, false otherwise
     */
    public boolean deposit(double amount) {
        if (status == AccountStatus.Closed) {
            return false;
        }
        if (amount <= 0) {
            return false;
        }
        balance += amount;
        return true;
    }
    
    /**
     * Withdraw money from account
     * @param amount Amount to withdraw
     * @return true if successful, false otherwise
     */
    public boolean withdraw(double amount) {
        if (status == AccountStatus.Closed || status == AccountStatus.Suspended) {
            return false;
        }
        if (status == AccountStatus.Unverified) {
            return false;
        }
        if (amount <= 0 || amount > balance) {
            return false;
        }
        balance -= amount;
        return true;
    }
    
    /**
     * Transfer money to another account
     * @param recipientCardNumber Recipient's card number
     * @param amount Amount to transfer
     * @param description Transfer description
     * @return true if successful, false otherwise
     */
    public boolean transfer(String recipientCardNumber, double amount, String description) {
        if (status != AccountStatus.Verified) {
            return false;
        }
        if (amount <= 0 || amount > balance) {
            return false;
        }
        
        Account recipient = AccountDAO.findByCardNumber(recipientCardNumber);
        if (recipient == null) {
            return false;
        }
        if (recipient.getStatus() == AccountStatus.Closed) {
            return false;
        }
        
        this.balance -= amount;
        recipient.balance += amount;
        return true;
    }
    
    // State transition methods
    
    /**
     * Verify account (admin action)
     * @return true if verification successful
     */
    public boolean verify() {
        if (status == AccountStatus.Unverified) {
            status = AccountStatus.Verified;
            return true;
        }
        return false;
    }
    
    /**
     * Suspend account due to violation
     * @return true if suspension successful
     */
    public boolean suspend() {
        if (status == AccountStatus.Verified) {
            status = AccountStatus.Suspended;
            return true;
        }
        return false;
    }
    
    /**
     * Close account (admin action)
     * @return true if closure successful
     */
    public boolean close() {
        if (status != AccountStatus.Closed) {
            status = AccountStatus.Closed;
            return true;
        }
        return false;
    }
    
    /**
     * Appeal from suspended state
     * @return true if appeal successful
     */
    public boolean appeal() {
        if (status == AccountStatus.Suspended) {
            status = AccountStatus.Verified;
            return true;
        }
        return false;
    }
    
    // Getters and Setters
    
    public int getId() {
        return id;
    }
    
    public String getClientName() {
        return clientName;
    }
    
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
    
    public String getCardNumber() {
        return cardNumber;
    }
    
    public double getBalance() {
        return balance;
    }
    
    public AccountStatus getStatus() {
        return status;
    }
    
    public void setStatus(AccountStatus status) {
        this.status = status;
    }
}