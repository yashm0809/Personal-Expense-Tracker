# Problem Statement: Automated Personal Expense & Budget Tracker

## 1. Problem Statement
University students and young professionals frequently experience financial distress due to unmonitored day-to-day discretionary spending. Without structured expense logging, category-level budgeting, and active notifications before limits are breached, individuals often exhaust allowances prematurely. Existing solutions frequently demand persistent internet connectivity, cloud accounts, or complex interfaces that hinder consistent daily usage.

## 2. Scope of the Project
This project provides a standalone, terminal-executable Java application designed for offline personal accounting. It features structured transaction logging (inflows and outflows), persistent flat-file CSV storage, automated budget threshold calculations, custom checked exceptions for input and budget validation, and an analytical summary exporter. The system operates entirely from the command line without external database or GUI dependencies.

## 3. Target Users
* University and college students managing monthly pocket money or stipends.
* Hostel residents splitting recurring mess, canteen, and academic costs.
* Users seeking a secure, offline, terminal-driven financial tracking tool.

## 4. High-Level Features
* **Transaction Engine (CRUD)**: Log, display, and structure income and expense entries with category tags and timestamps.
* **Threshold-Based Budget Guard**: Set monthly spending limits per category, receiving automated 80% warnings and critical breach alerts upon exceeding 100%.
* **Local Data Persistence**: Reliable flat-file CSV engine reading and committing records dynamically.
* **Financial Analytics & Export**: Computes real-time totals for income, expenses, and net savings, with export capability to formatted summary text reports.
* **Modular Code Architecture**: Clean separation of models, services, repositories, custom exceptions, and UI controllers.