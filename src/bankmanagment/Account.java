package bankmanagment;

import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.util.ArrayList;

public class Account {
    private static final String FILE_NAME = "accounts.dat"; 
    static ArrayList<Account> accounts = new ArrayList<>();

    String name;
    String accNo;
    String accType;
    String branchCode;
    double balance;

    // Constructor
    public Account(String name, String accNo, String accType, String branchCode, double balance) {
        this.name = name;
        this.accNo = accNo;
        this.accType = accType;
        this.branchCode = branchCode;
        this.balance = balance;
    }

    public static void addAccount(String name, String accNo, String accType, String branchCode, double balance) {

        if (!isValidAccountNumber(accNo)) {
            System.out.println("Invalid account number. It should be 10 digits long and start with 'PK'.");
            return; 
        }

        Account newAccount = new Account(name, accNo, accType, branchCode, balance);
        accounts.add(newAccount);
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write(newAccount.toCSV());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isValidAccountNumber(String accNo) {
        return accNo != null && accNo.length() == 10 && accNo.startsWith("PK");
    }

    public static void loadAccountsFromFile() {
        accounts.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 5) {
                    String name = data[0];
                    String accNo = data[1];
                    String accType = data[2];
                    String branchCode = data[3];
                    double balance = Double.parseDouble(data[4]);
                    accounts.add(new Account(name, accNo, accType, branchCode, balance));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean updateAccount(String name, String accNo, String newAccType, String newBranchCode, double newBalance) {
        boolean updated = false;

        for (Account account : accounts) {
            if (account.name.equals(name) && account.accNo.equals(accNo)) {
                account.accType = newAccType; 
                account.branchCode = newBranchCode;
                account.balance = newBalance;
                updated = true;
                break;
            }
        }

        if (updated) {
            writeAccountsToFile();
        }

        return updated;
    }
    
    public static boolean deleteAccount(String name, String accNo) {
        boolean deleted = accounts.removeIf(account -> account.name.equalsIgnoreCase(name) && account.accNo.equals(accNo));

        if (deleted) {
            writeAccountsToFile();
        }

        return deleted; 
    }

    static void writeAccountsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Account account : accounts) {
                writer.write(account.toCSV());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String toCSV() {
        return name + "," + accNo + "," + accType + "," + branchCode + "," + balance;
    }

    public static void loadTableFromFile(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        for (Account account : accounts) {
            tableModel.addRow(new Object[]{
                account.name,
                account.accNo,
                account.accType,
                account.branchCode,
                account.balance
            });
        }
    }
    
    public static Account searchAccount(String name, String accNo) {
        for (Account account : accounts) {
            if (account.name.equalsIgnoreCase(name) && account.accNo.equals(accNo)) {
                return account;
            }
        }
        return null;
    }
    
    public static boolean doesAccountNumberExist(String accNo) {
        loadAccountsFromFile();
        for (Account account : accounts) {
            if (account.accNo.equals(accNo)) {
                return true;
            }
        }
        return false;
    }
}
