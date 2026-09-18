package test;

import exception.BudgetExceededException;
import exception.InvalidTransactionException;
import model.Category;
import model.Transaction;
import service.BudgetService;
import service.ExpenseService;

import java.time.LocalDate;
import java.util.ArrayList;

public class TestRunner {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("       RUNNING AUTOMATED UNIT TESTS       ");
        System.out.println("==========================================");

        testAddValidTransaction();
        testRejectNegativeAmount();
        testNetBalanceCalculation();
        testCategoryFiltering();
        testBudgetExceededTrigger();

        System.out.println("\n------------------------------------------");
        System.out.printf("Tests Run: %d | Passed: %d | Failed: %d%n", (passed + failed), passed, failed);
        System.out.println("==========================================");
    }

    private static void assertTrue(String testName, boolean condition) {
        if (condition) {
            System.out.println("[PASS] " + testName);
            passed++;
        } else {
            System.err.println("[FAIL] " + testName);
            failed++;
        }
    }

    private static void testAddValidTransaction() {
        ExpenseService service = new ExpenseService(new ArrayList<>());
        try {
            Transaction tx = new Transaction("T1", LocalDate.now(), 500.0, Category.FOOD, "EXPENSE", "Lunch");
            service.addTransaction(tx);
            assertTrue("testAddValidTransaction", service.getAllTransactions().size() == 1);
        } catch (Exception e) {
            assertTrue("testAddValidTransaction", false);
        }
    }

    private static void testRejectNegativeAmount() {
        ExpenseService service = new ExpenseService(new ArrayList<>());
        try {
            Transaction tx = new Transaction("T2", LocalDate.now(), -150.0, Category.FOOD, "EXPENSE", "Invalid");
            service.addTransaction(tx);
            assertTrue("testRejectNegativeAmount", false);
        } catch (InvalidTransactionException e) {
            assertTrue("testRejectNegativeAmount", true);
        }
    }

    private static void testNetBalanceCalculation() {
        ExpenseService service = new ExpenseService(new ArrayList<>());
        try {
            service.addTransaction(new Transaction("T1", LocalDate.now(), 5000.0, Category.SALARY, "INCOME", "Allowance"));
            service.addTransaction(new Transaction("T2", LocalDate.now(), 1200.0, Category.FOOD, "EXPENSE", "Groceries"));
            assertTrue("testNetBalanceCalculation", service.getNetSavings() == 3800.0);
        } catch (Exception e) {
            assertTrue("testNetBalanceCalculation", false);
        }
    }

    private static void testCategoryFiltering() {
        ExpenseService service = new ExpenseService(new ArrayList<>());
        try {
            service.addTransaction(new Transaction("T1", LocalDate.now(), 300.0, Category.FOOD, "EXPENSE", "Snack"));
            service.addTransaction(new Transaction("T2", LocalDate.now(), 700.0, Category.FOOD, "EXPENSE", "Dinner"));
            service.addTransaction(new Transaction("T3", LocalDate.now(), 250.0, Category.COMMUTE, "EXPENSE", "Bus"));
            assertTrue("testCategoryFiltering", service.getTotalSpentByCategory(Category.FOOD) == 1000.0);
        } catch (Exception e) {
            assertTrue("testCategoryFiltering", false);
        }
    }

    private static void testBudgetExceededTrigger() {
        BudgetService budgetService = new BudgetService();
        budgetService.setBudget(Category.FOOD, 1000.0);
        try {
            budgetService.verifySpendingThreshold(Category.FOOD, 1200.0);
            assertTrue("testBudgetExceededTrigger", false);
        } catch (BudgetExceededException e) {
            assertTrue("testBudgetExceededTrigger", true);
        }
    }
}