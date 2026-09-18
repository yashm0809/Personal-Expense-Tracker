package repository;

import model.Category;
import model.Transaction;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FileStorageRepository {
    private final String filePath;

    public FileStorageRepository(String filePath) {
        this.filePath = filePath;
    }

    public void save(List<Transaction> transactions) throws IOException {
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            for (Transaction t : transactions) {
                writer.write(t.toCsv());
                writer.newLine();
            }
            writer.flush();
        }
    }

    public List<Transaction> load() {
        List<Transaction> list = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return list;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNum = 0;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",", -1);
                if (parts.length >= 5) {
                    String id = parts[0].trim();
                    LocalDate date = LocalDate.parse(parts[1].trim());
                    double amount = Double.parseDouble(parts[2].trim());
                    Category category = Category.valueOf(parts[3].trim().toUpperCase());
                    String type = parts[4].trim().toUpperCase();
                    String note = (parts.length >= 6 && !parts[5].trim().isEmpty()) ? parts[5].trim() : "N/A";

                    list.add(new Transaction(id, date, amount, category, type, note));
                } else {
                    System.err.println("[Warning] Skipping malformed line " + lineNum + ": " + line);
                }
            }
        } catch (Exception e) {
            System.err.println("[Storage Error] Could not read " + filePath + ": " + e.getMessage());
        }
        return list;
    }
}