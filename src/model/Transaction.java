package model;

import java.time.LocalDate;

public class Transaction {
    private final String id;
    private final LocalDate date;
    private final double amount;
    private final Category category;
    private final String type; // "INCOME" or "EXPENSE"
    private final String note;

    public Transaction(String id, LocalDate date, double amount, Category category, String type, String note) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.category = category;
        this.type = type.toUpperCase();
        this.note = note;
    }

    public String getId() { return id; }
    public LocalDate getDate() { return date; }
    public double getAmount() { return amount; }
    public Category getCategory() { return category; }
    public String getType() { return type; }
    public String getNote() { return note; }

    public String toCsv() {
        return String.join(",", id, date.toString(), String.valueOf(amount), category.name(), type, note);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | %-7s | %-13s | ₹%-9.2f | %s",
                id, date, type, category, amount, note);
    }
}
