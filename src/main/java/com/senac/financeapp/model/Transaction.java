package com.senac.financeapp.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transaction {
    private int id;
    private String type; // "RECEITA" ou "DESPESA"
    private BigDecimal amount;
    private LocalDate date;
    private String category;
    private String description;

    public Transaction(int id, String type, BigDecimal amount, LocalDate date, String category, String description) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.description = description;
    }

    public Transaction(String type, BigDecimal amount, LocalDate date, String category, String description) {
        this(-1, type, amount, date, category, description); // ID será gerado pelo banco
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Transaction{" +
               "id=" + id +
               ", type='" + type + '\'' +
               ", amount=" + amount +
               ", date=" + date +
               ", category='" + category + '\'' +
               ", description='" + description + '\'' +
               '}';
    }
}

