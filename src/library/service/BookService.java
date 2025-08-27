package library.service;

import library.model.Book;
import library.model.User;
import library.repository.BookRepository;

import java.time.LocalDate;
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
    }

    public List<Book> listBooks(){
        return repo.getAll();
    }

    public void borrowBook(int bookId, User user){
        if (user == null) throw new RuntimeException("You must be logged in!");

        Book book = repo.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found!"));
        if (book.isBorrowed) throw new RuntimeException("Book already borrowed!");

        book.isBorrowed = true;
        book.borrowedByUserId = user.id;
        book.borrowDate = LocalDate.now();
        book.dueDate = LocalDate.now().plusDays(14);
        repo.save();
    }

    public void returnBook(int bookId, User user){
        Book book = repo.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found!"));
        if (!book.isBorrowed || book.borrowedByUserId != user.id){
            throw new RuntimeException("You did not borrow this book!");
        }
        book.isBorrowed = false;
        book.borrowedByUserId = -1;
        book.borrowDate = null;
        book.dueDate = null;
        repo.save();
    }

    public void removeBook(int bookId, User user) {
        if (user == null || !user.isAdmin) {
            throw new RuntimeException("Only admins can add books!");
        }

        boolean exists = repo.findById(bookId).isPresent();
        if (!exists) throw new RuntimeException("Book not found!");

        repo.remove(bookId);
    }
}
