package com.librarysystem.controller;

import com.librarysystem.dao.BookDAOImpl;
import com.librarysystem.dao.IssuedBookDAOImpl;
import com.librarysystem.dao.StudentDAOImpl;
import com.librarysystem.model.Book;
import com.librarysystem.model.IssuedBook;
import com.librarysystem.model.Student;
import com.librarysystem.util.AlertHelper;
import com.librarysystem.util.DateUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReportController {

    @FXML private ComboBox<String> comboReportType;
    @FXML private Label lblReportTitle;
    @FXML private Label lblReportTime;
    @FXML private Label lblReportCount;
    @FXML private Label lblReportFineSum;
    @FXML private HBox boxTotalFine;
    @FXML private TableView<Object[]> tableReportData;

    private final IssuedBookDAOImpl issuedBookDAO = new IssuedBookDAOImpl();
    private final BookDAOImpl bookDAO = new BookDAOImpl();
    private final StudentDAOImpl studentDAO = new StudentDAOImpl();

    private String currentReport = "";

    @FXML
    public void initialize() {
        comboReportType.setItems(FXCollections.observableArrayList(
                "Active Book Issue Log",
                "Overdue Pending Returns List",
                "Fines & Returned History",
                "Books Catalog Availability",
                "Students Directory Summary"
        ));
        comboReportType.setValue("Active Book Issue Log");
        lblReportTime.setText("Generated: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        
        handleGenerate(null);
    }

    @FXML
    void handleGenerate(ActionEvent event) {
        String type = comboReportType.getValue();
        currentReport = type;
        lblReportTitle.setText(type);
        boxTotalFine.setVisible(false);

        // Generate dynamic table column maps
        tableReportData.getColumns().clear();

        if ("Active Book Issue Log".equals(type)) {
            setupIssueColumns();
            loadActiveIssueReport();
        } else if ("Overdue Pending Returns List".equals(type)) {
            setupIssueColumns();
            loadOverdueReport();
        } else if ("Fines & Returned History".equals(type)) {
            setupIssueColumns();
            boxTotalFine.setVisible(true);
            loadReturnedReport();
        } else if ("Books Catalog Availability".equals(type)) {
            setupBookColumns();
            loadBookReport();
        } else if ("Students Directory Summary".equals(type)) {
            setupStudentColumns();
            loadStudentReport();
        }
    }

    @FXML
    void handleExportConsole(ActionEvent event) {
        if (tableReportData.getItems().isEmpty()) {
            AlertHelper.showWarning("Print Action", "There is no report data generated to print.");
            return;
        }

        // Print preview to console logs
        System.out.println("\n=============================================================");
        System.out.println("LIBRARY SYSTEM PRINT REPORT: " + currentReport.toUpperCase());
        System.out.println("GENERATED TIME : " + LocalDateTimeDisplay());
        System.out.println("TOTAL COUNT    : " + lblReportCount.getText());
        if (boxTotalFine.isVisible()) {
            System.out.println("TOTAL FINES    : " + lblReportFineSum.getText());
        }
        System.out.println("=============================================================");

        // Print header columns
        StringBuilder header = new StringBuilder();
        for (TableColumn<Object[], ?> col : tableReportData.getColumns()) {
            header.append(col.getText()).append("\t| ");
        }
        System.out.println(header);
        System.out.println("-------------------------------------------------------------");

        // Print rows
        for (Object[] row : tableReportData.getItems()) {
            StringBuilder line = new StringBuilder();
            for (Object cell : row) {
                line.append(cell != null ? cell.toString() : "—").append("\t| ");
            }
            System.out.println(line);
        }
        System.out.println("=============================================================\n");

        AlertHelper.showSuccess("Export Succeeded", "Report structure printed to standard console logs successfully.");
    }

    // ─── Setup Columns Helpers ─────────────────────────────────

    private void setupIssueColumns() {
        TableColumn<Object[], String> col1 = new TableColumn<>("Transaction ID");
        TableColumn<Object[], String> col2 = new TableColumn<>("Student");
        TableColumn<Object[], String> col3 = new TableColumn<>("Book Issued");
        TableColumn<Object[], String> col4 = new TableColumn<>("Issue Date");
        TableColumn<Object[], String> col5 = new TableColumn<>("Due Date");
        TableColumn<Object[], String> col6 = new TableColumn<>("Returned Date");
        TableColumn<Object[], String> col7 = new TableColumn<>("Fines");

        col1.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue()[0].toString()));
        col2.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue()[1].toString()));
        col3.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue()[2].toString()));
        col4.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue()[3].toString()));
        col5.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue()[4].toString()));
        col6.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue()[5].toString()));
        col7.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue()[6].toString()));

        tableReportData.getColumns().addAll(col1, col2, col3, col4, col5, col6, col7);
    }

    private void setupBookColumns() {
        TableColumn<Object[], String> col1 = new TableColumn<>("ISBN");
        TableColumn<Object[], String> col2 = new TableColumn<>("Title");
        TableColumn<Object[], String> col3 = new TableColumn<>("Author");
        TableColumn<Object[], String> col4 = new TableColumn<>("Genre");
        TableColumn<Object[], String> col5 = new TableColumn<>("Total Qty");
        TableColumn<Object[], String> col6 = new TableColumn<>("Available");

        col1.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue()[0].toString()));
        col2.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue()[1].toString()));
        col3.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue()[2].toString()));
        col4.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue()[3].toString()));
        col5.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue()[4].toString()));
        col6.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue()[5].toString()));

        tableReportData.getColumns().addAll(col1, col2, col3, col4, col5, col6);
    }

    private void setupStudentColumns() {
        TableColumn<Object[], String> col1 = new TableColumn<>("Student ID");
        TableColumn<Object[], String> col2 = new TableColumn<>("Name");
        TableColumn<Object[], String> col3 = new TableColumn<>("Email");
        TableColumn<Object[], String> col4 = new TableColumn<>("Phone");
        TableColumn<Object[], String> col5 = new TableColumn<>("Dept");
        TableColumn<Object[], String> col6 = new TableColumn<>("Semester");

        col1.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue()[0].toString()));
        col2.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue()[1].toString()));
        col3.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue()[2].toString()));
        col4.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue()[3].toString()));
        col5.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue()[4].toString()));
        col6.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue()[5].toString()));

        tableReportData.getColumns().addAll(col1, col2, col3, col4, col5, col6);
    }

    // ─── Loading Report Data Helpers ───────────────────────────

    private void loadActiveIssueReport() {
        Task<List<IssuedBook>> task = new Task<>() {
            @Override protected List<IssuedBook> call() throws Exception {
                return issuedBookDAO.findActive();
            }
            @Override protected void succeeded() {
                populateIssueGrid(getValue());
            }
        };
        new Thread(task).start();
    }

    private void loadOverdueReport() {
        Task<List<IssuedBook>> task = new Task<>() {
            @Override protected List<IssuedBook> call() throws Exception {
                return issuedBookDAO.findOverdue();
            }
            @Override protected void succeeded() {
                populateIssueGrid(getValue());
            }
        };
        new Thread(task).start();
    }

    private void loadReturnedReport() {
        Task<List<IssuedBook>> task = new Task<>() {
            @Override protected List<IssuedBook> call() throws Exception {
                return issuedBookDAO.findReturned();
            }
            @Override protected void succeeded() {
                populateIssueGrid(getValue());
                double sum = getValue().stream().mapToDouble(IssuedBook::getFineAmount).sum();
                lblReportFineSum.setText(String.format("₹%.2f", sum));
            }
        };
        new Thread(task).start();
    }

    private void loadBookReport() {
        Task<List<Book>> task = new Task<>() {
            @Override protected List<Book> call() throws Exception {
                return bookDAO.findAll();
            }
            @Override protected void succeeded() {
                List<Object[]> rows = new ArrayList<>();
                for (Book b : getValue()) {
                    rows.add(new Object[]{
                            b.getIsbn(),
                            b.getTitle(),
                            b.getAuthor(),
                            b.getGenre(),
                            b.getQuantity(),
                            b.getAvailable()
                    });
                }
                tableReportData.setItems(FXCollections.observableArrayList(rows));
                lblReportCount.setText(String.valueOf(rows.size()));
            }
        };
        new Thread(task).start();
    }

    private void loadStudentReport() {
        Task<List<Student>> task = new Task<>() {
            @Override protected List<Student> call() throws Exception {
                return studentDAO.findAll();
            }
            @Override protected void succeeded() {
                List<Object[]> rows = new ArrayList<>();
                for (Student s : getValue()) {
                    rows.add(new Object[]{
                            s.getStudentId(),
                            s.getName(),
                            s.getEmail(),
                            s.getPhone(),
                            s.getDepartment(),
                            s.getSemester()
                    });
                }
                tableReportData.setItems(FXCollections.observableArrayList(rows));
                lblReportCount.setText(String.valueOf(rows.size()));
            }
        };
        new Thread(task).start();
    }

    private void populateIssueGrid(List<IssuedBook> list) {
        List<Object[]> rows = new ArrayList<>();
        for (IssuedBook ib : list) {
            rows.add(new Object[]{
                    "TXN-" + ib.getId(),
                    ib.getStudentName() + " (" + ib.getStudentIdCode() + ")",
                    ib.getBookTitle(),
                    DateUtils.formatShort(ib.getIssueDate()),
                    DateUtils.formatShort(ib.getDueDate()),
                    DateUtils.formatShort(ib.getReturnDate()),
                    String.format("₹%.2f", ib.getFineAmount())
            });
        }
        tableReportData.setItems(FXCollections.observableArrayList(rows));
        lblReportCount.setText(String.valueOf(rows.size()));
    }

    private String LocalDateTimeDisplay() {
        return java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }
}
