package com.librarysystem.controller;

import com.librarysystem.model.Book;
import com.librarysystem.service.BookService;
import com.librarysystem.util.AlertHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;

public class BookController {

    @FXML private TextField txtSearch;
    @FXML private TableView<Book> tableBooks;
    @FXML private TableColumn<Book, String> colIsbn;
    @FXML private TableColumn<Book, String> colTitle;
    @FXML private TableColumn<Book, String> colAuthor;
    @FXML private TableColumn<Book, String> colGenre;
    @FXML private TableColumn<Book, Integer> colQty;
    @FXML private TableColumn<Book, Integer> colAvail;

    @FXML private VBox panelEditor;
    @FXML private Label lblEditorTitle;
    @FXML private TextField txtIsbn;
    @FXML private TextField txtTitle;
    @FXML private TextField txtAuthor;
    @FXML private TextField txtPublisher;
    @FXML private TextField txtYear;
    @FXML private TextField txtGenre;
    @FXML private TextField txtQuantity;

    private final BookService bookService = new BookService();
    private final ObservableList<Book> bookList = FXCollections.observableArrayList();
    private Book selectedBook;
    private boolean isEditMode = false;

    @FXML
    public void initialize() {
        // Setup column bindings
        colIsbn.setCellValueFactory(cellData -> cellData.getValue().isbnProperty());
        colTitle.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        colAuthor.setCellValueFactory(cellData -> cellData.getValue().authorProperty());
        colGenre.setCellValueFactory(cellData -> cellData.getValue().genreProperty());
        colQty.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        colAvail.setCellValueFactory(cellData -> cellData.getValue().availableProperty().asObject());

        tableBooks.setItems(bookList);

        // Bind search filter textfield
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> performSearch(newValue));

        // Bind table row selection change
        tableBooks.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> showBookDetails(newVal));

        loadBooks();
    }

    private void loadBooks() {
        Task<List<Book>> task = new Task<>() {
            @Override
            protected List<Book> call() throws Exception {
                return bookService.getAllBooks();
            }

            @Override
            protected void succeeded() {
                bookList.setAll(getValue());
            }

            @Override
            protected void failed() {
                AlertHelper.showError("Database Error", "Failed to load books.\n" + getException().getMessage());
            }
        };
        new Thread(task).start();
    }

    private void performSearch(String keyword) {
        Task<List<Book>> task = new Task<>() {
            @Override
            protected List<Book> call() throws Exception {
                return bookService.searchBooks(keyword);
            }

            @Override
            protected void succeeded() {
                bookList.setAll(getValue());
            }

            @Override
            protected void failed() {
                AlertHelper.showError("Search Error", "An error occurred during search.\n" + getException().getMessage());
            }
        };
        new Thread(task).start();
    }

    private void showBookDetails(Book book) {
        if (book == null) {
            clearEditor();
            panelEditor.setDisable(true);
            return;
        }

        selectedBook = book;
        isEditMode = true;
        panelEditor.setDisable(false);
        lblEditorTitle.setText("Edit Book Details");

        txtIsbn.setText(book.getIsbn());
        txtTitle.setText(book.getTitle());
        txtAuthor.setText(book.getAuthor());
        txtPublisher.setText(book.getPublisher());
        txtYear.setText(book.getYear() == 0 ? "" : String.valueOf(book.getYear()));
        txtGenre.setText(book.getGenre());
        txtQuantity.setText(String.valueOf(book.getQuantity()));
    }

    @FXML
    void handleNewBook(ActionEvent event) {
        tableBooks.getSelectionModel().clearSelection();
        selectedBook = new Book();
        isEditMode = false;
        clearEditor();
        panelEditor.setDisable(false);
        lblEditorTitle.setText("Add New Book");
        txtIsbn.requestFocus();
    }

    @FXML
    void handleSaveBook(ActionEvent event) {
        // Collect form data
        String isbn = txtIsbn.getText();
        String title = txtTitle.getText();
        String author = txtAuthor.getText();
        String publisher = txtPublisher.getText();
        String genre = txtGenre.getText();
        String yearText = txtYear.getText();
        String qtyText = txtQuantity.getText();

        if (isbn == null || isbn.trim().isEmpty() || title == null || title.trim().isEmpty() || author == null || author.trim().isEmpty()) {
            AlertHelper.showWarning("Form Validation", "ISBN, Title, and Author are mandatory fields.");
            return;
        }

        int year = 0;
        if (yearText != null && !yearText.trim().isEmpty()) {
            try {
                year = Integer.parseInt(yearText.trim());
            } catch (NumberFormatException e) {
                AlertHelper.showWarning("Form Validation", "Please enter a valid numeric year.");
                txtYear.requestFocus();
                return;
            }
        }

        int quantity;
        try {
            quantity = Integer.parseInt(qtyText.trim());
            if (quantity <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            AlertHelper.showWarning("Form Validation", "Quantity must be a valid positive integer.");
            txtQuantity.requestFocus();
            return;
        }

        // Map values to selectedBook object
        selectedBook.setIsbn(isbn.trim());
        selectedBook.setTitle(title.trim());
        selectedBook.setAuthor(author.trim());
        selectedBook.setPublisher(publisher != null ? publisher.trim() : "");
        selectedBook.setYear(year);
        selectedBook.setGenre(genre != null ? genre.trim() : "");
        selectedBook.setQuantity(quantity);

        Task<Void> saveTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                if (isEditMode) {
                    bookService.updateBook(selectedBook);
                } else {
                    bookService.addBook(selectedBook);
                }
                return null;
            }

            @Override
            protected void succeeded() {
                AlertHelper.showSuccess("Success", "Book record saved successfully.");
                clearEditor();
                panelEditor.setDisable(true);
                loadBooks();
            }

            @Override
            protected void failed() {
                AlertHelper.showError("Save Error", getException().getMessage());
            }
        };

        new Thread(saveTask).start();
    }

    @FXML
    void handleDeleteBook(ActionEvent event) {
        if (selectedBook == null || selectedBook.getId() == 0) return;

        boolean confirm = AlertHelper.showConfirmation("Delete Confirmation",
                "Are you sure you want to delete book: " + selectedBook.getTitle() + "?");
        if (confirm) {
            Task<Void> deleteTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    bookService.deleteBook(selectedBook.getId());
                    return null;
                }

                @Override
                protected void succeeded() {
                    AlertHelper.showSuccess("Success", "Book deleted successfully.");
                    clearEditor();
                    panelEditor.setDisable(true);
                    loadBooks();
                }

                @Override
                protected void failed() {
                    AlertHelper.showError("Delete Error", getException().getMessage());
                }
            };
            new Thread(deleteTask).start();
        }
    }

    @FXML
    void handleCancelEdit(ActionEvent event) {
        clearEditor();
        panelEditor.setDisable(true);
        tableBooks.getSelectionModel().clearSelection();
    }

    private void clearEditor() {
        txtIsbn.clear();
        txtTitle.clear();
        txtAuthor.clear();
        txtPublisher.clear();
        txtYear.clear();
        txtGenre.clear();
        txtQuantity.clear();
        selectedBook = null;
    }
}
