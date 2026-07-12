package com.librarysystem.controller;

import com.librarysystem.model.Student;
import com.librarysystem.service.StudentService;
import com.librarysystem.util.AlertHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;

public class StudentController {

    @FXML private TextField txtSearch;
    @FXML private TableView<Student> tableStudents;
    @FXML private TableColumn<Student, String> colStudentId;
    @FXML private TableColumn<Student, String> colName;
    @FXML private TableColumn<Student, String> colEmail;
    @FXML private TableColumn<Student, String> colPhone;
    @FXML private TableColumn<Student, String> colDepartment;
    @FXML private TableColumn<Student, Integer> colSemester;

    @FXML private VBox panelEditor;
    @FXML private Label lblEditorTitle;
    @FXML private TextField txtStudentId;
    @FXML private TextField txtName;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPhone;
    @FXML private TextField txtDepartment;
    @FXML private TextField txtSemester;

    private final StudentService studentService = new StudentService();
    private final ObservableList<Student> studentList = FXCollections.observableArrayList();
    private Student selectedStudent;
    private boolean isEditMode = false;

    @FXML
    public void initialize() {
        // Setup column bindings
        colStudentId.setCellValueFactory(cellData -> cellData.getValue().studentIdProperty());
        colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colEmail.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        colPhone.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        colDepartment.setCellValueFactory(cellData -> cellData.getValue().departmentProperty());
        colSemester.setCellValueFactory(cellData -> cellData.getValue().semesterProperty().asObject());

        tableStudents.setItems(studentList);

        // Bind search filter textfield
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> performSearch(newValue));

        // Bind table row selection change
        tableStudents.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> showStudentDetails(newVal));

        loadStudents();
    }

    private void loadStudents() {
        Task<List<Student>> task = new Task<>() {
            @Override
            protected List<Student> call() throws Exception {
                return studentService.getAllStudents();
            }

            @Override
            protected void succeeded() {
                studentList.setAll(getValue());
            }

            @Override
            protected void failed() {
                AlertHelper.showError("Database Error", "Failed to load students.\n" + getException().getMessage());
            }
        };
        new Thread(task).start();
    }

    private void performSearch(String keyword) {
        Task<List<Student>> task = new Task<>() {
            @Override
            protected List<Student> call() throws Exception {
                return studentService.searchStudents(keyword);
            }

            @Override
            protected void succeeded() {
                studentList.setAll(getValue());
            }

            @Override
            protected void failed() {
                AlertHelper.showError("Search Error", "An error occurred during search.\n" + getException().getMessage());
            }
        };
        new Thread(task).start();
    }

    private void showStudentDetails(Student student) {
        if (student == null) {
            clearEditor();
            panelEditor.setDisable(true);
            return;
        }

        selectedStudent = student;
        isEditMode = true;
        panelEditor.setDisable(false);
        lblEditorTitle.setText("Edit Student Details");

        txtStudentId.setText(student.getStudentId());
        txtName.setText(student.getName());
        txtEmail.setText(student.getEmail());
        txtPhone.setText(student.getPhone());
        txtDepartment.setText(student.getDepartment());
        txtSemester.setText(String.valueOf(student.getSemester()));
    }

    @FXML
    void handleNewStudent(ActionEvent event) {
        tableStudents.getSelectionModel().clearSelection();
        selectedStudent = new Student();
        isEditMode = false;
        clearEditor();
        panelEditor.setDisable(false);
        lblEditorTitle.setText("Register New Student");
        txtStudentId.requestFocus();
    }

    @FXML
    void handleSaveStudent(ActionEvent event) {
        // Collect form data
        String studentId = txtStudentId.getText();
        String name = txtName.getText();
        String email = txtEmail.getText();
        String phone = txtPhone.getText();
        String department = txtDepartment.getText();
        String semesterText = txtSemester.getText();

        if (studentId == null || studentId.trim().isEmpty() || name == null || name.trim().isEmpty()) {
            AlertHelper.showWarning("Form Validation", "Student ID and Full Name are mandatory fields.");
            return;
        }

        int semester;
        try {
            semester = Integer.parseInt(semesterText.trim());
            if (semester < 1 || semester > 8) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            AlertHelper.showWarning("Form Validation", "Semester must be a valid integer between 1 and 8.");
            txtSemester.requestFocus();
            return;
        }

        // Map values
        selectedStudent.setStudentId(studentId.trim());
        selectedStudent.setName(name.trim());
        selectedStudent.setEmail(email != null ? email.trim() : "");
        selectedStudent.setPhone(phone != null ? phone.trim() : "");
        selectedStudent.setDepartment(department != null ? department.trim() : "");
        selectedStudent.setSemester(semester);

        Task<Void> saveTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                if (isEditMode) {
                    studentService.updateStudent(selectedStudent);
                } else {
                    studentService.addStudent(selectedStudent);
                }
                return null;
            }

            @Override
            protected void succeeded() {
                AlertHelper.showSuccess("Success", "Student records updated successfully.");
                clearEditor();
                panelEditor.setDisable(true);
                loadStudents();
            }

            @Override
            protected void failed() {
                AlertHelper.showError("Save Error", getException().getMessage());
            }
        };

        new Thread(saveTask).start();
    }

    @FXML
    void handleDeleteStudent(ActionEvent event) {
        if (selectedStudent == null || selectedStudent.getId() == 0) return;

        boolean confirm = AlertHelper.showConfirmation("Delete Confirmation",
                "Are you sure you want to unregister: " + selectedStudent.getName() + "?");
        if (confirm) {
            Task<Void> deleteTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    studentService.deleteStudent(selectedStudent.getId());
                    return null;
                }

                @Override
                protected void succeeded() {
                    AlertHelper.showSuccess("Success", "Student unregistered successfully.");
                    clearEditor();
                    panelEditor.setDisable(true);
                    loadStudents();
                }

                @Override
                protected void failed() {
                    AlertHelper.showError("Delete Error", "Cannot delete student. They might have currently issued books.");
                }
            };
            new Thread(deleteTask).start();
        }
    }

    @FXML
    void handleCancelEdit(ActionEvent event) {
        clearEditor();
        panelEditor.setDisable(true);
        tableStudents.getSelectionModel().clearSelection();
    }

    private void clearEditor() {
        txtStudentId.clear();
        txtName.clear();
        txtEmail.clear();
        txtPhone.clear();
        txtDepartment.clear();
        txtSemester.clear();
        selectedStudent = null;
    }
}
