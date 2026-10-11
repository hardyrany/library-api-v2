# Library API

A simple Spring Boot REST API to manage a library system, developed during the **IBM Java Developer** course.

## 🎯 Purpose

Demonstrates the fundamentals of building a REST API with Spring Boot:

- Bootstrapping with `@SpringBootApplication`
- Exposing REST endpoints with `@RestController`
- Loading initial data from JSON files via `@PostConstruct`
- In-memory storage using `ArrayList`
- Borrowing and returning books with availability tracking

## 🛠️ Tech Stack

- Java 21
- Spring Boot 4.1.1
- Maven
- Spring Boot Starter Web MVC

## 📚 Domain

| Entity | Description |
|--------|-------------|
| `Book` | A book available in the library |
| `Member` | A registered library member |
| `BorrowingRecord` | A record of a book borrowed by a member |

## 🌐 Endpoints

### Books

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/v1/books` | Create a new book |
| `GET` | `/api/v1/books` | List all books |
| `GET` | `/api/v1/books/{id}` | Get a book by ID |
| `PUT` | `/api/v1/books/{id}` | Update a book |
| `DELETE` | `/api/v1/books/{id}` | Delete a book |

### Members

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/v1/members` | Create a new member |
| `GET` | `/api/v1/members` | List all members |
| `GET` | `/api/v1/members/{id}` | Get a member by ID |
| `PUT` | `/api/v1/members/{id}` | Update a member |
| `DELETE` | `/api/v1/members/{id}` | Delete a member |

### Borrowings

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/v1/borrowingrecords` | List all borrowing records |
| `GET` | `/api/v1/borrowingrecords/{id}` | Get a borrowing record by ID |
| `POST` | `/api/v1/borrow` | Borrow a book |
| `PUT` | `/api/v1/return/{id}` | Return a book |

## ▶️ How to Run

mvn spring-boot:run

The application starts an embedded Tomcat server on port 8080.

🌐 Example Requests

Borrow a book
        curl -X POST http://localhost:8080/api/v1/borrow \
        -H "Content-Type: application/json" \
        -d '{
            "book": { "id": 1 },
            "member": { "id": 1 }
        }'
Return a book
        curl -X PUT http://localhost:8080/api/v1/return/1 \
        -H "Content-Type: application/json" \
        -d '{"returnDate": "2024-01-15"}'

📦 Build

        mvn clean package

Run the generated JAR:
        java -jar target/library-0.0.1-SNAPSHOT.jar

📌 Notes
- No database is used — data is stored in memory
- Initial data is loaded from books.json, members.json and borrowings.json at startup
- Changes are reset on every application restart
- Borrowing a book decreases availableCopies; returning increases it

👤 Author
Hardy Fernandes

Developed as part of the IBM Java Developer course.
