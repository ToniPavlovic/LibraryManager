package library.service;

import library.model.Book;
import library.model.User;
import library.repository.BookRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class BookService {
    private final BookRepository repo;

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

    public List<Book> listBooks(){
        return repo.getAll();
    }

    public void borrowBook(int bookId, User user){
        if (user == null) throw new RuntimeException("You must be logged in!");

        boolean hasOverdue = repo.getAll().stream()
                .anyMatch(b -> b.isBorrowed && b.borrowedByUserId == user.id && b.dueDate.isBefore(LocalDate.now()));

        if (hasOverdue){
            throw new RuntimeException("You have an overdue book. Return it before borrowing a new one!");
        }

        long borrowCount = repo.getAll().stream()
                .filter(b -> b.isBorrowed && b.borrowedByUserId == user.id)
                .count();
        if (borrowCount >= 1) {
            throw new RuntimeException("You can only borrow one book at the time! PLease return that book before borrowing a new one!");
        }

        Book book = repo.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found!"));
        if (book.isBorrowed) throw new RuntimeException("Book already borrowed!");

        book.isBorrowed = true;
        book.borrowedByUserId = user.id;
        book.borrowDate = LocalDate.now();
        book.dueDate = LocalDate.now().plusDays(14);
        repo.save();

        System.out.println("Book borrowed successfully! Due date: " + book.dueDate);
    }

    public void returnBook(int bookId, User user){
        Book book = repo.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found!"));
        if (!book.isBorrowed || book.borrowedByUserId != user.id){
            throw new RuntimeException("You did not borrow this book!");
        }

        boolean isOverdue = book.dueDate.isBefore(LocalDate.now());
        if (isOverdue) {
            long daysLate = ChronoUnit.DAYS.between(book.dueDate, LocalDate.now());
            System.out.println("This book is returned late by " + daysLate + " day(s)!");
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
