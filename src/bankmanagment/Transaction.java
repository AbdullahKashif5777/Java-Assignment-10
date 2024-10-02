package bankmanagment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Transaction {
    private static List<String> transactionRecords = new ArrayList<>();

    public static boolean depositAmount(JTextField acc_no1, JTextField balance1, DefaultTableModel tableModel, DefaultTableModel tableModel2) {
        String accNoText = acc_no1.getText();
        double depositAmount;

        try {
            depositAmount = Double.parseDouble(balance1.getText());
            if (depositAmount <= 0) {
                JOptionPane.showMessageDialog(null, "Please enter a positive amount to deposit.");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid amount. Please enter a valid number.");
            return false;
        }

        Account.loadAccountsFromFile();

        boolean accountFound = false;
        for (Account account : Account.accounts) {
            if (account.accNo.equals(accNoText)) {
                account.balance += depositAmount; 
                accountFound = true;
                break;
            }
        }

        if (accountFound) {
            Account.writeAccountsToFile();
            String transactionId = generateTransactionId();
            String date = LocalDate.now().toString(); 
            String transactionRecord = String.format("%s,%s,Deposit,%.2f,%s", transactionId, accNoText, depositAmount, date);
            saveTransactionToFile(transactionRecord);

            updateTableModelWithAccounts(tableModel);
            updateTransactionTableModel(tableModel2); 

            JOptionPane.showMessageDialog(null, "Amount deposited successfully.");
            return true;
        } else {
            JOptionPane.showMessageDialog(null, "Account not found. Amount not deposited.");
            return false;
        }
    }

    public static boolean withdrawAmount(JTextField acc_no1, JTextField balance1, DefaultTableModel tableModel, DefaultTableModel tableModel2) {
        String accNoText = acc_no1.getText();
        double withdrawAmount;

        try {
            withdrawAmount = Double.parseDouble(balance1.getText());
            if (withdrawAmount <= 0) {
                JOptionPane.showMessageDialog(null, "Please enter a positive amount to withdraw.");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid amount. Please enter a valid number.");
            return false;
        }

        Account.loadAccountsFromFile();

        boolean accountFound = false;
        for (Account account : Account.accounts) {
            if (account.accNo.equals(accNoText)) {
                if (account.balance < withdrawAmount) {
                    JOptionPane.showMessageDialog(null, "Insufficient balance. Withdrawal not processed.");
                    return false;
                }
                account.balance -= withdrawAmount;
                accountFound = true;
                break;
            }
        }

        if (accountFound) {
            Account.writeAccountsToFile();
            String transactionId = generateTransactionId();
            String date = LocalDate.now().toString();

            String transactionRecord = String.format("%s,%s,Withdraw,%.2f,%s", transactionId, accNoText, withdrawAmount, date);
            saveTransactionToFile(transactionRecord); 

            updateTableModelWithAccounts(tableModel);
            updateTransactionTableModel(tableModel2); 

            JOptionPane.showMessageDialog(null, "Amount withdrawn successfully.");
            return true;
        } else {
            JOptionPane.showMessageDialog(null, "Account not found. Withdrawal not processed.");
            return false;
        }
    }

    private static String generateTransactionId() {
        return String.valueOf(System.currentTimeMillis());
    }

    private static void saveTransactionToFile(String transactionRecord) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("transactions.txt", true))) {
            writer.write(transactionRecord);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void updateTableModelWithAccounts(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        for (Account account : Account.accounts) {
            tableModel.addRow(new Object[]{
                account.name,
                account.accNo,
                account.accType,
                account.branchCode,
                account.balance
            });
        }
    }

    private static void updateTransactionTableModel(DefaultTableModel tableModel2) {
        tableModel2.setRowCount(0);
        loadTransactionsFromFile(tableModel2); 
    }

    public static void loadTransactionsFromFile(DefaultTableModel tableModel2) {
        tableModel2.setRowCount(0); 
        try (BufferedReader reader = new BufferedReader(new FileReader("transactions.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 5) { 
                    tableModel2.addRow(data);
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); 
        }
    }
}
