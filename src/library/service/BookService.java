package library.service;

import library.model.Book;
import library.model.User;
import library.repository.BookRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class BookService {
    private final BookRepository repo;
    private static final int MAX_BORROW = 3;
    private static final int BORROW_DAYS = 14;

    public BookService(BookRepository repo){
        this.repo = repo;
    }

    public void addBook(Book book, User user){
        if (user == null || !user.isAdmin) {
            throw new RuntimeException("Only admins can add books!");
        }
        repo.add(book);
        repo.save();

        System.out.println("Book added successfully!.");
    }

    public List<Book> getAvailableBooks(User user){
        if (user == null) throw new RuntimeException("You must be logged in!");
        return repo.getAll().stream()
                .filter(b -> !b.isBorrowed)
                .toList();
    }

    public List<Book> getBorrowedBooks(User user){
        if (user == null) throw new RuntimeException("You must be logged in!");
        return repo.getAll().stream()
                .filter(b -> b.isBorrowed && b.borrowedByUserId == user.id)
                .toList();
    }

    public void borrowBook(int bookId, User user){
        if (user == null) throw new RuntimeException("You must be logged in!");

        long borrowedCount = repo.getAll().stream()
                .filter(b -> b.isBorrowed && b.borrowedByUserId == user.id).count();

        if (borrowedCount >= MAX_BORROW){
            throw new RuntimeException("You have reached the maximum borrow limit (" + MAX_BORROW + " books).");
        }

        Book book = repo.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found!"));

        if (book.isBorrowed){
            throw new RuntimeException("Book is already borrowed!");
        }

        book.isBorrowed = true;
        book.borrowedByUserId = user.id;
        book.borrowDate = LocalDate.now();
        book.dueDate = LocalDate.now().plusDays(BORROW_DAYS);

        repo.save();
        System.out.println("Book borrowed successfully! Due date: " + book.dueDate);
    }

    public void returnBook(int bookId, User user){
        Book book = repo.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found!"));

        if (!book.isBorrowed || book.borrowedByUserId != user.id){
            throw new RuntimeException("You did not borrow this book!");
        }

        long overdueDays;
        if (book.dueDate.isBefore(LocalDate.now())){
            overdueDays = ChronoUnit.DAYS.between(book.dueDate, LocalDate.now());
            double fine = overdueDays * Book.FINE_PER_DAY;
            System.out.println("This book is overdue by: " + overdueDays + " day(s). Fine: " + fine);
        }

        book.isBorrowed = false;
        book.borrowedByUserId = -1;
        book.borrowDate = null;
        book.dueDate = null;
        repo.save();

        System.out.println("Book returned successfully!");
    }

    public void removeBook(int bookId, User user) {
        if (user == null || !user.isAdmin) {
            throw new RuntimeException("Only admins can add books!");
        }

        boolean exists = repo.findById(bookId).isPresent();
        if (!exists) throw new RuntimeException("Book not found!");

        repo.remove(bookId);
        repo.save();

        System.out.println("Book returned successfully!");
    }
}
