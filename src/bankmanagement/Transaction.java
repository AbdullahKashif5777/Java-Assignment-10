package bankmanagment;

import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;

public class Transaction {
    
    // Static method to deposit an amount into an account
    public static void deposit(String accNo, double amount, DefaultTableModel tableModel) {
        // Load accounts from the file
        Account.loadAccountsFromFile();
        
        // Search for the account
        Account account = Account.searchAccount("", accNo); // Name is not needed, so we pass an empty string
        if (account != null) {
            // Update the account balance
            account.balance += amount;
            
            // Update the file with the new balance
            Account.writeAccountsToFile();
            
            // Refresh the table model
            Account.loadTableFromFile(tableModel);
            JOptionPane.showMessageDialog(null, "Deposit successful. New balance: " + account.balance);
        } else {
            JOptionPane.showMessageDialog(null, "Account number not found!");
        }
    }
}
