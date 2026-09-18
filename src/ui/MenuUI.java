package ui;

import exception.BudgetExceededException;
import exception.InvalidTransactionException;
import model.Category;
import model.Transaction;
import repository.FileStorageRepository;
import service.BudgetService;
import service.ExpenseService;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Scanner;

public class MenuUI {
    private final ExpenseService expenseService;
    private final BudgetService budgetService;
    private final FileStorageRepository repository;
    private final Scanner scanner;

    public MenuUI(ExpenseService expenseService, BudgetService budgetService, FileStorageRepository repository) {
        this.expenseService = expenseService;
        this.budgetService = budgetService;
        this.repository = repository;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            System.out.println("\n==========================================");
            System.out.println("     PERSONAL EXPENSE & BUDGET TRACKER    ");
            System.out.println("==========================================");
            System.out.println("1. Log Transaction (Income/Expense)");
            System.out.println("2. View All Transactions");
            System.out.println("3. Set / View Category Budgets");
            System.out.println("4. View Financial Analytics");
            System.out.println("5. Export Statement to TXT");
            System.out.println("6. Save & Exit");
            System.out.print("Select an option (1-6): ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": handleAddTransaction(); break;
                case "2": handleViewTransactions(); break;
                case "3": handleBudgetMenu(); break;
                case "4": handleAnalytics(); break;
                case "5": handleExportReport(); break;
                case "6":
                    saveData();
                    System.out.println("All records secured. Exiting application.");
                    return;
                default:
                    System.out.println("Invalid selection. Please choose an option from 1 to 6.");
            }
        }
    }

    private void handleAddTransaction() {
        try {
            System.out.print("Enter Type (INCOME / EXPENSE): ");
            String type = scanner.nextLine().trim().toUpperCase();
            if (!type.equals("INCOME") && !type.equals("EXPENSE")) {
                System.out.println("Invalid entry. Type must strictly be INCOME or EXPENSE.");
                return;
            }

            System.out.print("Enter Amount (₹): ");
            double amount = Double.parseDouble(scanner.nextLine().trim());

            System.out.println("Categories: FOOD, ACADEMICS, ENTERTAINMENT, COMMUTE, UTILITIES, SALARY, OTHER");
            System.out.print("Enter Category: ");
            Category cat = Category.valueOf(scanner.nextLine().trim().toUpperCase());

            System.out.print("Enter Description / Memo: ");
            String note = scanner.nextLine().trim();
            if (note.isEmpty()) {
                note = "N/A";
            }

            String txId = "TX" + (System.currentTimeMillis() % 100000);
            Transaction tx = new Transaction(txId, LocalDate.now(), amount, cat, type, note);
            expenseService.addTransaction(tx);
            
            // Auto-persist immediately so no data is lost on unexpected exit
            saveData();
            System.out.println(">> Success: Transaction logged under ID [" + txId + "]");

            if (type.equals("EXPENSE")) {
                double totalCatSpend = expenseService.getTotalSpentByCategory(cat);
                try {
                    String alert = budgetService.verifySpendingThreshold(cat, totalCatSpend);
                    if (alert != null) System.out.println(">> " + alert);
                } catch (BudgetExceededException be) {
                    System.out.println(">> " + be.getMessage());
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Input Error: Amount must be numeric.");
        } catch (IllegalArgumentException e) {
            System.out.println("Input Error: Category name is unrecognized.");
        } catch (InvalidTransactionException e) {
            System.out.println("Validation Error: " + e.getMessage());
        }
    }

    private void handleViewTransactions() {
        var list = expenseService.getAllTransactions();
        if (list.isEmpty()) {
            System.out.println("No recorded transactions present.");
            return;
        }
        System.out.println("\n----------------------- TRANSACTION LOG -----------------------");
        list.forEach(System.out::println);
    }

    private void handleBudgetMenu() {
        System.out.println("\n--- BUDGET MANAGEMENT ---");
        System.out.println("1. Configure Category Budget");
        System.out.println("2. Display All Budgets & Utilization");
        System.out.print("Select (1-2): ");
        String subChoice = scanner.nextLine().trim();

        if (subChoice.equals("1")) {
            try {
                System.out.print("Enter Category: ");
                Category cat = Category.valueOf(scanner.nextLine().trim().toUpperCase());
                System.out.print("Set Monthly Cap (₹): ");
                double limit = Double.parseDouble(scanner.nextLine().trim());
                budgetService.setBudget(cat, limit);
                System.out.println(">> Budget set: " + cat + " = ₹" + limit);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else if (subChoice.equals("2")) {
            var map = budgetService.getAllBudgets();
            if (map.isEmpty()) {
                System.out.println("No category budgets configured yet.");
            } else {
                System.out.println("\nCategory        | Budget Limit | Current Spend | Utilization");
                System.out.println("------------------------------------------------------------");
                map.forEach((cat, b) -> {
                    double spent = expenseService.getTotalSpentByCategory(cat);
                    double util = (b.getMonthlyLimit() > 0) ? (spent / b.getMonthlyLimit()) * 100 : 0;
                    System.out.printf("%-15s | ₹%-11.2f | ₹%-12.2f | %.1f%%%n",
                            cat, b.getMonthlyLimit(), spent, util);
                });
            }
        }
    }

    private void handleAnalytics() {
        System.out.println("\n================ FINANCIAL OVERVIEW ================");
        System.out.printf("Total Inflow  : ₹%.2f%n", expenseService.getTotalIncome());
        System.out.printf("Total Outflow : ₹%.2f%n", expenseService.getTotalExpense());
        System.out.printf("Net Savings   : ₹%.2f%n", expenseService.getNetSavings());
        System.out.println("====================================================");
    }

    private void handleExportReport() {
        String filename = "data/summary_report.txt";
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("================ FINANCIAL REPORT ================\n");
            writer.write("Generated on: " + LocalDate.now() + "\n\n");
            writer.write(String.format("Total Inflow  : ₹%.2f\n", expenseService.getTotalIncome()));
            writer.write(String.format("Total Outflow : ₹%.2f\n", expenseService.getTotalExpense()));
            writer.write(String.format("Net Savings   : ₹%.2f\n\n", expenseService.getNetSavings()));
            writer.write("---------------- ALL TRANSACTIONS ----------------\n");
            for (Transaction t : expenseService.getAllTransactions()) {
                writer.write(t.toString() + "\n");
            }
            System.out.println(">> Report successfully generated at: " + filename);
        } catch (IOException e) {
            System.out.println("File Export Error: " + e.getMessage());
        }
    }

    private void saveData() {
        try {
            repository.save(expenseService.getAllTransactions());
            System.out.println(">> Data successfully persisted to CSV.");
        } catch (IOException e) {
            System.out.println("Data Sync Error: " + e.getMessage());
        }
    }
}