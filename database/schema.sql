-- ============================================================
-- Library Management System — Database Schema
-- MySQL 8.0+
-- ============================================================

-- Create database
CREATE DATABASE IF NOT EXISTS library_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE library_db;

-- ============================================================
-- TABLE: users
-- Stores librarian and admin login credentials.
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name     VARCHAR(100) NOT NULL,
    role          ENUM('ADMIN', 'LIBRARIAN') NOT NULL DEFAULT 'LIBRARIAN',
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_users_username (username)
) ENGINE=InnoDB;

-- ============================================================
-- TABLE: books
-- Stores the book catalog.
-- ============================================================
CREATE TABLE IF NOT EXISTS books (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    isbn       VARCHAR(20)  NOT NULL UNIQUE,
    title      VARCHAR(255) NOT NULL,
    author     VARCHAR(150) NOT NULL,
    publisher  VARCHAR(150),
    year       INT,
    genre      VARCHAR(50),
    quantity   INT NOT NULL DEFAULT 1,
    available  INT NOT NULL DEFAULT 1,
    added_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_books_title  (title),
    INDEX idx_books_author (author),
    INDEX idx_books_isbn   (isbn),
    INDEX idx_books_genre  (genre)
) ENGINE=InnoDB;

-- ============================================================
-- TABLE: students
-- Stores registered student / member information.
-- ============================================================
CREATE TABLE IF NOT EXISTS students (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    student_id    VARCHAR(20)  NOT NULL UNIQUE,
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(100),
    phone         VARCHAR(20),
    department    VARCHAR(100),
    semester      INT,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_students_student_id  (student_id),
    INDEX idx_students_name        (name),
    INDEX idx_students_department   (department)
) ENGINE=InnoDB;

-- ============================================================
-- TABLE: issued_books
-- Tracks book issue and return transactions.
-- ============================================================
CREATE TABLE IF NOT EXISTS issued_books (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    book_id      INT NOT NULL,
    student_id   INT NOT NULL,
    issue_date   DATE NOT NULL,
    due_date     DATE NOT NULL,
    return_date  DATE DEFAULT NULL,
    fine_amount  DECIMAL(10, 2) DEFAULT 0.00,
    status       ENUM('ISSUED', 'RETURNED', 'OVERDUE') NOT NULL DEFAULT 'ISSUED',

    FOREIGN KEY (book_id)    REFERENCES books(id)    ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,

    INDEX idx_issued_status     (status),
    INDEX idx_issued_book_id    (book_id),
    INDEX idx_issued_student_id (student_id),
    INDEX idx_issued_due_date   (due_date)
) ENGINE=InnoDB;


-- ============================================================
-- SEED DATA
-- ============================================================

