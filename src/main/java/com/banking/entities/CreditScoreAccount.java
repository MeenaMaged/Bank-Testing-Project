package com.banking.entities;

/**
 * Extended Account class with Credit Score feature
 * This is the GREEN PHASE implementation for TDD
 */
public class CreditScoreAccount extends Account {
    
    private int creditScore;
    private int overdraftAttempts;
    private int successfulTransactions;
    
    // Credit score constants
    private static final int INITIAL_SCORE = 700;
    private static final int MIN_SCORE = 300;
    private static final int MAX_SCORE = 850;
    private static final int DEPOSIT_BONUS = 5;
    private static final int OVERDRAFT_PENALTY = 20;
    private static final int SUSPENSION_PENALTY = 50;
    private static final int APPEAL_RECOVERY = 25;
    
    public CreditScoreAccount(int id, String clientName, double initialBalance) {
        super(id, clientName, initialBalance);
        this.creditScore = INITIAL_SCORE;
        this.overdraftAttempts = 0;
        this.successfulTransactions = 0;
    }
    
    /**
     * Get current credit score
     * @return Credit score (300-850)
     */
    public int getCreditScore() {
        return creditScore;
    }
    
    /**
     * Get transaction limit based on credit score
     * @return Maximum allowed transaction amount
     */
    public double getTransactionLimit() {
        // Higher credit score = higher limit
        // Score 700 = $7000 limit
        return creditScore * 10.0;
    }
    
    /**
     * Deposit with credit score update
     */
    @Override
    public boolean deposit(double amount) {
        boolean result = super.deposit(amount);
        if (result) {
            successfulTransactions++;
            // Improve credit score for successful deposits
            if (successfulTransactions % 3 == 0) {
                adjustCreditScore(DEPOSIT_BONUS);
            }
        }
        return result;
    }
    
    /**
     * Withdraw with credit score check
     */
    @Override
    public boolean withdraw(double amount) {
        // Check credit-based limit
        if (amount > getTransactionLimit()) {
            return false;
        }
        
        boolean result = super.withdraw(amount);
        if (!result && amount > getBalance()) {
            // Overdraft attempt - penalize credit score
            overdraftAttempts++;
            adjustCreditScore(-OVERDRAFT_PENALTY);
        } else if (result) {
            successfulTransactions++;
        }
        return result;
    }
    
    /**
     * Withdraw with explicit credit check
     * @param amount Amount to withdraw
     * @return true if successful
     */
    public boolean withdrawWithCreditCheck(double amount) {
        if (amount > getTransactionLimit()) {
            return false;
        }
        return withdraw(amount);
    }
    
    /**
     * Suspend account with credit score penalty
     */
    @Override
    public boolean suspend() {
        boolean result = super.suspend();
        if (result) {
            adjustCreditScore(-SUSPENSION_PENALTY);
        }
        return result;
    }
    
    /**
     * Appeal with partial credit score recovery
     */
    @Override
    public boolean appeal() {
        boolean result = super.appeal();
        if (result) {
            adjustCreditScore(APPEAL_RECOVERY);
        }
        return result;
    }
    
    /**
     * Adjust credit score within bounds
     */
    private void adjustCreditScore(int adjustment) {
        creditScore = Math.max(MIN_SCORE, Math.min(MAX_SCORE, creditScore + adjustment));
    }
    
    /**
     * Calculate credit score based on account factors
     * This is a more sophisticated calculation for the REFACTOR phase
     */
    public void recalculateCreditScore() {
        int baseScore = INITIAL_SCORE;
        
        // Balance factor (higher balance = better score)
        if (getBalance() > 5000) {
            baseScore += 50;
        } else if (getBalance() > 1000) {
            baseScore += 25;
        } else if (getBalance() < 100) {
            baseScore -= 25;
        }
        
        // Transaction history factor
        baseScore += (successfulTransactions * 2);
        baseScore -= (overdraftAttempts * 10);
        
        // Status factor
        switch (getStatus()) {
            case Verified:
                baseScore += 10;
                break;
            case Suspended:
                baseScore -= 30;
                break;
            case Closed:
                baseScore -= 50;
                break;
            default:
                break;
        }
        
        creditScore = Math.max(MIN_SCORE, Math.min(MAX_SCORE, baseScore));
    }
}