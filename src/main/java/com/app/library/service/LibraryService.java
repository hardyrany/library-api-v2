package com.app.library.service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import com.app.library.model.Book;
import com.app.library.model.BorrowingRecord;
import com.app.library.model.Member;
import jakarta.annotation.PostConstruct;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Service
public class LibraryService {

    // In-memory storage using ArrayList
    private final List<Book> books = new ArrayList<>();
    private final List<Member> members = new ArrayList<>();
    private final List<BorrowingRecord> borrowingRecords = new ArrayList<>();

    private final AtomicLong nextBookId = new AtomicLong(1);
    private final AtomicLong nextMemberId = new AtomicLong(1);
    private final AtomicLong nextBorrowingId = new AtomicLong(1);

    private final ObjectMapper objectMapper;

    public LibraryService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // ==================== Book Dataset ====================

    /**
     * Load all books from books.json
     */
    @PostConstruct
    public void loadBooks() throws IOException {
        try (InputStream is = new ClassPathResource("data/books.json").getInputStream()) {
            List<Book> loaded = objectMapper.readValue(is, new TypeReference<List<Book>>() {
            });
            books.addAll(loaded);

            long maxId = loaded.stream().mapToLong(Book::getId).max().orElse(0L);
            nextBookId.set(maxId + 1);

            System.out.println(books.size() + " Books load from books.json");
        }
    }

    // ==================== Book Methods ====================

    // Add a new book
    public synchronized Book addBook(Book book) {
        book.setId(nextBookId.getAndIncrement());
        books.add(book);
        return book;
    }

    // Get all books
    public List<Book> getAllBooks() {
        return List.copyOf(books);
    }

    // Get a book by ID
    public Optional<Book> getBookById(Long id) {
        return books.stream().filter(book -> book.getId().equals(id)).findFirst();
    }

    // Get a book by Genre
    public List<Book> getBooksByGenre(String genre) {
        return books.stream()
                .filter(book -> book.getGenre().toLowerCase().contains(genre.toLowerCase()))
                .collect(Collectors.toList());
    }

    // Get a book by Author and Genre
    public List<Book> getBooksByAuthorAndGenre(String author, String genre) {
        return books.stream()
                .filter(book -> book.getAuthor() != null
                        && book.getAuthor().toLowerCase()
                                .contains(author.toLowerCase()))
                .filter(book -> genre == null || genre.isBlank()
                        || (book.getGenre() != null
                                && book.getGenre().toLowerCase()
                                        .contains(genre.toLowerCase())))
                .collect(Collectors.toList());
    }

    // Update a book
    public synchronized Optional<Book> updateBook(Long id, Book updated) {
        for (Book book : books) {
            if (book.getId().equals(id)) {
                book.setTitle(updated.getTitle());
                book.setAuthor(updated.getAuthor());
                book.setPublicationYear(updated.getPublicationYear());
                book.setGenre(updated.getGenre());
                book.setAvailableCopies(updated.getAvailableCopies());
                return Optional.of(book);
            }
        }
        return Optional.empty();
    }

    // Delete a book by ID
    public synchronized Optional<Book> deleteBook(Long id) {
        for (Book book : books) {
            if (book.getId().equals(id)) {
                books.remove(book);
                return Optional.of(book);
            }
        }
        return Optional.empty();
    }

    // ==================== Members Dataset ====================

    /**
     * Load all members from members.json
     */
    @PostConstruct
    public void loadMembers() throws IOException {
        try (InputStream is = new ClassPathResource("data/members.json").getInputStream()) {
            List<Member> loaded = objectMapper.readValue(is, new TypeReference<List<Member>>() {
            });
            members.addAll(loaded);

            long maxId = loaded.stream().mapToLong(Member::getId).max().orElse(0L);
            nextMemberId.set(maxId + 1);

            System.out.println(members.size() + " Members load from members.json");
        }
    }

    // ==================== Member Methods ====================

    // Add a new member
    public synchronized Member addMember(Member member) {
        member.setId(nextMemberId.getAndIncrement());
        members.add(member);
        return member;
    }

    // Get all members
    public List<Member> getAllMembers() {
        return List.copyOf(members);
    }

    // Get a member by ID
    public Optional<Member> getMemberById(Long id) {
        return members.stream().filter(member -> member.getId().equals(id)).findFirst();
    }