-- Default admin user (password: admin123)
-- SHA-256 hash of "admin123"
INSERT INTO users (username, password_hash, full_name, role) VALUES
    ('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'System Administrator', 'ADMIN'),
    ('librarian', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'Jane Smith', 'LIBRARIAN');

-- Sample books
INSERT INTO books (isbn, title, author, publisher, year, genre, quantity, available) VALUES
    ('978-0-06-112008-4', 'To Kill a Mockingbird',        'Harper Lee',           'J. B. Lippincott & Co.', 1960, 'Fiction',          5, 5),
    ('978-0-452-28423-4', '1984',                          'George Orwell',        'Secker & Warburg',       1949, 'Dystopian',        4, 4),
    ('978-0-7432-7356-5', 'The Great Gatsby',              'F. Scott Fitzgerald',  'Charles Scribner''s Sons', 1925, 'Classic',        3, 3),
    ('978-0-316-76948-0', 'The Catcher in the Rye',        'J.D. Salinger',        'Little, Brown',          1951, 'Fiction',          4, 4),
    ('978-0-14-028329-7', 'The Odyssey',                   'Homer',                'Penguin Classics',       -800, 'Epic Poetry',      2, 2),
    ('978-0-06-093546-7', 'To Kill a Mockingbird (50th)',  'Harper Lee',           'Harper Perennial',       2010, 'Fiction',          3, 3),
    ('978-0-553-21311-7', 'Pride and Prejudice',           'Jane Austen',          'T. Egerton',             1813, 'Romance',          5, 5),
    ('978-0-14-118776-1', 'One Hundred Years of Solitude', 'Gabriel García Márquez','Harper & Row',          1967, 'Magical Realism',  3, 3),
    ('978-0-06-112241-5', 'Brave New World',               'Aldous Huxley',        'Chatto & Windus',        1932, 'Science Fiction',  4, 4),
    ('978-0-14-044913-6', 'Crime and Punishment',          'Fyodor Dostoevsky',    'The Russian Messenger',  1866, 'Philosophical',    2, 2),
    ('978-0-7432-7357-2', 'The Da Vinci Code',             'Dan Brown',            'Doubleday',              2003, 'Thriller',         6, 6),
    ('978-0-439-02348-1', 'Harry Potter and the Deathly Hallows', 'J.K. Rowling',  'Bloomsbury',             2007, 'Fantasy',          8, 8),
    ('978-0-261-10235-4', 'The Lord of the Rings',         'J.R.R. Tolkien',       'Allen & Unwin',          1954, 'Fantasy',          4, 4),
    ('978-0-14-028334-1', 'The Art of War',                'Sun Tzu',              'Penguin Classics',       -500, 'Strategy',         3, 3),
    ('978-0-06-935464-0', 'Sapiens',                       'Yuval Noah Harari',    'Harper',                 2011, 'Non-Fiction',      5, 5);

-- Sample students
INSERT INTO students (student_id, name, email, phone, department, semester) VALUES
    ('STU001', 'Aarav Sharma',    'aarav.sharma@college.edu',    '9801234567', 'Computer Science',       4),
    ('STU002', 'Priya Patel',     'priya.patel@college.edu',     '9807654321', 'Electronics',            6),
    ('STU003', 'Rohan Singh',     'rohan.singh@college.edu',     '9812345678', 'Mechanical Engineering', 2),
    ('STU004', 'Ananya Gupta',    'ananya.gupta@college.edu',    '9823456789', 'Computer Science',       4),
    ('STU005', 'Vikram Thapa',    'vikram.thapa@college.edu',    '9834567890', 'Civil Engineering',      8),
    ('STU006', 'Sneha Joshi',     'sneha.joshi@college.edu',     '9845678901', 'Information Technology', 6),
    ('STU007', 'Arjun Rai',       'arjun.rai@college.edu',       '9856789012', 'Electronics',            2),
    ('STU008', 'Kavya Reddy',     'kavya.reddy@college.edu',     '9867890123', 'Computer Science',       8);

-- Sample issued books (some active, some returned, some overdue)
INSERT INTO issued_books (book_id, student_id, issue_date, due_date, return_date, fine_amount, status) VALUES
    (1, 1, '2026-06-01', '2026-06-15', '2026-06-14', 0.00, 'RETURNED'),
    (2, 2, '2026-06-10', '2026-06-24', '2026-06-28', 8.00, 'RETURNED'),
    (3, 3, '2026-06-20', '2026-07-04', NULL, 0.00, 'OVERDUE'),
    (4, 1, '2026-07-01', '2026-07-15', NULL, 0.00, 'ISSUED'),
    (5, 4, '2026-07-05', '2026-07-19', NULL, 0.00, 'ISSUED'),
    (12, 5, '2026-06-15', '2026-06-29', '2026-06-29', 0.00, 'RETURNED'),
    (13, 6, '2026-07-08', '2026-07-22', NULL, 0.00, 'ISSUED'),
    (9, 2, '2026-06-25', '2026-07-09', NULL, 0.00, 'OVERDUE');
