package com.librarysystem.dao;

import com.librarysystem.model.Book;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Book entities.
 */
public interface BookDAO {

    List<Book> findAll() throws SQLException;

    Optional<Book> findById(int id) throws SQLException;

    List<Book> findByTitle(String title) throws SQLException;

    List<Book> findByAuthor(String author) throws SQLException;

    Optional<Book> findByISBN(String isbn) throws SQLException;

    List<Book> search(String keyword) throws SQLException;

    void save(Book book) throws SQLException;

    void update(Book book) throws SQLException;

    void delete(int id) throws SQLException;

    int countTotal() throws SQLException;

    int countAvailable() throws SQLException;

    /**
     * Decrements the available count by 1. Used when issuing a book.
     */
    void decrementAvailable(int bookId) throws SQLException;

    /**
     * Increments the available count by 1. Used when returning a book.
     */
    void incrementAvailable(int bookId) throws SQLException;
}
