package com.librarysystem.service;

import com.librarysystem.dao.StudentDAO;
import com.librarysystem.dao.StudentDAOImpl;
import com.librarysystem.model.Student;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Service class handling business logic for students.
 */
public class StudentService {

    private final StudentDAO studentDAO;

    public StudentService() {
        this.studentDAO = new StudentDAOImpl();
    }

    public StudentService(StudentDAO studentDAO) {
        this.studentDAO = studentDAO;
    }

    public List<Student> getAllStudents() throws SQLException {
        return studentDAO.findAll();
    }

    public Optional<Student> getStudentById(int id) throws SQLException {
        return studentDAO.findById(id);
    }

    public Optional<Student> getStudentByCode(String studentId) throws SQLException {
        return studentDAO.findByStudentId(studentId);
    }

    public List<Student> searchStudents(String keyword) throws SQLException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllStudents();
        }
        return studentDAO.search(keyword.trim());
    }

    /**
     * Registers a new student.
     * Enforces unique Student ID.
     */
    public void addStudent(Student student) throws SQLException, IllegalArgumentException {
        validateStudent(student);
        Optional<Student> existing = studentDAO.findByStudentId(student.getStudentId());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("A student with ID " + student.getStudentId() + " is already registered.");
        }
        studentDAO.save(student);
    }

    /**
     * Updates an existing student's details.
     */
    public void updateStudent(Student student) throws SQLException, IllegalArgumentException {
        validateStudent(student);
        Optional<Student> existing = studentDAO.findByStudentId(student.getStudentId());
        if (existing.isPresent() && existing.get().getId() != student.getId()) {
            throw new IllegalArgumentException("A student with ID " + student.getStudentId() + " is already registered.");
        }
        studentDAO.update(student);
    }

    /**
     * Deletes a student by database ID.
     */
    public void deleteStudent(int id) throws SQLException {
        // Enforce that we don't delete if they have pending books
        // (Note: IssuanceService can be used to check, but we can do a simple check or foreign key constraint cascade handle)
        studentDAO.delete(id);
    }

    private void validateStudent(Student student) {
        if (student.getStudentId() == null || student.getStudentId().trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID is required.");
        }
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Student Name is required.");
        }
        if (student.getSemester() < 1 || student.getSemester() > 8) {
            throw new IllegalArgumentException("Semester must be between 1 and 8.");
        }
    }
}