    // Update a member
    public synchronized Optional<Member> updateMember(Long id, Member updatedMember) {
        for (Member member : members) {
            if (member.getId().equals(id)) {
                member.setName(updatedMember.getName());
                member.setEmail(updatedMember.getEmail());
                member.setPhoneNumber(updatedMember.getPhoneNumber());
                member.setStartDate(updatedMember.getStartDate());
                member.setEndDate(updatedMember.getEndDate());
                return Optional.of(member);
            }
        }
        return Optional.empty();
    }

    // Delete a member by ID
    public synchronized Optional<Member> deleteMember(Long id) {
        for (Member member : members) {
            if (member.getId().equals(id)) {
                members.remove(member);
                return Optional.of(member);
            }
        }
        return Optional.empty();
    }

    // ==================== Borrowings Dataset ====================

    /**
     * Load all borrowing Records from borrowings.json
     */
    @PostConstruct
    public void loadBorrowings() throws IOException {
        try (InputStream is = new ClassPathResource("data/borrowings.json").getInputStream()) {
            List<BorrowingRecord> loaded = objectMapper.readValue(is, new TypeReference<List<BorrowingRecord>>() {
            });
            borrowingRecords.addAll(loaded);

            long maxId = loaded.stream().mapToLong(BorrowingRecord::getId).max().orElse(0L);
            nextBorrowingId.set(maxId + 1);

            System.out.println(
                    borrowingRecords.size() + " Borrowing Record load from borrowings.json");
        }
    }

    // ==================== BorrowingRecord Methods ====================

    // Get all borrowing records
    public List<BorrowingRecord> getAllBorrowingRecords() {
        return List.copyOf(borrowingRecords);
    }

    // Get borrowing records by Id
    public Optional<BorrowingRecord> getBorrowingRecordsById(Long id) {
        return borrowingRecords.stream()
                .filter(borrowingRecords -> borrowingRecords.getId().equals(id)).findFirst();
    }

    // Borrow a book (create a new borrowing record)
    public synchronized Optional<BorrowingRecord> borrowBook(BorrowingRecord record) {
        if (record == null || record.getBook() == null || record.getMember() == null) {
            return Optional.empty();
        }

        Optional<Book> bookOpt = getBookById(record.getBook().getId());
        if (bookOpt.isEmpty()) {
            return Optional.empty();
        }

        Optional<Member> memberOpt = getMemberById(record.getMember().getId());
        if (memberOpt.isEmpty()) {
            return Optional.empty();
        }

        Book book = bookOpt.get();
        Member member = memberOpt.get();
        if (book.getAvailableCopies() <= 0) {
            return Optional.empty();
        }

        record.setId(nextBorrowingId.getAndIncrement());
        record.setBook(book);
        record.setMember(member);
        record.setBorrowDate(LocalDate.now());
        record.setDueDate(LocalDate.now().plusDays(14));
        record.setReturnDate(null);

        borrowingRecords.add(record);
        book.setAvailableCopies(book.getAvailableCopies() - 1);

        return Optional.of(record);
    }

    // Return a book (update the borrowing record with the return date)
    public synchronized Optional<BorrowingRecord> returnBook(Long recordId, LocalDate returnDate) {
        if (recordId == null || returnDate == null) {
            return Optional.empty();
        }

        for (BorrowingRecord record : borrowingRecords) {
            if (record.getId().equals(recordId)) {
                if (record.getReturnDate() != null) {
                    return Optional.empty();
                }

                Optional<Book> bookOpt = getBookById(record.getBook().getId());
                if (bookOpt.isEmpty()) {
                    return Optional.empty();
                }

                Book book = bookOpt.get();

                record.setReturnDate(returnDate);
                record.setBook(book);
                book.setAvailableCopies(book.getAvailableCopies() + 1);

                return Optional.of(record);
            }
        }
        return Optional.empty();
    }

    // Get books due on date
    public List<Book> getBooksDueOnDate(LocalDate dueDate) {

        return borrowingRecords.stream()
                .filter(record -> record.getDueDate().equals(dueDate))
                .map(record -> record.getBook())
                .collect(Collectors.toList());
    }

    // Check books availability
    public LocalDate checkAvailability(Long bookId) {

        Optional<Book> bookOpt = getBookById(bookId);

        if (bookOpt.isEmpty()) {
            return null;
        }

        Book book = bookOpt.get();
        if (book.getAvailableCopies() >= 1) {
            return LocalDate.now();
        }

        return borrowingRecords.stream()
                .filter(record -> record.getBook().getId().equals(bookId))
                .map(BorrowingRecord::getDueDate)
                .sorted()
                .findFirst()
                .orElse(null);
    }
}
