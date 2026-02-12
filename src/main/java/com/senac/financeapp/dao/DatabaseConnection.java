package com.senac.financeapp.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String DB_HOST = "aws-1-sa-east-1.pooler.supabase.com";
    private static final String DB_PORT = "6543";
    private static final String DB_NAME = "postgres";
    private static final String DB_USER = "postgres.qlbajcoxjhcacvtjsliq";
    private static final String DB_PASSWORD = "@66100Sapato#";

    private static final String DB_URL = String.format("jdbc:postgresql://%s:%s/%s?sslmode=require", DB_HOST, DB_PORT, DB_NAME);

    private static HikariDataSource dataSource;

    static {
        try {
            // ESSENCIAL para o .exe: Garante que o driver seja carregado na memória
            Class.forName("org.postgresql.Driver");
            
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(DB_URL);
            config.setUsername(DB_USER);
            config.setPassword(DB_PASSWORD);
            
            // Configurações de Escala e Estabilidade
            config.setMaximumPoolSize(10); // Máximo de conexões simultâneas
            config.setMinimumIdle(2);      // Mantém conexões prontas para uso rápido
            config.setConnectionTimeout(10000); // 10 segundos de limite antes de dar erro (timeout)
            config.setIdleTimeout(600000);    // 10 minutos para fechar conexões inativas
            
            dataSource = new HikariDataSource(config);
            System.out.println("Pool de conexões Hikari (Supabase) iniciado com sucesso.");
            
        } catch (ClassNotFoundException e) {
            System.err.println("Erro: Driver PostgreSQL não encontrado! Verifique o POM.xml.");
        } catch (Exception e) {
            System.err.println("Erro na inicialização do Pool do Supabase: " + e.getMessage());
        }
    }

    /**
     * Retorna uma conexão do Pool. Muito mais rápido que DriverManager.
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("Fonte de dados não inicializada corretamente.");
        }
        return dataSource.getConnection();
    }
    
    // Método para fechar o pool quando o app fechar (opcional)
    public static void shutdown() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}