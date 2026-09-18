package model;

public class Budget {
    private final Category category;
    private double monthlyLimit;

    public Budget(Category category, double monthlyLimit) {
        this.category = category;
        this.monthlyLimit = monthlyLimit;
    }

    public Category getCategory() { return category; }
    public double getMonthlyLimit() { return monthlyLimit; }
    public void setMonthlyLimit(double monthlyLimit) { this.monthlyLimit = monthlyLimit; }
}
