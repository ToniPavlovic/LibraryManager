import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    static ArrayList<Book> library = new ArrayList<>();

    public static void main(String[] args) {
        loadFromFIle();
        while (true){
            System.out.println("\n---Library Menu---");
            System.out.println("1) Add Book");
            System.out.println("2) List Books");
            System.out.println("3) Search Book");
            System.out.println("4) Borrow Book");
            System.out.println("5) Remove Book");
            System.out.println("6) Exit");

            System.out.print("Choose: ");
            String choice = scanner.nextLine();

            switch (choice){
                case "1": addBook(); break;
                case "2": listBooks(); break;
                case "3": searchBook(); break;
                case "4": borrowBook(); break;
                case "5": removeBook(); break;
                case "6": return;
                default: System.out.println("Invalid choice!");
            }
        }
    }

    static void addBook(){
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Author: ");
        String author = scanner.nextLine();
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine();

        library.add(new Book(title, author, isbn));
        System.out.println("Book added.");
        saveToFile();
    }

    static void listBooks(){
        if (library.isEmpty()){
            System.out.println("No books found!");
        } else {
            for (int i = 0 ; i < library.size() ; i++){
                System.out.println((i+1) + ". " + library.get(i));
            }
        }
    }

    static void searchBook(){
        System.out.print("Search by title or by author: ");
        String query = scanner.nextLine();
        boolean found = false;

        for (Book book : library)
            if (book.title.toLowerCase().contains(query) || book.author.toLowerCase().contains(query)) {
                System.out.println(book);
                found = true;
            }
        if (!found) System.out.println("No books with such title or author were found.");
    }

    static void borrowBook(){
        listBooks();
        System.out.print("Enter the number of the book you wish to borrow or return: ");
        int index = Integer.parseInt(scanner.nextLine()) -1;

        if (index >= 0 && index < library.size()){
            Book book = library.get(index);
            book.isBorrowed = !book.isBorrowed;
            System.out.println("Book status updated: " + (book.isBorrowed ? "Borrowed" : "Returned"));
        } else {
            System.out.println("Invalid index. No matches found.");
        }
        saveToFile();
    }

    static void removeBook(){
        listBooks();
        System.out.print("Enter the number of the book you wish to remove: ");
        int index = Integer.parseInt(scanner.nextLine()) -1;

        if (index >= 0 && index < library.size()){
            library.remove(index);
            System.out.println("The book was successfully removed from the list.");
        } else {
            System.out.println("Invalid index. No matches found.");
        }
        saveToFile();
    }

    static void saveToFile(){
        try {
            Gson gson = new Gson();
            String json = gson.toJson(library);
            Files.write(Paths.get("library.json"), json.getBytes());
            System.out.println("Library saved.");
        } catch (IOException e){
            System.out.println("Error saving library: " + e.getMessage());
        }
    }

    static void loadFromFIle(){
        try {
            Path path = Paths.get("library.json");
            if (!Files.exists(path)) return;

            String json = Files.readString(path);
            Gson gson = new Gson();
            library = gson.fromJson(json, new TypeToken<ArrayList<Book>>(){}.getType());

            if (library == null) library = new ArrayList<>();
        } catch (IOException e) {
            System.out.println("Error loading library: " + e.getMessage());
        }
    }
}