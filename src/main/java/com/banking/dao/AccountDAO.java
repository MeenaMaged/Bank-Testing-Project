package com.banking.dao;

import com.banking.entities.Account;
import java.util.HashMap;
import java.util.Map;

/**
 * Data Access Object for Account management
 */
public class AccountDAO {
    
    private static Map<Integer, Account> accounts = new HashMap<>();
    private static Map<String, Account> cardNumberIndex = new HashMap<>();
    
    /**
     * Add account to storage
     * @param account Account to add
     */
    public static void add(Account account) {
        accounts.put(account.getId(), account);
        cardNumberIndex.put(account.getCardNumber(), account);
    }
    
    /**
     * Find account by ID
     * @param id Account ID
     * @return Account or null if not found
     */
    public static Account findById(int id) {
        return accounts.get(id);
    }
    
    /**
     * Find account by card number
     * @param cardNumber Card number
     * @return Account or null if not found
     */
    public static Account findByCardNumber(String cardNumber) {
        return cardNumberIndex.get(cardNumber);
    }
    
    /**
     * Remove account from storage
     * @param id Account ID
     */
    public static void remove(int id) {
        Account account = accounts.remove(id);
        if (account != null) {
            cardNumberIndex.remove(account.getCardNumber());
        }
    }
    
    /**
     * Clear all accounts (for testing)
     */
    public static void clear() {
        accounts.clear();
        cardNumberIndex.clear();
    }
    
    /**
     * Get all accounts count
     * @return Number of accounts
     */
    public static int count() {
        return accounts.size();
    }
}