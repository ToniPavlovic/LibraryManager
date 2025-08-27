package library.repository;

import library.model.Book;
import library.util.JsonUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class BookRepository {
    private static final String FILE_NAME = "library.json";
    private List<Book> books = new ArrayList<>();

    public List<Book> getAll() {
        return books;
    }

    public void add(Book book){
        books.add(book);
        save();
    }

    public void remove(int bookId){
        books.removeIf(b -> b.id == bookId);
        save();
    }

    public Optional<Book> findById(int bookId){
        return books.stream().filter(b -> b.id == bookId).findFirst();
    }

    public void save(){
        JsonUtil.saveToFile(FILE_NAME, books);
    }

    public void load(){
        File file = new File(FILE_NAME);
        if (file.exists()){
            Book[] loaded = JsonUtil.loadFromFile(FILE_NAME, Book[].class);
            if (loaded != null){
                books = new ArrayList<>(Arrays.asList(loaded));
            }
        }
    }
}
