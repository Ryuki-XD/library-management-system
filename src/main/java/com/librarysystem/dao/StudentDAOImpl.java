package com.librarysystem.dao;

import com.librarysystem.model.Student;
import com.librarysystem.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of {@link StudentDAO}.
 */
public class StudentDAOImpl implements StudentDAO {

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public List<Student> findAll() throws SQLException {
        String sql = "SELECT * FROM students ORDER BY name";
        List<Student> students = new ArrayList<>();

        try (PreparedStatement stmt = getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                students.add(mapRow(rs));
            }
        }
        return students;
    }

    @Override
    public Optional<Student> findById(int id) throws SQLException {
        String sql = "SELECT * FROM students WHERE id = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Student> findByStudentId(String studentId) throws SQLException {
        String sql = "SELECT * FROM students WHERE student_id = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Student> search(String keyword) throws SQLException {
        String sql = "SELECT * FROM students WHERE name LIKE ? OR student_id LIKE ? OR department LIKE ? OR email LIKE ? ORDER BY name";
        List<Student> students = new ArrayList<>();
        String pattern = "%" + keyword + "%";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);
            stmt.setString(4, pattern);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    students.add(mapRow(rs));
                }
            }
        }
        return students;
    }

    @Override
    public void save(Student student) throws SQLException {
        String sql = "INSERT INTO students (student_id, name, email, phone, department, semester) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, student.getStudentId());
            stmt.setString(2, student.getName());
            stmt.setString(3, student.getEmail());
            stmt.setString(4, student.getPhone());
            stmt.setString(5, student.getDepartment());
            stmt.setInt(6, student.getSemester());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    student.setId(keys.getInt(1));
                }
            }
        }
    }

    @Override
    public void update(Student student) throws SQLException {
        String sql = "UPDATE students SET student_id = ?, name = ?, email = ?, phone = ?, department = ?, semester = ? WHERE id = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, student.getStudentId());
            stmt.setString(2, student.getName());
            stmt.setString(3, student.getEmail());
            stmt.setString(4, student.getPhone());
            stmt.setString(5, student.getDepartment());
            stmt.setInt(6, student.getSemester());
            stmt.setInt(7, student.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM students WHERE id = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public int countTotal() throws SQLException {
        String sql = "SELECT COUNT(*) FROM students";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    // ─── Row Mapper ────────────────────────────────────────────

    private Student mapRow(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getInt("id"));
        student.setStudentId(rs.getString("student_id"));
        student.setName(rs.getString("name"));
        student.setEmail(rs.getString("email"));
        student.setPhone(rs.getString("phone"));
        student.setDepartment(rs.getString("department"));
        student.setSemester(rs.getInt("semester"));
        Timestamp ts = rs.getTimestamp("registered_at");
        if (ts != null) student.setRegisteredAt(ts.toLocalDateTime());
        return student;
    }
}
