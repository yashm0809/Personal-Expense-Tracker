package service;

import exception.InvalidTransactionException;
import model.Category;
import model.Transaction;

import java.util.*;

public class ExpenseService {
    private final List<Transaction> transactions;

    public ExpenseService(List<Transaction> initialData) {
        this.transactions = new ArrayList<>(initialData);
    }

    public void addTransaction(Transaction tx) throws InvalidTransactionException {
        if (tx.getAmount() <= 0) {
            throw new InvalidTransactionException("Transaction amount must be strictly greater than 0.");
        }
        transactions.add(tx);
    }

    public List<Transaction> getAllTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    public double getTotalExpense() {
        return transactions.stream()
                .filter(t -> t.getType().equalsIgnoreCase("EXPENSE"))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getTotalIncome() {
        return transactions.stream()
                .filter(t -> t.getType().equalsIgnoreCase("INCOME"))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getNetSavings() {
        return getTotalIncome() - getTotalExpense();
    }

    public double getTotalSpentByCategory(Category cat) {
        return transactions.stream()
                .filter(t -> t.getType().equalsIgnoreCase("EXPENSE") && t.getCategory() == cat)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }
}