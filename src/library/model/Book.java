package library.model;

import java.time.LocalDate;

public class Book {
    public int id;
    public String title;
    public String author;
    public String isbn;
    public boolean isBorrowed = false;
    public int borrowedByUserId = -1;
    public LocalDate borrowDate;
    public LocalDate dueDate;

    public Book(int id, String title, String author, String isbn){
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
    }

    @Override
    public String toString() {
        String status = isBorrowed ? "Borrowed by User ID" + borrowedByUserId + " on " + borrowDate : "Available";
        return String.format("[%d] \"%s\" by %s (%s) - %s", id, title, author, isbn, status);
    }
}
