package com.librarysystem.dao;

import com.librarysystem.model.Student;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Student entities.
 */
public interface StudentDAO {

    List<Student> findAll() throws SQLException;

    Optional<Student> findById(int id) throws SQLException;

    Optional<Student> findByStudentId(String studentId) throws SQLException;

    List<Student> search(String keyword) throws SQLException;

    void save(Student student) throws SQLException;

    void update(Student student) throws SQLException;

    void delete(int id) throws SQLException;

    int countTotal() throws SQLException;
}
