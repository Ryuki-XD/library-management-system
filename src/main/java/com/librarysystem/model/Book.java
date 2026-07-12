package com.librarysystem.model;

import javafx.beans.property.*;

import java.time.LocalDateTime;

/**
 * Represents a book in the library catalog.
 */
public class Book {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty isbn = new SimpleStringProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty author = new SimpleStringProperty();
    private final StringProperty publisher = new SimpleStringProperty();
    private final IntegerProperty year = new SimpleIntegerProperty();
    private final StringProperty genre = new SimpleStringProperty();
    private final IntegerProperty quantity = new SimpleIntegerProperty();
    private final IntegerProperty available = new SimpleIntegerProperty();
    private final ObjectProperty<LocalDateTime> addedAt = new SimpleObjectProperty<>();

    public Book() {}

    public Book(int id, String isbn, String title, String author, String publisher,
                int year, String genre, int quantity, int available, LocalDateTime addedAt) {
        setId(id);
        setIsbn(isbn);
        setTitle(title);
        setAuthor(author);
        setPublisher(publisher);
        setYear(year);
        setGenre(genre);
        setQuantity(quantity);
        setAvailable(available);
        setAddedAt(addedAt);
    }

    // ─── ID ────────────────────────────────────────────────────
    public int getId() { return id.get(); }
    public void setId(int value) { id.set(value); }
    public IntegerProperty idProperty() { return id; }

    // ─── ISBN ──────────────────────────────────────────────────
    public String getIsbn() { return isbn.get(); }
    public void setIsbn(String value) { isbn.set(value); }
    public StringProperty isbnProperty() { return isbn; }

    // ─── Title ─────────────────────────────────────────────────
    public String getTitle() { return title.get(); }
    public void setTitle(String value) { title.set(value); }
    public StringProperty titleProperty() { return title; }

    // ─── Author ────────────────────────────────────────────────
    public String getAuthor() { return author.get(); }
    public void setAuthor(String value) { author.set(value); }
    public StringProperty authorProperty() { return author; }

    // ─── Publisher ─────────────────────────────────────────────
    public String getPublisher() { return publisher.get(); }
    public void setPublisher(String value) { publisher.set(value); }
    public StringProperty publisherProperty() { return publisher; }

    // ─── Year ──────────────────────────────────────────────────
    public int getYear() { return year.get(); }
    public void setYear(int value) { year.set(value); }
    public IntegerProperty yearProperty() { return year; }

    // ─── Genre ─────────────────────────────────────────────────
    public String getGenre() { return genre.get(); }
    public void setGenre(String value) { genre.set(value); }
    public StringProperty genreProperty() { return genre; }

    // ─── Quantity ──────────────────────────────────────────────
    public int getQuantity() { return quantity.get(); }
    public void setQuantity(int value) { quantity.set(value); }
    public IntegerProperty quantityProperty() { return quantity; }

    // ─── Available ─────────────────────────────────────────────
    public int getAvailable() { return available.get(); }
    public void setAvailable(int value) { available.set(value); }
    public IntegerProperty availableProperty() { return available; }

    // ─── Added At ──────────────────────────────────────────────
    public LocalDateTime getAddedAt() { return addedAt.get(); }
    public void setAddedAt(LocalDateTime value) { addedAt.set(value); }
    public ObjectProperty<LocalDateTime> addedAtProperty() { return addedAt; }

    @Override
    public String toString() {
        return title.get() + " by " + author.get();
    }
}
