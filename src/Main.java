import java.util.ArrayList;
import java.util.Scanner;


public class Main {
    static Scanner scanner = new Scanner(System.in);
    static ArrayList<Book> library = new ArrayList<>();
    public static void main(String[] args) {
        while (true){
            System.out.println("\n---Library Menu---");
            System.out.println("1) Add Book");
            System.out.println("2) List Books");
            System.out.println("3) Search Book");
            System.out.println("4) Remove Book");
            System.out.println("5) Exit");

            System.out.print("Choose: ");
            String choice = scanner.nextLine();

            switch (choice){
                case "1": addBook(); break;
                case "2": listBooks(); break;
                case "3": searchBook(); break;
                case "4": removeBook(); break;
                case "5": return;
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
    }
}