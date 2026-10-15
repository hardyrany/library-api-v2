package com.app.library.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.app.library.model.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.app.library.model.Book;
import com.app.library.model.BorrowingRecord;
import com.app.library.service.LibraryService;

@RestController
@RequestMapping("/api/v1")
public class LibraryController {

    // Create a logger instance
    private static final Logger logger = LoggerFactory.getLogger(LibraryController.class);

    private final LibraryService libraryService;

    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @GetMapping("/library")
    public String libraryEndpoint() {
        return "<h1>Library</h1>" + "\n<p>Welcome to Library System</p>";
    }

    // ==================== Book Endpoints ====================

    // Add a new book
    @PostMapping("/books")
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        libraryService.addBook(book);
        logger.info("The book was added");
        return new ResponseEntity<>(book, HttpStatus.CREATED);
    }

    // Get all books
    @GetMapping("/books")
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = libraryService.getAllBooks();
        logger.info("The list of books returned " + books);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    // Get a book by ID
    @GetMapping("/books/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Optional<Book> book = libraryService.getBookById(id);
        logger.info("The book returned " + book);
        return book.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Get a book by genre
    @GetMapping("/books/genre")
    public ResponseEntity<List<Book>> getBooksByGenre(@RequestParam String genre) {
        List<Book> books = libraryService.getBooksByGenre(genre);
        logger.info("The book returned by genre " + books);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    // Get books by author and optional genre
    @GetMapping("/books/author/{author}")
    public ResponseEntity<List<Book>> getBooksByAuthorAndGenre(@PathVariable String author,
            @RequestParam(required = false) String genre) {
        List<Book> books = libraryService.getBooksByAuthorAndGenre(author, genre);
        logger.info("Books retrieved for author {} and genre {}",
                author, genre);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    // Update a book
    @PutMapping("/books/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book updateBook) {

        if (libraryService.getBookById(id).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        updateBook.setId(id);

        Optional<Book> updatedBook = libraryService.updateBook(id, updateBook);
        logger.info("The book has been updated " + updatedBook.get());
        return new ResponseEntity<>(updatedBook.get(), HttpStatus.OK);
    }

    // Delete a book
    @DeleteMapping("/books/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        if (libraryService.getBookById(id).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        libraryService.deleteBook(id);
        logger.info("The book has been deleted: " + id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // ==================== Member Endpoints ====================

    // Add a new member
    @PostMapping("/members")
    public ResponseEntity<Member> addMember(@RequestBody Member member) {
        libraryService.addMember(member);
        logger.info("The member was added");
        return new ResponseEntity<>(member, HttpStatus.CREATED);
    }

    // Get all members
    @GetMapping("/members")
    public ResponseEntity<List<Member>> getAllMembers() {
        List<Member> members = libraryService.getAllMembers();
        logger.info("The list of members returned " + members);
        return new ResponseEntity<>(members, HttpStatus.OK);
    }

    // Get a member by ID
    @GetMapping("/members/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable Long id) {
        Optional<Member> member = libraryService.getMemberById(id);
        logger.info("The member returned " + member);
        return member.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Update a member
    @PutMapping("/members/{id}")
    public ResponseEntity<Member> updateMember(@PathVariable Long id,
            @RequestBody Member updateMember) {

        if (libraryService.getMemberById(id).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        updateMember.setId(id);

        Optional<Member> updatedMember = libraryService.updateMember(id, updateMember);
        logger.info("The member has been updated " + updatedMember.get());
        return new ResponseEntity<>(updatedMember.get(), HttpStatus.OK);
    }

    // Delete a member
    @DeleteMapping("/members/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        if (libraryService.getMemberById(id).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        libraryService.deleteMember(id);
        logger.info("Member has been deleted: " + id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // ==================== BorrowingRecord Endpoints ====================

    // Get all borrowing records
    @GetMapping("/borrowingrecords")
    public ResponseEntity<List<BorrowingRecord>> getAllBorrowingRecords() {
        List<BorrowingRecord> records = libraryService.getAllBorrowingRecords();
        logger.info("The records has been retrieved " + records);
        return new ResponseEntity<>(records, HttpStatus.OK);
    }

    // Get borrowing records by id
    @GetMapping("/borrowingrecords/{id}")
    public ResponseEntity<BorrowingRecord> getBorrowingRecordsById(@PathVariable Long id) {
        Optional<BorrowingRecord> borrowingRecords = libraryService.getBorrowingRecordsById(id);
        logger.info("The borrowing record returned: " + borrowingRecords);
        return borrowingRecords.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Borrow a book
    @PostMapping("/borrow")
    public ResponseEntity<BorrowingRecord> borrowBook(@RequestBody BorrowingRecord record) {
        Optional<BorrowingRecord> borrowed = libraryService.borrowBook(record);

        if (borrowed.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        logger.info("Book borrowed: " + borrowed.get().getId());
        return new ResponseEntity<>(borrowed.get(), HttpStatus.CREATED);
    }

    // Return a book
    @PutMapping("/return/{id}")
    public ResponseEntity<BorrowingRecord> returnBook(@PathVariable Long id,
            @RequestBody Map<String, String> body) {

        LocalDate returnDate = LocalDate.parse(body.get("returnDate"));

        Optional<BorrowingRecord> returned = libraryService.returnBook(id, returnDate);

        if (returned.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        logger.info("Book returned: " + returned.get().getId());
        return new ResponseEntity<>(returned.get(), HttpStatus.OK);
    }

    // Get books due for return
    @GetMapping("/books/due")
    public ResponseEntity<List<Book>> getBooksDueOnDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<Book> books = libraryService.getBooksDueOnDate(date);
        logger.info("Books due on date {}", date);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    // Check book availability
    @GetMapping("/books/{id}/availability")
    public ResponseEntity<LocalDate> checkAvailability(
            @PathVariable Long id) {

        LocalDate availableDate = libraryService.checkAvailability(id);
        logger.info("Availability checked for book {}: {}", id, availableDate);
        if (availableDate == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(availableDate, HttpStatus.OK);
    }
}
