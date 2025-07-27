import java.time.LocalDate;

public class Book {
    int id;
    String title;
    String author;
    String isbn;
    boolean isBorrowed = false;
    String borrowedBy;
    LocalDate borrowDate;

    Book(int id, String title, String author, String isbn){
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
    }

    @Override
    public String toString() {
        String status = isBorrowed ? "Borrowed by " + borrowedBy + " on " + borrowDate : "Available";
        return String.format("[%d] \"%s\" by %s (%s) - %s", id, title, author, isbn, status);
    }
}
