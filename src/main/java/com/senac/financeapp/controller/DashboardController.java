package com.senac.financeapp.controller;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.senac.financeapp.dao.TransactionDAO;
import com.senac.financeapp.model.Transaction;
import com.senac.financeapp.util.NumberUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.PrinterJob;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    //<editor-fold desc="FXML Injections">
    @FXML
    private Label currentBalanceLabel;
    @FXML
    private ComboBox<String> transactionTypeComboBox;
    @FXML
    private TextField amountField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<String> categoryComboBox;
    @FXML
    private TextField descriptionField;
    @FXML
    private Button saveTransactionButton;
    @FXML
    private Button clearFieldsButton;
    @FXML
    private DatePicker filterStartDatePicker;
    @FXML
    private DatePicker filterEndDatePicker;
    @FXML
    private ComboBox<String> filterCategoryComboBox;
    @FXML
    private Button applyFilterButton;
    @FXML
    private Button clearFilterButton;
    @FXML
    private Button exportCsvButton;
    @FXML
    private Button exportPdfButton;
    @FXML
    private Button printButton;
    @FXML
    private TableView<Transaction> transactionsTable;
    @FXML
    private TableColumn<Transaction, Integer> colId;
    @FXML
    private TableColumn<Transaction, String> colType;
    @FXML
    private TableColumn<Transaction, BigDecimal> colAmount;
    @FXML
    private TableColumn<Transaction, LocalDate> colDate;
    @FXML
    private TableColumn<Transaction, String> colCategory;
    @FXML
    private TableColumn<Transaction, String> colDescription;
    @FXML
    private Button editTransactionButton;
    @FXML
    private Button deleteTransactionButton;
    @FXML
    private BarChart<String, Number> monthlyExpensesChart;
    @FXML
    private PieChart categoryDistributionChart;
    //</editor-fold>

    private TransactionDAO transactionDAO;
    private ObservableList<Transaction> transactionList;
    private boolean isUpdatingAmountField = false;
    private Transaction selectedTransactionForEdit;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        transactionDAO = new TransactionDAO();
        transactionList = FXCollections.observableArrayList();

        setupComboBoxes();
        setupTableColumns();
        setupEventListeners();

        updateUI(null, null, null);
    }

    private void setupComboBoxes() {
        transactionTypeComboBox.setItems(FXCollections.observableArrayList("RECEITA", "DESPESA"));
        categoryComboBox.setItems(FXCollections.observableArrayList(
                "Alimentação", "Transporte", "Moradia", "Saúde", "Educação",
                "Lazer", "Salário", "Investimento", "Outros"
        ));
        filterCategoryComboBox.setItems(FXCollections.observableArrayList(
                "Todas", "Alimentação", "Transporte", "Moradia", "Saúde", "Educação",
                "Lazer", "Salário", "Investimento", "Outros"
        ));
        filterCategoryComboBox.getSelectionModel().selectFirst();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        colAmount.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal amount, boolean empty) {
                super.updateItem(amount, empty);
                setText(empty || amount == null ? null : NumberUtil.formatCurrency(amount));
            }
        });

        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colDate.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setText(empty || date == null ? null : date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }
        });

        transactionsTable.setItems(transactionList);
    }

    private void setupEventListeners() {
        amountField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (isUpdatingAmountField || newValue == null) return;
            isUpdatingAmountField = true;
            int caretPosition = amountField.getCaretPosition();
            String formattedText;
            try {
                String digitsOnly = newValue.replaceAll("\\D", "");
                if (digitsOnly.isEmpty()) {
                    formattedText = "";
                } else {
                    BigDecimal value = new BigDecimal(digitsOnly).divide(new BigDecimal(100));
                    formattedText = NumberUtil.formatCurrency(value);
                }
            } catch (NumberFormatException e) {
                formattedText = oldValue;
            }
            amountField.setText(formattedText);
            int newCaretPosition = caretPosition + (formattedText.length() - (oldValue != null ? oldValue.length() : 0));
            if (newCaretPosition >= 0 && newCaretPosition <= formattedText.length()) {
                amountField.positionCaret(newCaretPosition);
            }
            isUpdatingAmountField = false;
        });

        transactionsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedTransactionForEdit = newSelection;
            editTransactionButton.setDisable(newSelection == null);
            deleteTransactionButton.setDisable(newSelection == null);
        });

        saveTransactionButton.setOnAction(event -> handleSaveTransaction());
        clearFieldsButton.setOnAction(event -> clearTransactionFields());
        editTransactionButton.setOnAction(event -> handleEditTransaction());
        deleteTransactionButton.setOnAction(event -> handleDeleteTransaction());
        applyFilterButton.setOnAction(event -> handleApplyFilter());
        clearFilterButton.setOnAction(event -> handleClearFilter());
        exportCsvButton.setOnAction(event -> handleExportCsv());
        exportPdfButton.setOnAction(event -> handleExportPdf());
        printButton.setOnAction(event -> handlePrint());
    }

    @FXML
    private void handleSaveTransaction() {
        String type = transactionTypeComboBox.getSelectionModel().getSelectedItem();
        String amountText = amountField.getText();
        LocalDate date = datePicker.getValue();
        String category = categoryComboBox.getSelectionModel().getSelectedItem();
        String description = descriptionField.getText();

        if (type == null || type.isEmpty() || amountText == null || amountText.isEmpty() || date == null || category == null || category.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erro de Validação", "Por favor, preencha todos os campos obrigatórios (Tipo, Valor, Data, Categoria).");
            return;
        }

        BigDecimal amount;
        try {
            amount = NumberUtil.parseCurrency(amountText);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                showAlert(Alert.AlertType.ERROR, "Erro de Validação", "O valor da transação deve ser maior que zero.");
                return;
            }
        } catch (ParseException e) {
            showAlert(Alert.AlertType.ERROR, "Erro de Validação", "Formato de valor inválido.");
            return;
        }

        final Transaction transactionToUpdate = selectedTransactionForEdit;

        Task<Void> saveTask = new Task<>() {
            @Override
            protected Void call() {
                if (transactionToUpdate == null) {
                    Transaction newTransaction = new Transaction(0, type, amount, date, category, description);
                    transactionDAO.save(newTransaction);
                } else {
                    transactionToUpdate.setType(type);
                    transactionToUpdate.setAmount(amount);
                    transactionToUpdate.setDate(date);
                    transactionToUpdate.setCategory(category);
                    transactionToUpdate.setDescription(description);
                    transactionDAO.update(transactionToUpdate);
                }
                return null;
            }
        };

        saveTask.setOnSucceeded(event -> {
            showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Transação salva com sucesso!");
            clearTransactionFields();
            updateUI(null, null, null);
        });

        saveTask.setOnFailed(event -> {
            showAlert(Alert.AlertType.ERROR, "Erro", "Ocorreu um erro ao salvar a transação.");
            saveTask.getException().printStackTrace();
        });

        new Thread(saveTask).start();
    }

    @FXML
    private void handleEditTransaction() {
        if (selectedTransactionForEdit != null) {
            transactionTypeComboBox.getSelectionModel().select(selectedTransactionForEdit.getType());
            amountField.setText(NumberUtil.formatCurrency(selectedTransactionForEdit.getAmount()));
            datePicker.setValue(selectedTransactionForEdit.getDate());
            categoryComboBox.getSelectionModel().select(selectedTransactionForEdit.getCategory());
            descriptionField.setText(selectedTransactionForEdit.getDescription());
            saveTransactionButton.setText("Atualizar Transação");
        }
    }

    @FXML
    private void handleDeleteTransaction() {
        if (selectedTransactionForEdit == null) {
            showAlert(Alert.AlertType.WARNING, "Nenhuma Seleção", "Por favor, selecione uma transação para remover.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Tem certeza que deseja remover a transação de ID " + selectedTransactionForEdit.getId() + "?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirmar Remoção");
        alert.setHeaderText("Remover Transação?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                Task<Void> deleteTask = new Task<>() {
                    @Override
                    protected Void call() {
                        transactionDAO.delete(selectedTransactionForEdit.getId());
                        return null;
                    }
                };
                deleteTask.setOnSucceeded(e -> {
                    showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Transação removida com sucesso!");
                    clearTransactionFields();
                    updateUI(null, null, null);
                });
                deleteTask.setOnFailed(e -> {
                    showAlert(Alert.AlertType.ERROR, "Erro", "Ocorreu um erro ao remover a transação.");
                    deleteTask.getException().printStackTrace();
                });
                new Thread(deleteTask).start();
            }
        });
    }

    @FXML
    private void handleApplyFilter() {
        LocalDate startDate = filterStartDatePicker.getValue();
        LocalDate endDate = filterEndDatePicker.getValue();
        String category = filterCategoryComboBox.getSelectionModel().getSelectedItem();
        if ("Todas".equals(category)) {
            category = null;
        }
        updateUI(startDate, endDate, category);
    }

    @FXML
    private void handleClearFilter() {
        filterStartDatePicker.setValue(null);
        filterEndDatePicker.setValue(null);
        filterCategoryComboBox.getSelectionModel().selectFirst();
        updateUI(null, null, null);
    }

    private void updateUI(LocalDate startDate, LocalDate endDate, String category) {
        Task<List<Transaction>> loadDataTask = new Task<>() {
            @Override
            protected List<Transaction> call() {
                return transactionDAO.findFiltered(startDate, endDate, category);
            }
        };

        loadDataTask.setOnSucceeded(event -> {
            List<Transaction> transactions = loadDataTask.getValue();
            transactionList.setAll(transactions);
            updateBalance(transactions);
            updateMonthlyExpensesChart();
            updateCategoryDistributionChart();
        });

        loadDataTask.setOnFailed(event -> {
            showAlert(Alert.AlertType.ERROR, "Erro", "Ocorreu um erro ao buscar os dados.");
            loadDataTask.getException().printStackTrace();
        });

        new Thread(loadDataTask).start();
    }

    private void updateBalance(List<Transaction> transactions) {
        BigDecimal balance = BigDecimal.ZERO;
        for (Transaction t : transactions) {
            balance = "RECEITA".equals(t.getType()) ? balance.add(t.getAmount()) : balance.subtract(t.getAmount());
        }
        currentBalanceLabel.setText(NumberUtil.formatCurrency(balance));
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            currentBalanceLabel.getStyleClass().remove("balance-positive");
            currentBalanceLabel.getStyleClass().add("balance-negative");
        } else {
            currentBalanceLabel.getStyleClass().remove("balance-negative");
            currentBalanceLabel.getStyleClass().add("balance-positive");
        }
    }

    private void updateMonthlyExpensesChart() {
        Task<List<Object[]>> task = new Task<>() {
            @Override
            protected List<Object[]> call() {
                return transactionDAO.getMonthlyExpenses();
            }
        };
        task.setOnSucceeded(e -> {
            monthlyExpensesChart.getData().clear();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Gastos Mensais");
            task.getValue().forEach(data -> series.getData().add(new XYChart.Data<>((String) data[0], (BigDecimal) data[1])));
            monthlyExpensesChart.getData().add(series);
        });
        new Thread(task).start();
    }

    private void updateCategoryDistributionChart() {
        Task<List<Object[]>> task = new Task<>() {
            @Override
            protected List<Object[]> call() {
                return transactionDAO.getCategoryDistribution();
            }
        };
        task.setOnSucceeded(e -> {
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            task.getValue().forEach(data -> {
                BigDecimal total = (BigDecimal) data[1];
                pieChartData.add(new PieChart.Data((String) data[0] + " (" + NumberUtil.formatCurrency(total) + ")", total.doubleValue()));
            });
            categoryDistributionChart.setData(pieChartData);
        });
        new Thread(task).start();
    }

    @FXML
    private void handleExportCsv() {
        Stage stage = (Stage) exportCsvButton.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar Transações como CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivos CSV", "*.csv"));
        fileChooser.setInitialFileName("transacoes.csv");
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("ID,Tipo,Valor,Data,Categoria,Descrição");
                writer.newLine();
                for (Transaction transaction : transactionsTable.getItems()) {
                    String descriptionCsv = transaction.getDescription() != null ? "\"" + transaction.getDescription().replace("\"", "\"\"") + "\"" : "";
                    writer.write(String.format("%d,%s,%s,%s,%s,%s",
                            transaction.getId(),
                            transaction.getType(),
                            transaction.getAmount().toPlainString(),
                            transaction.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                            transaction.getCategory(),
                            descriptionCsv
                    ));
                    writer.newLine();
                }
                showAlert(Alert.AlertType.INFORMATION, "Exportação Concluída", "Transações exportadas com sucesso!");
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erro de Exportação", "Ocorreu um erro ao salvar o arquivo CSV.");
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleExportPdf() {
        List<Transaction> transactions = transactionsTable.getItems();
        if (transactions.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Aviso", "Não há dados para exportar para PDF.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exportar Relatório PDF");
        fileChooser.setInitialFileName("Relatorio_Financeiro.pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Documento PDF", "*.pdf"));
        File file = fileChooser.showSaveDialog(transactionsTable.getScene().getWindow());

        if (file != null) {
            try {
                Document document = new Document(PageSize.A4);
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                Paragraph title = new Paragraph("Relatório de Transações - FinanceApp", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(20);
                document.add(title);

                PdfPTable table = new PdfPTable(6);
                table.setWidthPercentage(100);
                String[] headers = {"ID", "Tipo", "Valor", "Data", "Categoria", "Descrição"};
                for (String header : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
                    cell.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                }

                for (Transaction t : transactions) {
                    table.addCell(String.valueOf(t.getId()));
                    table.addCell(t.getType());
                    table.addCell(NumberUtil.formatCurrency(t.getAmount()));
                    table.addCell(t.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    table.addCell(t.getCategory());
                    table.addCell(t.getDescription() != null ? t.getDescription() : "");
                }
                document.add(table);

                BigDecimal total = transactions.stream()
                        .map(t -> "RECEITA".equals(t.getType()) ? t.getAmount() : t.getAmount().negate())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                Paragraph footer = new Paragraph("\nSaldo do Período: " + NumberUtil.formatCurrency(total));
                footer.setAlignment(Element.ALIGN_RIGHT);
                document.add(footer);

                document.close();
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Relatório PDF gerado com sucesso!");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erro", "Erro ao gerar PDF: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handlePrint() {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(transactionsTable.getScene().getWindow())) {
            double pageWidth = job.getJobSettings().getPageLayout().getPrintableWidth();
            double nodeWidth = transactionsTable.getBoundsInParent().getWidth();
            double scaleAmount = (pageWidth / nodeWidth) > 1 ? 1 : (pageWidth / nodeWidth);

            Scale scale = new Scale(scaleAmount, scaleAmount);
            transactionsTable.getTransforms().add(scale);

            boolean success = job.printPage(transactionsTable);

            transactionsTable.getTransforms().remove(scale);

            if (success) {
                job.endJob();
                showAlert(Alert.AlertType.INFORMATION, "Impressão", "Relatório enviado para a impressora!");
            }
        }
    }

    @FXML
    private void clearTransactionFields() {
        transactionTypeComboBox.getSelectionModel().clearSelection();
        amountField.clear();
        datePicker.setValue(null);
        categoryComboBox.getSelectionModel().clearSelection();
        descriptionField.clear();
        selectedTransactionForEdit = null;
        saveTransactionButton.setText("Salvar Transação");
        transactionsTable.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}