package com.librarysystem.model;

import javafx.beans.property.*;

import java.time.LocalDate;

/**
 * Represents a book issuance / loan transaction.
 */
public class IssuedBook {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty bookId = new SimpleIntegerProperty();
    private final IntegerProperty studentId = new SimpleIntegerProperty();
    private final ObjectProperty<LocalDate> issueDate = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> dueDate = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> returnDate = new SimpleObjectProperty<>();
    private final DoubleProperty fineAmount = new SimpleDoubleProperty();
    private final StringProperty status = new SimpleStringProperty();

    // Transient display fields (joined from books/students tables)
    private final StringProperty bookTitle = new SimpleStringProperty();
    private final StringProperty studentName = new SimpleStringProperty();
    private final StringProperty studentIdCode = new SimpleStringProperty();

    public IssuedBook() {}

    public IssuedBook(int id, int bookId, int studentId, LocalDate issueDate, LocalDate dueDate,
                      LocalDate returnDate, double fineAmount, String status) {
        setId(id);
        setBookId(bookId);
        setStudentId(studentId);
        setIssueDate(issueDate);
        setDueDate(dueDate);
        setReturnDate(returnDate);
        setFineAmount(fineAmount);
        setStatus(status);
    }

    // ─── ID ────────────────────────────────────────────────────
    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }

    // ─── Book ID ───────────────────────────────────────────────
    public int getBookId() { return bookId.get(); }
    public void setBookId(int value) { bookId.set(value); }
    public IntegerProperty bookIdProperty() { return bookId; }

    // ─── Student ID ────────────────────────────────────────────
    public int getStudentId() { return studentId.get(); }
    public void setStudentId(int value) { studentId.set(value); }
    public IntegerProperty studentIdProperty() { return studentId; }

    // ─── Issue Date ────────────────────────────────────────────
    public LocalDate getIssueDate() { return issueDate.get(); }
    public void setIssueDate(LocalDate value) { issueDate.set(value); }
    public ObjectProperty<LocalDate> issueDateProperty() { return issueDate; }

    // ─── Due Date ──────────────────────────────────────────────
    public LocalDate getDueDate() { return dueDate.get(); }
    public void setDueDate(LocalDate value) { dueDate.set(value); }
    public ObjectProperty<LocalDate> dueDateProperty() { return dueDate; }

    // ─── Return Date ───────────────────────────────────────────
    public LocalDate getReturnDate() { return returnDate.get(); }
    public void setReturnDate(LocalDate value) { returnDate.set(value); }
    public ObjectProperty<LocalDate> returnDateProperty() { return returnDate; }

    // ─── Fine Amount ───────────────────────────────────────────
    public double getFineAmount() { return fineAmount.get(); }
    public void setFineAmount(double value) { fineAmount.set(value); }
    public DoubleProperty fineAmountProperty() { return fineAmount; }

    // ─── Status ────────────────────────────────────────────────
    public String getStatus() { return status.get(); }
    public void setStatus(String value) { status.set(value); }
    public StringProperty statusProperty() { return status; }

    // ─── Book Title (display) ──────────────────────────────────
    public String getBookTitle() { return bookTitle.get(); }
    public void setBookTitle(String value) { bookTitle.set(value); }
    public StringProperty bookTitleProperty() { return bookTitle; }

    // ─── Student Name (display) ────────────────────────────────
    public String getStudentName() { return studentName.get(); }
    public void setStudentName(String value) { studentName.set(value); }
    public StringProperty studentNameProperty() { return studentName; }

    // ─── Student ID Code (display) ─────────────────────────────
    public String getStudentIdCode() { return studentIdCode.get(); }
    public void setStudentIdCode(String value) { studentIdCode.set(value); }
    public StringProperty studentIdCodeProperty() { return studentIdCode; }

    @Override
    public String toString() {
        return "Issue #" + id.get() + " — " + bookTitle.get() + " → " + studentName.get();
    }
}
