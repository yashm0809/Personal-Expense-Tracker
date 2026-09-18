package service;

import exception.BudgetExceededException;
import model.Budget;
import model.Category;

import java.util.HashMap;
import java.util.Map;

public class BudgetService {
    private final Map<Category, Budget> budgets = new HashMap<>();

    public void setBudget(Category category, double limit) {
        budgets.put(category, new Budget(category, limit));
    }

    public Budget getBudget(Category category) {
        return budgets.get(category);
    }

    public Map<Category, Budget> getAllBudgets() {
        return budgets;
    }

    public String verifySpendingThreshold(Category cat, double currentSpend) throws BudgetExceededException {
        Budget b = budgets.get(cat);
        if (b == null) return null;

        double limit = b.getMonthlyLimit();
        if (currentSpend > limit) {
            throw new BudgetExceededException(
                String.format("ALERT: Budget breached for %s! Total Spent: ₹%.2f (Limit: ₹%.2f)", cat, currentSpend, limit)
            );
        } else if (currentSpend >= (limit * 0.80)) {
            return String.format("WARNING: Category %s is at %.1f%% of budget limit! (₹%.2f / ₹%.2f)",
                    cat, (currentSpend / limit) * 100, currentSpend, limit);
        }
        return null;
    }
}