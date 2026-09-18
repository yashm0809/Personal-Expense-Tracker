package ui;

import model.Category;
import model.Transaction;
import repository.FileStorageRepository;
import service.BudgetService;
import service.ExpenseService;

import java.io.File;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Ensure data folder exists
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        String dataFilePath = "data/transactions.csv";
        FileStorageRepository repository = new FileStorageRepository(dataFilePath);

        List<Transaction> storedData = repository.load();
        System.out.println("[System] Initialized. Loaded " + storedData.size() + " existing record(s) from storage.");

        ExpenseService expenseService = new ExpenseService(storedData);
        BudgetService budgetService = new BudgetService();

        // Baseline default thresholds
        budgetService.setBudget(Category.FOOD, 4000.0);
        budgetService.setBudget(Category.ACADEMICS, 1500.0);
        budgetService.setBudget(Category.ENTERTAINMENT, 1000.0);

        MenuUI ui = new MenuUI(expenseService, budgetService, repository);
        ui.start();
    }
}