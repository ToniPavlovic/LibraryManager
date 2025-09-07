package library;

import library.model.Book;
import library.model.User;
import library.repository.BookRepository;
import library.repository.UserRepository;
import library.service.BookService;
import library.service.UserService;

import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    private static final BookRepository bookRepo = new BookRepository();
    private static final UserRepository userRepo = new UserRepository();

    private static final BookService bookService = new BookService(bookRepo);
    private static final UserService userService = new UserService(userRepo);

    private static User loggedInUser = null;

    public static void main(String[] args) {

        while (true) {
            showMenu();
            String choice = scanner.nextLine();
            try {
                switch (choice) {
                    case "1" -> login();
                    case "2" -> logout();
                    case "3" -> listAvailableBooks();
                    case "4" -> listMyBooks();
                    case "5" -> borrowBook();
                    case "6" -> returnBook();
                    case "7" -> adminMenu();
                    case "8" -> exit();
                    default -> System.out.println("Invalid choice!");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void showMenu() {
        System.out.println("\n--- Library Menu ---");
        System.out.println("1) Login");
        System.out.println("2) Logout");
        System.out.println("3) List available Books");
        System.out.println("4) List my Books");
        System.out.println("5) Borrow Book");
        System.out.println("6) Return Book");
        System.out.println("7) Admin Menu");
        System.out.println("8) Exit");
        System.out.print("Choose: ");
    }

    private static void login() {
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        loggedInUser = userService.login(name, password);
        System.out.println("Logged in as " + loggedInUser.name);
    }

    private static void logout() {
        if (loggedInUser != null) {
            System.out.println("Logged out: " + loggedInUser.name);
            loggedInUser = null;
        } else {
            System.out.println("No user is logged in.");
        }
    }

    private static void addBook() {
        System.out.print("ID: ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Author: ");
        String author = scanner.nextLine();
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine();

        bookService.addBook(new Book(id, title, author, isbn), loggedInUser);
    }

    private static void listAvailableBooks() {
        for (Book book : bookService.getAvailableBooks(loggedInUser)) {
            System.out.println(book);
        }
    }

    public static void listMyBooks(){
        var borrowed = bookService.getBorrowedBooks(loggedInUser);

        if (borrowed.isEmpty()){
            System.out.println("You have no borrowed books!");
        } else {
            for (Book book : borrowed){
                System.out.println(book);
            }
        }
    }


    private static void borrowBook() {
        System.out.print("Enter book ID: ");
        int id = Integer.parseInt(scanner.nextLine());
        bookService.borrowBook(id, loggedInUser);
    }

    private static void returnBook() {
        System.out.print("Enter book ID: ");
        int id = Integer.parseInt(scanner.nextLine());
        bookService.returnBook(id, loggedInUser);
    }

    private static void registerUser() {
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        boolean isAdmin = false;
        if (!userService.listUsers().isEmpty()) { // only ask after first user
            System.out.print("Should this user be admin? (y/n): ");
            String choice = scanner.nextLine().trim().toLowerCase();
            isAdmin = choice.equals("y");
        }

        userService.registerUser(name, password, isAdmin, loggedInUser);
    }

    private static void listUsers() {
        for (User user : userService.listUsers()) {
            System.out.println(user);
        }
    }

    private static void removeUser() {
        System.out.print("Enter user ID: ");
        int id = Integer.parseInt(scanner.nextLine());
        userService.removeUser(id, loggedInUser);
        System.out.println("User removed.");
    }

    private static void removeBook() {
        System.out.print("Enter book ID: ");
        int id = Integer.parseInt(scanner.nextLine());
        bookService.removeBook(id, loggedInUser);
    }

    private static void exit() {
        System.out.println("Goodbye!");
        System.exit(0);
    }

    private static void adminMenu() {
        if (loggedInUser == null) throw new RuntimeException("You must be logged in!");
        if (!loggedInUser.isAdmin)
        {
            System.out.println("Access denied. Admins only!");
            return;
        }

        while (true)
        {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. Register new User");
            System.out.println("2. List Users");
            System.out.println("3. Remove User");
            System.out.println("4. Add Book");
            System.out.println("5. Remove Book");
            System.out.println("6. Back");

            System.out.print("Choose: ");
            var choice = scanner.nextLine();

            switch (choice)
            {
                case "1": registerUser(); break;
                case "2": listUsers(); break;
                case "3": removeUser(); break;
                case "4": addBook(); break;
                case "5": removeBook(); break;
                case "6": return;
                default: System.out.println("Invalid choice!"); break;
            }
        }
    }
}