package com.senac.financeapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // A lógica de obter e fechar a conexão foi removida daqui,
        // pois agora cada operação no DAO gerencia sua própria conexão.
        // Isso simplifica a classe principal e corrige o erro de compilação.

        FXMLLoader fxmlLoader = new FXMLLoader(
                Objects.requireNonNull(getClass().getResource("/com/senac/financeapp/view/DashboardView.fxml")));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        scene.getStylesheets().add(Objects
                .requireNonNull(getClass().getResource("/com/senac/financeapp/view/style.css")).toExternalForm());

        stage.setTitle("FinanceApp - Seu Gerenciador Financeiro");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        // Fecha o pool de conexões e libera memória ao sair do app
        com.senac.financeapp.dao.DatabaseConnection.shutdown();
        System.out.println("Aplicação encerrada e conexões liberadas.");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
