package com.librarysystem.controller;

import com.librarysystem.service.DashboardService;
import com.librarysystem.util.AlertHelper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DashboardController {

    @FXML private Label lblTotalBooks;
    @FXML private Label lblTotalStudents;
    @FXML private Label lblActiveIssues;
    @FXML private Label lblOverdueBooks;
    @FXML private Label lblTotalFines;

    @FXML private BarChart<String, Number> chartIssues;
    @FXML private PieChart fxPieGenre; // Note FXML might bind to this
    @FXML private PieChart chartGenre;
    @FXML private TableView<Map.Entry<String, Integer>> tablePopularBooks;
    @FXML private TableColumn<Map.Entry<String, Integer>, String> colPopTitle;
    @FXML private TableColumn<Map.Entry<String, Integer>, Integer> colPopIssues;

    private final DashboardService dashboardService = new DashboardService();

    @FXML
    public void initialize() {
        colPopTitle.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        colPopIssues.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getValue()).asObject());

        loadDashboardData();
    }

    private void loadDashboardData() {
        Task<Void> dataTask = new Task<>() {
            private int totalBooks;
            private int totalStudents;
            private int activeIssues;
            private int overdueBooks;
            private double totalFines;
            private Map<String, Integer> monthlyStats;
            private Map<String, Integer> genreStats;
            private List<Map.Entry<String, Integer>> popularBooksList;

            @Override
            protected Void call() throws Exception {
                totalBooks = dashboardService.getTotalBooks();
                totalStudents = dashboardService.getTotalStudents();
                activeIssues = dashboardService.getActiveIssuesCount();
                overdueBooks = dashboardService.getOverdueCount();
                totalFines = dashboardService.getTotalFinesCollected();
                monthlyStats = dashboardService.getMonthlyIssueStats();
                genreStats = dashboardService.getGenreDistribution();
                popularBooksList = dashboardService.getPopularBooks(5);
                return null;
            }

            @Override
            protected void succeeded() {
                // Bind UI labels
                lblTotalBooks.setText(String.valueOf(totalBooks));
                lblTotalStudents.setText(String.valueOf(totalStudents));
                lblActiveIssues.setText(String.valueOf(activeIssues));
                lblOverdueBooks.setText(String.valueOf(overdueBooks));
                lblTotalFines.setText(String.format("Fines: ₹%.2f", totalFines));

                // Bind monthly trends chart
                chartIssues.getData().clear();
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                for (Map.Entry<String, Integer> entry : monthlyStats.entrySet()) {
                    series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                }
                chartIssues.getData().add(series);

                // Bind genre pie chart
                chartGenre.getData().clear();
                ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
                for (Map.Entry<String, Integer> entry : genreStats.entrySet()) {
                    pieData.add(new PieChart.Data(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue()));
                }
                chartGenre.setData(pieData);

                // Bind top books table
                tablePopularBooks.setItems(FXCollections.observableArrayList(popularBooksList));
            }

            @Override
            protected void failed() {
                Throwable ex = getException();
                AlertHelper.showError("Data Error", "Failed to retrieve statistics.\n" + ex.getMessage());
                ex.printStackTrace();
            }
        };

        new Thread(dataTask).start();
    }
}
