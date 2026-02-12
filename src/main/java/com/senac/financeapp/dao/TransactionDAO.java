package com.senac.financeapp.dao;

import com.senac.financeapp.model.Transaction;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    public void save(Transaction transaction) {
        String sql = "INSERT INTO transactions(type, amount, date, category, description) VALUES(?, ?, ?, ?, ?)";
        // O try-with-resources agora obtém uma nova conexão e a fecha ao final
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, transaction.getType());
            pstmt.setBigDecimal(2, transaction.getAmount());
            pstmt.setDate(3, java.sql.Date.valueOf(transaction.getDate()));
            pstmt.setString(4, transaction.getCategory());
            pstmt.setString(5, transaction.getDescription());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        transaction.setId(generatedKeys.getInt(1));
                        System.out.println("Transação salva com ID: " + transaction.getId());
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao salvar transação: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void update(Transaction transaction) {
        String sql = "UPDATE transactions SET type = ?, amount = ?, date = ?, category = ?, description = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, transaction.getType());
            pstmt.setBigDecimal(2, transaction.getAmount());
            pstmt.setDate(3, java.sql.Date.valueOf(transaction.getDate()));
            pstmt.setString(4, transaction.getCategory());
            pstmt.setString(5, transaction.getDescription());
            pstmt.setInt(6, transaction.getId());

            pstmt.executeUpdate();
            System.out.println("Transação atualizada com ID: " + transaction.getId());
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar transação: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM transactions WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Transação removida com ID: " + id);
        } catch (SQLException e) {
            System.err.println("Erro ao remover transação: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Transaction> findFiltered(LocalDate startDate, LocalDate endDate, String category) {
        List<Transaction> transactions = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM transactions WHERE 1=1");

        if (startDate != null) {
            sql.append(" AND date >= ?");
        }
        if (endDate != null) {
            sql.append(" AND date <= ?");
        }
        if (category != null && !category.isEmpty() && !"Todas".equals(category)) {
            sql.append(" AND category = ?");
        }
        sql.append(" ORDER BY date DESC, id DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (startDate != null) {
                pstmt.setDate(paramIndex++, java.sql.Date.valueOf(startDate));
            }
            if (endDate != null) {
                pstmt.setDate(paramIndex++, java.sql.Date.valueOf(endDate));
            }
            if (category != null && !category.isEmpty() && !"Todas".equals(category)) {
                pstmt.setString(paramIndex, category);
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                transactions.add(mapRowToTransaction(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar transações filtradas: " + e.getMessage());
            e.printStackTrace();
        }
        return transactions;
    }

    public List<Object[]> getMonthlyExpenses() {
        List<Object[]> monthlyExpenses = new ArrayList<>();
        String sql = "SELECT TO_CHAR(date, 'YYYY-MM') AS month, SUM(amount) AS total " +
                     "FROM transactions " +
                     "WHERE type = 'DESPESA' " +
                     "GROUP BY month " +
                     "ORDER BY month ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                monthlyExpenses.add(new Object[]{rs.getString("month"), rs.getBigDecimal("total")});
            }
        } catch (SQLException e) {
            System.err.println("Erro ao obter gastos mensais: " + e.getMessage());
            e.printStackTrace();
        }
        return monthlyExpenses;
    }

    public List<Object[]> getCategoryDistribution() {
        List<Object[]> categoryData = new ArrayList<>();
        String sql = "SELECT category, SUM(amount) as total " +
                     "FROM transactions " +
                     "WHERE type = 'DESPESA' " +
                     "GROUP BY category " +
                     "ORDER BY total DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                categoryData.add(new Object[]{rs.getString("category"), rs.getBigDecimal("total")});
            }
        } catch (SQLException e) {
            System.err.println("Erro ao obter distribuição por categoria: " + e.getMessage());
            e.printStackTrace();
        }
        return categoryData;
    }

    private Transaction mapRowToTransaction(ResultSet rs) throws SQLException {
        return new Transaction(
                rs.getInt("id"),
                rs.getString("type"),
                rs.getBigDecimal("amount"),
                rs.getDate("date").toLocalDate(),
                rs.getString("category"),
                rs.getString("description")
        );
    }
}
