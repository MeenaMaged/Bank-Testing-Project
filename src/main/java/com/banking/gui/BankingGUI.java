package com.banking.gui;

import com.banking.controllers.ClientController;
import com.banking.dao.AccountDAO;
import com.banking.entities.Account;
import com.banking.entities.Account.AccountStatus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * GUI interface for the banking system
 * Matches the design shown in the project specification
 */
public class BankingGUI extends JFrame {
    
    private ClientController controller;
    private Account currentAccount;
    
    // Account Info Fields
    private JTextField clientNameField;
    private JTextField accountNumberField;
    private JTextField balanceField;
    private JLabel statusLabel;
    
    // Operation Buttons
    private JButton depositButton;
    private JButton withdrawButton;
    private JButton transferButton;
    private JButton viewStatementButton;
    
    // Input and Notification
    private JTextField amountField;
    private JTextArea notificationArea;
    
    public BankingGUI() {
        controller = new ClientController();
        initializeGUI();
        createSampleAccount();
    }
    
    private void initializeGUI() {
        setTitle("Banking System - Client Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // Create panels
        JPanel accountPanel = createAccountPanel();
        JPanel operationsPanel = createOperationsPanel();
        JPanel notificationPanel = createNotificationPanel();
        
        // Add panels to frame
        add(accountPanel, BorderLayout.NORTH);
        add(operationsPanel, BorderLayout.CENTER);
        add(notificationPanel, BorderLayout.SOUTH);
        
        // Set frame properties
        pack();
        setMinimumSize(new Dimension(450, 400));
        setLocationRelativeTo(null);
    }
    
    private JPanel createAccountPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Client Account"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Client Name
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Client Name:"), gbc);
        gbc.gridx = 1;
        clientNameField = new JTextField(15);
        clientNameField.setEditable(false);
        panel.add(clientNameField, gbc);
        
        // Account Number
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Account Number:"), gbc);
        gbc.gridx = 1;
        accountNumberField = new JTextField(15);
        accountNumberField.setEditable(false);
        panel.add(accountNumberField, gbc);
        
        // Balance
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Balance:"), gbc);
        gbc.gridx = 1;
        balanceField = new JTextField(15);
        balanceField.setEditable(false);
        panel.add(balanceField, gbc);
        
        // Status
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        statusLabel = new JLabel("Verified");
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD));
        panel.add(statusLabel, gbc);
        
        return panel;
    }
    
    private JPanel createOperationsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Operations"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Amount input
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        JPanel amountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        amountPanel.add(new JLabel("Amount: $"));
        amountField = new JTextField(10);
        amountPanel.add(amountField);
        panel.add(amountPanel, gbc);
        
        // Buttons row 1
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        depositButton = new JButton("Deposit");
        depositButton.addActionListener(e -> handleDeposit());
        panel.add(depositButton, gbc);
        
        gbc.gridx = 1;
        withdrawButton = new JButton("Withdraw");
        withdrawButton.addActionListener(e -> handleWithdraw());
        panel.add(withdrawButton, gbc);
        
        // Buttons row 2
        gbc.gridx = 0; gbc.gridy = 2;
        transferButton = new JButton("Transfer");
        transferButton.addActionListener(e -> handleTransfer());
        panel.add(transferButton, gbc);
        
        gbc.gridx = 1;
        viewStatementButton = new JButton("View Statement");
        viewStatementButton.addActionListener(e -> handleViewStatement());
        panel.add(viewStatementButton, gbc);
        
        return panel;
    }
    
    private JPanel createNotificationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Notifications"));
        
        notificationArea = new JTextArea(5, 40);
        notificationArea.setEditable(false);
        notificationArea.setBackground(new Color(240, 240, 240));
        notificationArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(notificationArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void createSampleAccount() {
        // Create sample account matching the PDF example
        currentAccount = controller.createAccount(1, "John Doe", 1000.0);
        currentAccount.verify();
        updateGUI();
        addNotification("Account created and verified successfully");
    }
    
    private void updateGUI() {
        if (currentAccount != null) {
            clientNameField.setText(currentAccount.getClientName());
            accountNumberField.setText(currentAccount.getCardNumber());
            balanceField.setText(String.format("$%.2f", currentAccount.getBalance()));
            
            AccountStatus status = currentAccount.getStatus();
            statusLabel.setText(status.toString());
            
            // Update status color based on state
            switch (status) {
                case Verified:
                    statusLabel.setForeground(new Color(0, 128, 0)); // Green
                    break;
                case Suspended:
                    statusLabel.setForeground(Color.ORANGE);
                    break;
                case Closed:
                    statusLabel.setForeground(Color.RED);
                    break;
                default:
                    statusLabel.setForeground(Color.BLUE);
            }
            
            updateButtonStates();
        }
    }
    
    private void updateButtonStates() {
        AccountStatus status = currentAccount.getStatus();
        
        // Button states based on account status (as per PDF requirements)
        depositButton.setEnabled(status != AccountStatus.Closed);
        withdrawButton.setEnabled(status == AccountStatus.Verified);
        transferButton.setEnabled(status == AccountStatus.Verified);
        viewStatementButton.setEnabled(true); // Always enabled
    }
    
    private void addNotification(String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String timestamp = sdf.format(new Date());
        notificationArea.append(timestamp + ": " + message + "\n");
        notificationArea.setCaretPosition(notificationArea.getDocument().getLength());
    }
    
    private void handleDeposit() {
        try {
            double amount = parseAmount();
            if (amount <= 0) {
                addNotification("Invalid amount entered");
                return;
            }
            
            String result = controller.processDeposit(currentAccount, amount);
            addNotification(result);
            updateGUI();
            amountField.setText("");
        } catch (NumberFormatException ex) {
            addNotification("Invalid amount entered");
        }
    }
    
    private void handleWithdraw() {
        try {
            double amount = parseAmount();
            if (amount <= 0) {
                addNotification("Invalid amount entered");
                return;
            }
            
            String result = controller.processWithdrawal(currentAccount, amount);
            addNotification(result);
            updateGUI();
            amountField.setText("");
        } catch (NumberFormatException ex) {
            addNotification("Invalid amount entered");
        }
    }
    
    private void handleTransfer() {
        try {
            double amount = parseAmount();
            if (amount <= 0) {
                addNotification("Invalid amount entered");
                return;
            }
            
            // For demo: create a target account
            String targetCard = JOptionPane.showInputDialog(this, 
                "Enter recipient card number:", 
                "Transfer", 
                JOptionPane.QUESTION_MESSAGE);
            
            if (targetCard != null && !targetCard.trim().isEmpty()) {
                String result = controller.processTransfer(currentAccount, targetCard, amount, "GUI Transfer");
                addNotification(result);
                updateGUI();
                amountField.setText("");
            }
        } catch (NumberFormatException ex) {
            addNotification("Invalid amount entered");
        }
    }
    
    private void handleViewStatement() {
        String statement = controller.getAccountStatement(currentAccount);
        JOptionPane.showMessageDialog(this, statement, "Account Statement", JOptionPane.INFORMATION_MESSAGE);
        addNotification("Statement viewed");
    }
    
    private double parseAmount() throws NumberFormatException {
        String text = amountField.getText().trim();
        if (text.isEmpty()) {
            throw new NumberFormatException("Empty input");
        }
        return Double.parseDouble(text);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new BankingGUI().setVisible(true);
        });
    }
}