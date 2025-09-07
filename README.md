# 📚 Library Manager

Java application for managing books and users in a library.
Admins can add, remove, and list books and users, while all users can borrow, return, and search for books.

## Features
- __Books (admin only):__ Add, list, and remove books
- __Users (admin only):__ Register, remove, and list users
- __Borrowing & Returning:__ Borrow and return books (all users)
- __Search:__ Search books by title or author (all users)
- __Authentication:__ Login and logout
- __Data Persistence:__ All data is stored in MySQL

## Structure
- __Models:__ `Book`, `User`
- __Repository:__ Handles MySQL database operations for books and users 
- __Service:__ `BookService` and `UserService` manage operations and enforce admin permissions
- __Utils:__ `DBConnection` handles database connections
