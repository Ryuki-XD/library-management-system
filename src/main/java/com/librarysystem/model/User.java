package com.librarysystem.model;

import javafx.beans.property.*;

import java.time.LocalDateTime;

/**
 * Represents a system user (Admin or Librarian).
 */
public class User {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty username = new SimpleStringProperty();
    private final StringProperty passwordHash = new SimpleStringProperty();
    private final StringProperty fullName = new SimpleStringProperty();
    private final StringProperty role = new SimpleStringProperty();
    private final ObjectProperty<LocalDateTime> createdAt = new SimpleObjectProperty<>();

    public User() {}

    public User(int id, String username, String passwordHash, String fullName, String role, LocalDateTime createdAt) {
        setId(id);
        setUsername(username);
        setPasswordHash(passwordHash);
        setFullName(fullName);
        setRole(role);
        setCreatedAt(createdAt);
    }

    // ─── ID ────────────────────────────────────────────────────
    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }

    // ─── Username ──────────────────────────────────────────────
    public String getUsername() { return username.get(); }
    public void setUsername(String value) { username.set(value); }
    public StringProperty usernameProperty() { return username; }

    // ─── Password Hash ────────────────────────────────────────
    public String getPasswordHash() { return passwordHash.get(); }
    public void setPasswordHash(String value) { passwordHash.set(value); }
    public StringProperty passwordHashProperty() { return passwordHash; }

    // ─── Full Name ─────────────────────────────────────────────
    public String getFullName() { return fullName.get(); }
    public void setFullName(String value) { fullName.set(value); }
    public StringProperty fullNameProperty() { return fullName; }

    // ─── Role ──────────────────────────────────────────────────
    public String getRole() { return role.get(); }
    public void setRole(String value) { role.set(value); }
    public StringProperty roleProperty() { return role; }

    // ─── Created At ────────────────────────────────────────────
    public LocalDateTime getCreatedAt() { return createdAt.get(); }
    public void setCreatedAt(LocalDateTime value) { createdAt.set(value); }
    public ObjectProperty<LocalDateTime> createdAtProperty() { return createdAt; }

    @Override
    public String toString() {
        return fullName.get() + " (" + role.get() + ")";
    }
}
