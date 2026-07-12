package com.librarysystem.controller;

import com.librarysystem.model.Book;
import com.librarysystem.model.IssuedBook;
import com.librarysystem.model.Student;
import com.librarysystem.service.BookService;
import com.librarysystem.service.IssuanceService;
import com.librarysystem.service.StudentService;
import com.librarysystem.util.AlertHelper;
import com.librarysystem.util.DateUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class IssuedBookController {

    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> comboFilter;
    @FXML private TableView<IssuedBook> tableIssues;

    @FXML private TableColumn<IssuedBook, String> colStudent;
    @FXML private TableColumn<IssuedBook, String> colBook;
    @FXML private TableColumn<IssuedBook, String> colIssueDate;
    @FXML private TableColumn<IssuedBook, String> colDueDate;
    @FXML private TableColumn<IssuedBook, String> colReturnDate;
    @FXML private TableColumn<IssuedBook, String> colFine;
    @FXML private TableColumn<IssuedBook, String> colStatus;

    @FXML private VBox panelActions;
    @FXML private Label lblPanelTitle;
    @FXML private Label lblDetailStudent;
    @FXML private Label lblDetailBook;
    @FXML private Label lblDetailIssueDate;
    @FXML private Label lblDetailDueDate;
    @FXML private Label lblDetailStatus;

    @FXML private VBox panelReturnActions;
    @FXML private Label lblCalculatedFine;

    private final IssuanceService issuanceService = new IssuanceService();
    private final StudentService studentService = new StudentService();
    private final BookService bookService = new BookService();

    private final ObservableList<IssuedBook> issueList = FXCollections.observableArrayList();
    private IssuedBook selectedIssue;

    @FXML
    public void initialize() {
        // Setup filter modes
        comboFilter.setItems(FXCollections.observableArrayList("All Records", "Active Loans", "Overdue Loans", "Returned Books"));
        comboFilter.setValue("All Records");

        // Cell bindings with formatted values
        colStudent.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getStudentName() + " (" + cellData.getValue().getStudentIdCode() + ")"));
        colBook.setCellValueFactory(cellData -> cellData.getValue().bookTitleProperty());
        colIssueDate.setCellValueFactory(cellData -> new SimpleStringProperty(DateUtils.formatShort(cellData.getValue().getIssueDate())));
        colDueDate.setCellValueFactory(cellData -> new SimpleStringProperty(DateUtils.formatShort(cellData.getValue().getDueDate())));
        colReturnDate.setCellValueFactory(cellData -> new SimpleStringProperty(DateUtils.formatShort(cellData.getValue().getReturnDate())));
        colFine.setCellValueFactory(cellData -> new SimpleStringProperty(String.format("₹%.2f", cellData.getValue().getFineAmount())));
        colStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        // Styled Status column
        colStatus.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(item);
                    badge.getStyleClass().add("badge");
                    switch (item.toUpperCase()) {
                        case "ISSUED" -> badge.getStyleClass().add("badge-issued");
                        case "RETURNED" -> badge.getStyleClass().add("badge-returned");
                        case "OVERDUE" -> badge.getStyleClass().add("badge-overdue");
                    }
                    setGraphic(badge);
                }
            }
        });

        tableIssues.setItems(issueList);

        // Selection Listener
        tableIssues.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> showIssueActionDetails(newVal));

        // Search text filter
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> performSearch(newVal));

        // ComboBox Filter selection listener
        comboFilter.valueProperty().addListener((obs, oldVal, newVal) -> loadIssues());

        loadIssues();
    }

    private void loadIssues() {
        String filter = comboFilter.getValue();
        Task<List<IssuedBook>> task = new Task<>() {
            @Override
            protected List<IssuedBook> call() throws Exception {
                // Background check overdue status sync
                issuanceService.syncOverdueStatus();
                
                return switch (filter) {
                    case "Active Loans" -> issuanceService.getActiveIssues();
                    case "Overdue Loans" -> issuanceService.getOverdueIssues();
                    case "Returned Books" -> issuanceService.getReturnedIssues();
                    default -> issuanceService.getAllIssues();
                };
            }

            @Override
            protected void succeeded() {
                issueList.setAll(getValue());
            }

            @Override
            protected void failed() {
                AlertHelper.showError("Database Error", "Failed to load transactions.\n" + getException().getMessage());
            }
        };
        new Thread(task).start();
    }

    private void performSearch(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            loadIssues();
            return;
        }
        Task<List<IssuedBook>> task = new Task<>() {
            @Override
            protected List<IssuedBook> call() throws Exception {
                return issuanceService.searchIssues(keyword);
            }

            @Override
            protected void succeeded() {
                issueList.setAll(getValue());
            }

            @Override
            protected void failed() {
                AlertHelper.showError("Search Error", getException().getMessage());
            }
        };
        new Thread(task).start();
    }

    private void showIssueActionDetails(IssuedBook issue) {
        if (issue == null) {
            clearActionPanel();
            panelActions.setDisable(true);
            return;
        }

        selectedIssue = issue;
        panelActions.setDisable(false);

        lblDetailStudent.setText(issue.getStudentName() + " (" + issue.getStudentIdCode() + ")");
        lblDetailBook.setText(issue.getBookTitle());
        lblDetailIssueDate.setText(DateUtils.formatDisplay(issue.getIssueDate()));
        lblDetailDueDate.setText(DateUtils.formatDisplay(issue.getDueDate()));
        lblDetailStatus.setText(issue.getStatus());

        if ("RETURNED".equals(issue.getStatus())) {
            panelReturnActions.setVisible(false);
            lblPanelTitle.setText("Returned Transaction");
        } else {
            panelReturnActions.setVisible(true);
            lblPanelTitle.setText("Book Return Action");
            
            // Calculate dynamic real-time fine
            double fine = DateUtils.calculateFine(issue.getDueDate(), LocalDate.now());
            lblCalculatedFine.setText(String.format("₹%.2f", fine));
            if (fine > 0) {
                lblCalculatedFine.setStyle("-fx-text-fill: -color-danger; -fx-font-weight: bold; -fx-font-size: 26px;");
            } else {
                lblCalculatedFine.setStyle("-fx-text-fill: -color-success; -fx-font-weight: bold; -fx-font-size: 26px;");
            }
        }
    }

    @FXML
    void handleNewIssue(ActionEvent event) {
        // Create custom modal dialog inputs to link issues
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Issue Book Transaction");
        dialog.setHeaderText("Please provide student and book credentials to issue a book.");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField txtDialogStudentId = new TextField();
        txtDialogStudentId.setPromptText("Enter Student ID (e.g., STU001)");
        TextField txtDialogIsbn = new TextField();
        txtDialogIsbn.setPromptText("Enter Book ISBN");
        DatePicker datePicker = new DatePicker(LocalDate.now());

        grid.add(new Label("Student ID * :"), 0, 0);
        grid.add(txtDialogStudentId, 1, 0);
        grid.add(new Label("Book ISBN * :"), 0, 1);
        grid.add(txtDialogIsbn, 1, 1);
        grid.add(new Label("Issue Date :"), 0, 2);
        grid.add(datePicker, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Style the dialog
        dialog.getDialogPane().getStylesheets().addAll(tableIssues.getScene().getStylesheets());

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String studentId = txtDialogStudentId.getText();
            String isbn = txtDialogIsbn.getText();
            LocalDate issueDate = datePicker.getValue();

            if (studentId == null || studentId.trim().isEmpty() || isbn == null || isbn.trim().isEmpty() || issueDate == null) {
                AlertHelper.showWarning("Form Validation", "Student ID and Book ISBN are mandatory.");
                return;
            }

            Task<Void> issueTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    // 1. Resolve student by code
                    Optional<Student> studentOpt = studentService.getStudentByCode(studentId.trim());
                    if (studentOpt.isEmpty()) {
                        throw new IllegalArgumentException("Student ID not found in system registers.");
                    }
                    // 2. Resolve book by ISBN
                    Optional<Book> bookOpt = bookService.getBookByIsbn(isbn.trim());
                    if (bookOpt.isEmpty()) {
                        throw new IllegalArgumentException("Book ISBN not found in systems catalog.");
                    }

                    // 3. Perform issue
                    issuanceService.issueBook(bookOpt.get().getId(), studentOpt.get().getId(), issueDate);
                    return null;
                }

                @Override
                protected void succeeded() {
                    AlertHelper.showSuccess("Success", "Book issued successfully.");
                    loadIssues();
                }

                @Override
                protected void failed() {
                    AlertHelper.showError("Issuance Failure", getException().getMessage());
                }
            };
            new Thread(issueTask).start();
        }
    }

    @FXML
    void handleReturnBook(ActionEvent event) {
        if (selectedIssue == null || selectedIssue.getId() == 0) return;

        boolean confirm = AlertHelper.showConfirmation("Return Book Confirmation",
                "Are you sure you want to mark return for: " + selectedIssue.getBookTitle() + "?");
        if (confirm) {
            Task<Double> returnTask = new Task<>() {
                @Override
                protected Double call() throws Exception {
                    return issuanceService.returnBook(selectedIssue.getId(), LocalDate.now());
                }

                @Override
                protected void succeeded() {
                    double fineCollected = getValue();
                    if (fineCollected > 0) {
                        AlertHelper.showSuccess("Book Returned",
                                String.format("Book marked as returned successfully!\nFines collected: ₹%.2f", fineCollected));
                    } else {
                        AlertHelper.showSuccess("Book Returned", "Book returned successfully with zero overdue fines.");
                    }
                    clearActionPanel();
                    panelActions.setDisable(true);
                    loadIssues();
                }

                @Override
                protected void failed() {
                    AlertHelper.showError("Transaction Error", getException().getMessage());
                }
            };
            new Thread(returnTask).start();
        }
    }

    @FXML
    void handleCancelAction(ActionEvent event) {
        clearActionPanel();
        panelActions.setDisable(true);
        tableIssues.getSelectionModel().clearSelection();
    }

    private void clearActionPanel() {
        lblDetailStudent.setText("—");
        lblDetailBook.setText("—");
        lblDetailIssueDate.setText("—");
        lblDetailDueDate.setText("—");
        lblDetailStatus.setText("—");
        lblCalculatedFine.setText("₹0.00");
        selectedIssue = null;
    }
}
