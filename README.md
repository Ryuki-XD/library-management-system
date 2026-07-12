# 📚 Athena Library Management System

A production-ready, professional desktop application built using **JavaFX 21**, **MySQL**, and **Maven**. The project adheres strictly to the **MVC (Model-View-Controller)** pattern, **DAO (Data Access Object)** abstraction, and **OOP (Object-Oriented Programming)** best practices.

---

## 🌟 Key Features

- **User Login & Session management**: Hashed passwords (SHA-256) for secure administrative/librarian logins.
- **Book Inventory Management**: Cataloging tools with complete CRUD (Create, Read, Update, Delete) capability. Keeps dynamic records of total and available stock.
- **Student Enrollment directory**: Manage students registered under various departments and semesters.
- **Issue & Return books system**: Smooth borrowing workflows enforcing student loan caps (max 3 books) and tracking active issue logs.
- **Real-Time Search & Filtering**: Instant table filters by name, title, genre, ID, or ISBN parameters.
- **Dynamic Overdue Fine Calculations**: Automatic overdue flags and fine rate accrual (₹2/day default) calculated automatically upon returns.
- **Aggregate Dashboard Stats**: Clean grid stats showing totals, pie chart genre distributions, bar charts showing 6-month borrowing trends, and top-issued book grids.
- **System Reports**: Generate dynamic sheets representing issue activity, overdue checklists, returned fine archives, catalog stock audits, or student rosters. Supports print preview to standard console logs.
- **Dark Mode Switch**: Smooth UI swapping between responsive Dark and Light CSS layout themes.

---

## 🏗️ Architecture & Folder Structure

The application separates its components into clean layers, preventing tight coupling of UI controllers, logic, and database operations.

```text
d:\Projects\LIBRARY MANAGEMENT SYSTEM\
├── database/
│   └── schema.sql                # SQL database initialization script
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── module-info.java  # JPMS module configuration
│   │   │   └── com/librarysystem/
│   │   │       ├── App.java      # Application bootstrap class
│   │   │       ├── Launcher.java # Shad-JAR compatible launcher
│   │   │       │
│   │   │       ├── model/        # Plain JavaFX beans for tables
│   │   │       ├── dao/          # Database CRUD operations
│   │   │       ├── service/      # Validation & core business logic
│   │   │       ├── controller/   # View controllers
│   │   │       └── util/         # Helper functions & singleton managers
│   │   │
│   │   └── resources/
│   │       ├── db.properties     # Connection details
│   │       └── com/librarysystem/
│   │           ├── css/          # Custom Light & Dark themes
│   │           └── fxml/         # Declarative UI templates
│   │
│   └── test/                     # Test cases
│
├── pom.xml                       # Maven build descriptor
├── LICENSE                       # MIT License
└── README.md                     # Documentation
```

---

## 🛠️ Prerequisites

- **Java JDK 21** or higher.
- **Maven 3.8+** build tool.
- **MySQL Server 8.0+** running.

---

## ⚙️ Setup Instructions

### 1. Database Creation
Create a local MySQL schema and seed default records by executing the provided script:
```bash
mysql -u root -p < database/schema.sql
```
*(Alternatively, copy and run the contents of [schema.sql](file:///d:/Projects/LIBRARY%20MANAGEMENT%20SYSTEM/database/schema.sql) directly within your preferred MySQL administration IDE).*

### 2. Configure JDBC Connection details
Open [db.properties](file:///d:/Projects/LIBRARY%20MANAGEMENT%20SYSTEM/src/main/resources/db.properties) and update the configuration variables to match your local setup:
```properties
db.url=jdbc:mysql://localhost:3306/library_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.username=YOUR_MYSQL_USERNAME
db.password=YOUR_MYSQL_PASSWORD
```

---

## 🚀 Running the Application

### Running with Maven (Development)
You can compile and boot the JavaFX platform directly from your terminal:
```bash
mvn clean compile javafx:run
```

### Packaging & Running the Fat JAR (Production Build)
Build a single executable self-contained JAR file with all required dependencies:
```bash
mvn clean package
```
After the compile finishes successfully, execute the JAR using java command line:
```bash
java -jar target/library-management-system-1.0.0.jar
```

---

## 🔑 Login Credentials

The system seeds two users by default:
- **System Admin**: Username `admin` / Password `admin123`
- **Librarian**: Username `librarian` / Password `librarian`

---

## 📄 License
This application is distributed under the terms of the [MIT License](file:///d:/Projects/LIBRARY%20MANAGEMENT%20SYSTEM/LICENSE).
