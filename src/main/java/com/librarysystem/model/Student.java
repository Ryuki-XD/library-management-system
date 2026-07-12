package com.librarysystem.model;

import javafx.beans.property.*;

import java.time.LocalDateTime;

/**
 * Represents a registered student / library member.
 */
public class Student {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty studentId = new SimpleStringProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty phone = new SimpleStringProperty();
    private final StringProperty department = new SimpleStringProperty();
    private final IntegerProperty semester = new SimpleIntegerProperty();
    private final ObjectProperty<LocalDateTime> registeredAt = new SimpleObjectProperty<>();

    public Student() {}

    public Student(int id, String studentId, String name, String email, String phone,
                   String department, int semester, LocalDateTime registeredAt) {
        setId(id);
        setStudentId(studentId);
        setName(name);
        setEmail(email);
        setPhone(phone);
        setDepartment(department);
        setSemester(semester);
        setRegisteredAt(registeredAt);
    }

    // ─── ID ────────────────────────────────────────────────────
    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }

    // ─── Student ID ────────────────────────────────────────────
    public String getStudentId() { return studentId.get(); }
    public void setStudentId(String value) { studentId.set(value); }
    public StringProperty studentIdProperty() { return studentId; }

    // ─── Name ──────────────────────────────────────────────────
    public String getName() { return name.get(); }
    public void setName(String value) { name.set(value); }
    public StringProperty nameProperty() { return name; }

    // ─── Email ─────────────────────────────────────────────────
    public String getEmail() { return email.get(); }
    public void setEmail(String value) { email.set(value); }
    public StringProperty emailProperty() { return email; }

    // ─── Phone ─────────────────────────────────────────────────
    public String getPhone() { return phone.get(); }
    public void setPhone(String value) { phone.set(value); }
    public StringProperty phoneProperty() { return phone; }

    // ─── Department ────────────────────────────────────────────
    public String getDepartment() { return department.get(); }
    public void setDepartment(String value) { department.set(value); }
    public StringProperty departmentProperty() { return department; }

    // ─── Semester ──────────────────────────────────────────────
    public int getSemester() { return semester.get(); }
    public void setSemester(int value) { semester.set(value); }
    public IntegerProperty semesterProperty() { return semester; }

    // ─── Registered At ─────────────────────────────────────────
    public LocalDateTime getRegisteredAt() { return registeredAt.get(); }
    public void setRegisteredAt(LocalDateTime value) { registeredAt.set(value); }
    public ObjectProperty<LocalDateTime> registeredAtProperty() { return registeredAt; }

    @Override
    public String toString() {
        return studentId.get() + " — " + name.get();
    }
}
