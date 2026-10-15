# Library API

A simple Spring Boot REST API to manage a library system, developed during the **IBM Java Developer** course.

## 🎯 Purpose

This project demonstrates the fundamentals of building a REST API with Spring Boot:

- Bootstrapping with `@SpringBootApplication`
- Exposing REST endpoints with `@RestController`
- Loading initial data from JSON files using `@PostConstruct`
- In-memory data storage using `ArrayList`
- Managing books and library members
- Borrowing and returning books
- Tracking book availability and due dates
- Searching books by author and genre
- Filtering borrowing records by due date

## 🛠️ Tech Stack

- Java 21
- Spring Boot 4.1.1
- Maven
- Spring Web MVC
- Jackson for JSON processing

## 📚 Domain

| Entity | Description |
|---|---|
| `Book` | Represents a book available in the library |
| `Member` | Represents a registered library member |
| `BorrowingRecord` | Tracks book loans, borrowing dates, due dates, and returns |

## 🌐 API Endpoints

All endpoints use the `/api/v1` base path.

### 📖 Books

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/books` | Create a new book |
| `GET` | `/api/v1/books` | Retrieve all books |
| `GET` | `/api/v1/books/{id}` | Retrieve a book by ID |
| `PUT` | `/api/v1/books/{id}` | Update a book |
| `DELETE` | `/api/v1/books/{id}` | Delete a book |
| `GET` | `/api/v1/books/genre?genre={genre}` | Search books by genre |
| `GET` | `/api/v1/books/author/{author}` | Search books by author, optionally filtered by genre |
| `GET` | `/api/v1/books/due?date={date}` | Retrieve books due on a specific date |
| `GET` | `/api/v1/books/availability?bookId={id}` | Check book availability and expected return date |

The due-date and availability paths above should match the mappings in `LibraryController`. Use distinct paths for these two operations.

#### Search books by genre

Returns books whose genre contains the specified search term, ignoring case differences.

Example:

```http
GET /api/v1/books/genre?genre=Fiction
```

#### Search books by author

Supports partial author-name searches and ignores case differences.

Example:

```http
GET /api/v1/books/author/harper%20le
```

This can match an author stored as `Harper Lee`.

To filter by both author and genre, provide the optional `genre` query parameter:

```http
GET /api/v1/books/author/Harper%20Lee?genre=Fiction
```

#### Retrieve books due on a specific date

Returns books associated with borrowing records whose due date matches the specified date.

Example:

```http
GET /api/v1/books/due?date=2026-10-20
```

The date must use the `YYYY-MM-DD` format.

#### Check book availability

Checks whether a book has available copies. The service returns the current date if copies are available. Otherwise, it returns the earliest due date found in the book's borrowing records, or no date if none is found.

Example:

```http
GET /api/v1/books/availability?bookId=2
```

The response is a date in `YYYY-MM-DD` format when a date is available. The controller determines the HTTP status returned when the service provides no date.

### 👥 Members

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/v1/members` | Create a new member |
| `GET` | `/api/v1/members` | Retrieve all members |
| `GET` | `/api/v1/members/{id}` | Retrieve a member by ID |
| `PUT` | `/api/v1/members/{id}` | Update a member |
| `DELETE` | `/api/v1/members/{id}` | Delete a member |

### 📋 Borrowing Records

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/v1/borrowingrecords` | Retrieve all borrowing records |
| `GET` | `/api/v1/borrowingrecords/{id}` | Retrieve a borrowing record by ID |
| `POST` | `/api/v1/borrow` | Borrow a book |
| `PUT` | `/api/v1/return/{id}` | Return a borrowed book |

## 🔄 Borrowing Workflow

When a book is borrowed:

1. The service validates the book and member.
2. It checks that at least one copy is available.
3. It creates a borrowing record.
4. It sets the borrowing date to the current date.
5. It sets the due date to 14 days after borrowing.
6. It decreases the book's available copy count by one.

When a book is returned:

1. The service verifies the borrowing record.
2. It checks that the book has not already been returned.
3. It records the return date.
4. It increases the available copy count by one.

## ▶️ How to Run

Run the application using Maven:

```bash
mvn spring-boot:run
```

The application starts an embedded Tomcat server on port `8080`.

## 🌐 Example Requests

### Borrow a book

```bash
curl -X POST http://localhost:8080/api/v1/borrow \
  -H "Content-Type: application/json" \
  -d '{
    "book": { "id": 1 },
    "member": { "id": 1 }
  }'
```

### Return a book

```bash
curl -X PUT http://localhost:8080/api/v1/return/1 \
  -H "Content-Type: application/json" \
  -d '{"returnDate": "2026-10-20"}'
```

### Search by author

```bash
curl "http://localhost:8080/api/v1/books/author/harper%20le"
```

### Search by author and genre

```bash
curl "http://localhost:8080/api/v1/books/author/Harper%20Lee?genre=Fiction"
```

### Search by genre

```bash
curl "http://localhost:8080/api/v1/books/genre?genre=Fiction"
```

### Get books due on a date

```bash
curl "http://localhost:8080/api/v1/books/due?date=2026-10-20"
```

### Check book availability

```bash
curl "http://localhost:8080/api/v1/books/availability?bookId=2"
```

## 📦 Build

Build the project:

```bash
mvn clean package
```

Run the generated JAR:

```bash
java -jar target/library-0.0.1-SNAPSHOT.jar
```

## 📌 Notes

- No database is used. Data is stored in memory.
- Initial data is loaded from `books.json`, `members.json`, and `borrowings.json` in the `data` classpath directory.
- Data changes are lost when the application restarts.
- New book, member, and borrowing IDs are generated using `AtomicLong` counters initialized from the loaded data.
- Borrowing a book decreases `availableCopies`.
- Returning a book increases `availableCopies`.
- The default borrowing period is 14 days.
- Author searches support partial matching and ignore case differences.
- Genre filtering is optional when searching by author.
- Due-date searches use borrowing records and return their associated books.

## 👤 Author

**Hardy Fernandes**

Developed as part of the IBM Java Developer course.