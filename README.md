# Book Club Application

A Spring Boot web application that allows users to manage their reading lists, built with Spring Boot 3 and MySQL.

## Features

- User authentication and authorization
- Create and manage reading lists
- Add, remove, and update books in reading lists
- Order books by title, author, and publication date
- Search functionality for books and reading lists
- Role-based access control

## Technology Stack

- Java 17
- Spring Boot 3.4.3
- Spring Security 6
- Spring Data JPA
- Thymeleaf
- MySQL
- Maven

## Getting Started

### Prerequisites

- JDK 17 or later
- MySQL 8.0 or later
- Maven 3.6 or later

### Installation

1. Clone the repository
2. Configure MySQL database in `src/main/resources/application.properties`
   - Default configuration:
     ```
     spring.datasource.url=jdbc:mysql://localhost:3306/bookclub?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
     spring.datasource.username=root
     spring.datasource.password=root
     ```
3. Build the project:
   ```bash
   mvn clean install
   ```
4. Run the application:
   ```bash
   mvn spring-boot:run
   ```
5. Access the application at http://localhost:8080

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Authenticate user

### Users
- `GET /api/users` - Get all users (admin only)
- `GET /api/users/{id}` - Get a user by ID
- `PUT /api/users/{id}` - Update a user
- `DELETE /api/users/{id}` - Delete a user (admin only)

### Books
- `POST /api/books` - Create a new book (admin only)
- `GET /api/books/public` - Get all books
- `GET /api/books/{id}` - Get a book by ID
- `PUT /api/books/{id}` - Update a book (admin only)
- `DELETE /api/books/{id}` - Delete a book (admin only)
- `GET /api/books/public/search` - Search books by title or author
- `GET /api/books/public/by-title` - Find books by title
- `GET /api/books/public/by-author` - Find books by author
- `GET /api/books/public/by-date-range` - Find books by publication date range

### Reading Lists
- `POST /api/readinglists` - Create a new reading list
- `GET /api/readinglists` - Get reading lists for the authenticated user
- `GET /api/readinglists/{id}` - Get a reading list by ID
- `PUT /api/readinglists/{id}` - Update a reading list
- `DELETE /api/readinglists/{id}` - Delete a reading list
- `POST /api/readinglists/{readingListId}/books/{bookId}` - Add a book to a reading list
- `DELETE /api/readinglists/{readingListId}/books/{bookId}` - Remove a book from a reading list

## Security

The application uses Spring Security for authentication and authorization. There are two roles:
- `ROLE_USER` - Regular users who can manage their own reading lists
- `ROLE_ADMIN` - Administrators who can manage all users, books, and reading lists

By default, new users are assigned the `ROLE_USER` role.

## Architecture

The application follows a layered architecture:
- Controller layer - Handles HTTP requests and responses
- Service layer - Implements business logic
- Repository layer - Provides data access
- Entity layer - Represents database tables

## Database Schema

The application uses the following database tables:
- `users` - Stores user information
- `user_roles` - Stores user roles
- `books` - Stores book information
- `reading_lists` - Stores reading list information
- `reading_list_books` - Many-to-many relationship between reading lists and books

## Future Enhancements

- Email verification for new users
- Password reset functionality
- Book ratings and reviews
- Reading progress tracking
- Book recommendations based on reading history
- Integration with external book APIs 