package com.librarysystem.dao;

import com.librarysystem.model.IssuedBook;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for issued book transactions.
 */
public interface IssuedBookDAO {

    List<IssuedBook> findAll() throws SQLException;

    List<IssuedBook> findActive() throws SQLException;

    List<IssuedBook> findOverdue() throws SQLException;

    List<IssuedBook> findReturned() throws SQLException;

    List<IssuedBook> findByStudent(int studentId) throws SQLException;

    List<IssuedBook> findByBook(int bookId) throws SQLException;

    Optional<IssuedBook> findById(int id) throws SQLException;

    List<IssuedBook> search(String keyword) throws SQLException;

    void save(IssuedBook issuedBook) throws SQLException;

    void updateReturn(IssuedBook issuedBook) throws SQLException;

    int countActive() throws SQLException;

    int countOverdue() throws SQLException;

    int countActiveByStudent(int studentId) throws SQLException;

    double getTotalFinesCollected() throws SQLException;
}
