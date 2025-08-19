import com.google.gson.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    static ArrayList<Book> library = new ArrayList<>();
    static ArrayList<User> users = new ArrayList<>();
    static User loggedInUser = null;
    static int nextUserId = 1;

    public static void main(String[] args) {
        loadFromFile();
        while (true){
            System.out.println("\n---Library Menu---");
            System.out.println("1) Login");
            System.out.println("2) Logout");
            System.out.println("3) Add Book");
            System.out.println("4) List Books");
            System.out.println("5) Search Book");
            System.out.println("6) Borrow Book");
            System.out.println("7) Return Book");
            System.out.println("8) Remove Book");
            System.out.println("9) Register User");
            System.out.println("10) List Users");
            System.out.println("11) Remove User");
            System.out.println("12) Exit");

            System.out.print("Choose: ");
            String choice = scanner.nextLine();

            switch (choice){
                case "1": loginUser(); break;
                case "2": logoutUser(); break;
                case "3": addBook(); break;
                case "4": listBooks(); break;
                case "5": searchBook(); break;
                case "6": borrowBook(); break;
                case "7": returnBook(); break;
                case "8": removeBook(); break;
                case "9": registerUser(); break;
                case "10": listUsers(); break;
                case "11": removeUser(); break;
                case "12": return;
                default: System.out.println("Invalid choice!");
            }
        }
    }

    static void addBook(){
        if (loggedInUser == null || !loggedInUser.isAdmin){
            System.out.println("Only admins can perform this action.");
            return;
        }

        System.out.print("ID: ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Author: ");
        String author = scanner.nextLine();
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine();

        library.add(new Book(id, title, author, isbn));
        System.out.println("Book added.");
        saveToFile();
    }

    static void listBooks(){
        if (library.isEmpty()){
            System.out.println("No books in library!");
        }

        for (Book book : library){
            System.out.println(book);

            if (book.isBorrowed && book.borrowDate != null){
                LocalDate dueDate = book.borrowDate.plusDays(14);
                if (dueDate.isBefore(LocalDate.now())){
                    System.out.println("OVERDUE! Due: " + dueDate);
                }
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
        if (loggedInUser == null){
            System.out.println("You must be logged in to be able borrow or return books.");
            return;
        }

        listBooks();
        System.out.print("Enter the ID of the book you wish to borrow: ");
        int id = Integer.parseInt(scanner.nextLine());

        for (Book book : library){
            if (book.id == id){
                if (book.isBorrowed){
                    System.out.println("Book is already borrowed");
                    return;
                }

                book.isBorrowed = true;
                book.borrowedByUserId = loggedInUser.id;
                book.borrowDate = LocalDate.now();
                book.dueDate = LocalDate.now().plusDays(14);

                System.out.println(loggedInUser.name + " has borrowed " + book.title + " . Due date: " + book.dueDate);
                saveToFile();
                return;
            }
        }

        System.out.println("Book not found.");
    }

    static void returnBook(){
        if (loggedInUser == null){
            System.out.println("You must be logged in to be able borrow or return books.");
            return;
        }

        System.out.print("Enter the ID of the book you wish to return: ");
        int id = Integer.parseInt(scanner.nextLine());

        for (Book book : library) {
            if (book.id == id) {
                if (!book.isBorrowed || book.borrowedByUserId != loggedInUser.id) {
                    System.out.println("You did not borrow this book.");
                    return;
                }

                LocalDate today = LocalDate.now();
                if (today.isAfter(book.dueDate)) {
                    long daysLate = java.time.temporal.ChronoUnit.DAYS.between(book.dueDate, today);
                    double fine = daysLate * 1.0;
                    System.out.println("Book is overdue! Days late: " + daysLate +
                            ". Fine: €" + fine);
                } else {
                    System.out.println("Book returned on time. No fines.");
                }

                book.isBorrowed = false;
                book.borrowedByUserId = -1;
                book.borrowDate = null;
                book.dueDate = null;

                System.out.println(loggedInUser.name + " has returned " + book.title);
                saveToFile();
                return;
            }
        }

        System.out.println("Book not found.");
    }

    static void removeBook(){
        if (loggedInUser == null || !loggedInUser.isAdmin){
            System.out.println("Only admins can perform this action.");
            return;
        }

        listBooks();
        System.out.print("Enter the ID of the book you wish to remove: ");
        int id = Integer.parseInt(scanner.nextLine()) ;

        boolean removed = library.removeIf(book -> book.id == id);
        if (removed){
            System.out.println("The book was successfully removed from the list.");
        } else {
            System.out.println("Invalid ID. No matches found.");
        }
        saveToFile();
    }

    static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, _, _) ->
                        LocalDate.parse(json.getAsString()))
                .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (date, _, _) ->
                        new JsonPrimitive(date.toString()))
                .setPrettyPrinting()
                .create();
    }

    static void saveToFile() {
        Gson gson = getGson();

        try (PrintWriter writer = new PrintWriter("library.json")) {
            String json = gson.toJson(library);
            writer.write(json);
        } catch (IOException e) {
            System.out.println("Error saving library: " + e.getMessage());
        }

        saveUsers(gson);
    }

    static void loadFromFile() {
        File file = new File("library.json");
        if (!file.exists()) return;

        Gson gson = getGson();

        try (Scanner fileScanner = new Scanner(file)) {
            StringBuilder json = new StringBuilder();
            while (fileScanner.hasNextLine()) {
                json.append(fileScanner.nextLine());
            }
            Book[] books = gson.fromJson(json.toString(), Book[].class);
            library.clear();
            if (books != null) {
                library.addAll(Arrays.asList(books));
            }
        } catch (IOException e) {
            System.out.println("Error loading library: " + e.getMessage());
        }

        loadUsers(gson);
    }

    static void registerUser(){
        if (loggedInUser == null || !loggedInUser.isAdmin){
            System.out.println("Only admins can perform this action.");
            return;
        }

        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        boolean isAdmin = users.isEmpty(); // only the first user is the admin
        users.add(new User(nextUserId++, name, password, isAdmin));
        System.out.println("User registered.");
        saveToFile();
    }

    static void removeUser(){
        if (loggedInUser == null || !loggedInUser.isAdmin){
            System.out.println("Only admins can perform this action.");
            return;
        }

        listUsers();
        System.out.print("Enter the ID of the user you wish to remove: ");
        int id = Integer.parseInt(scanner.nextLine()) ;

        boolean removed = users.removeIf(user -> user.id == id);
        if (removed){
            System.out.println("The user was successfully removed.");
        } else {
            System.out.println("Invalid ID. No matches found.");
        }
        saveToFile();
    }

    static void loginUser(){
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        boolean found = false;
        for (User user : users){
            if (user.name.equals(name) && user.password.equals(password)) {
                loggedInUser = user;
                System.out.println("Logged in as: " + user.name);
                found = true;
                break;
            }
        }
        if (!found) System.out.println("Invalid credentials.");
    }

    static void logoutUser(){
        if (loggedInUser != null){
            System.out.println("Logged out: " + loggedInUser.name);
            loggedInUser = null;
        } else {
            System.out.println("No user is currently logged in.");
        }
    }

    static void listUsers(){
        if (users.isEmpty()){
            System.out.println("No users registered.");
        } else {
            for (User user : users){
                System.out.println(user);
            }
        }
    }

    static void saveUsers(Gson gson){
        try (PrintWriter writer = new PrintWriter("users.json")) {
            String json = gson.toJson(users);
            writer.write(json);
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }

    static void loadUsers(Gson gson) {
        File file = new File("users.json");
        if (!file.exists()) return;

        try (Scanner fileScanner = new Scanner(file)) {
            StringBuilder json = new StringBuilder();
            while (fileScanner.hasNextLine()) {
                json.append(fileScanner.nextLine());
            }
            User[] loadedUsers = gson.fromJson(json.toString(), User[].class);
            if (loadedUsers != null) {
                users.addAll(Arrays.asList(loadedUsers));
                nextUserId = users.stream().mapToInt(u -> u.id).max().orElse(0) +1;
            }
        } catch (IOException e) {
            System.out.println("Error loading users: " + e.getMessage());
        }
    }
}