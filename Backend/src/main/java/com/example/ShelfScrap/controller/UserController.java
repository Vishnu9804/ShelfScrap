package com.example.ShelfScrap.controller;

import com.example.ShelfScrap.entities.Book;
import com.example.ShelfScrap.entities.User;
import com.example.ShelfScrap.repository.BookRepository;
import com.example.ShelfScrap.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@RestController
@CrossOrigin("http://localhost:5173")
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    // Sign Up (Register)
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists!");
        }
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully!");
    }

    // Sign In (Login)
    @PostMapping("/signin")
    public ResponseEntity<String> signIn(@RequestBody User user) {
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());

        if (existingUser.isPresent() && user.getPassword().equals(existingUser.get().getPassword())) {
            return ResponseEntity.ok("Login successful!");
        }
        return ResponseEntity.status(401).body("Invalid username or password!");
    }

    // Like a Book
    @PostMapping("/likeBook/{username}/{bookId}")
    public ResponseEntity<String> likeBook(@PathVariable String username, @PathVariable Long bookId) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        Optional<Book> bookOptional = bookRepository.findById(bookId);

        if (userOptional.isPresent() && bookOptional.isPresent()) {
            User user = userOptional.get();
            Book book = bookOptional.get();

            if (!user.getLiked().contains(book)) {
                user.getLiked().add(book);
                userRepository.save(user);
                return ResponseEntity.ok("Book liked successfully!");
            }
            return ResponseEntity.badRequest().body("Book already liked!");
        }
        return ResponseEntity.status(404).body("User or Book not found!");
    }

    // Add a book to the 'Want to Read' list
    @PostMapping("/want_to_read/{username}/{bookId}")
    public ResponseEntity<String> addToWantToRead(@PathVariable String username, @PathVariable Long bookId) {
        return addBookToList(username, bookId, "wantToRead");
    }

    // Add a book to the 'Already Read' list
    @PostMapping("/already_read/{username}/{bookId}")
    public ResponseEntity<String> addToAlreadyRead(@PathVariable String username, @PathVariable Long bookId) {
        return addBookToList(username, bookId, "alreadyRead");
    }

    // Add a book to the 'Currently Reading' list
    @PostMapping("/currently_reading/{username}/{bookId}")
    public ResponseEntity<String> addToCurrentlyReading(@PathVariable String username, @PathVariable Long bookId) {
        return addBookToList(username, bookId, "currentlyReading");
    }

    private void generateAlreadyReadExcel() {
        List<User> users = userRepository.findAll(); // Fetch all users

        // Create a workbook and sheet
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Already Read Books");

        // Populate user data (without a header row)
        int rowIndex = 0; // Start from row 0 instead of 1
        for (User user : users) {
            Row row = sheet.createRow(rowIndex);
            row.createCell(0).setCellValue(user.getUsername());

            // Add the user's already_read books in the same row
            List<Book> alreadyReadBooks = user.getAlreadyRead();
            for (int j = 0; j < alreadyReadBooks.size(); j++) {
                row.createCell(j + 1).setCellValue(alreadyReadBooks.get(j).getBook_id());
            }
            rowIndex++;
        }

        // Auto-size columns
        for (int i = 0; i < users.size(); i++) {
            sheet.autoSizeColumn(i);
        }

        // Write to file
        try (FileOutputStream fileOut = new FileOutputStream("C:\\My Corner\\Java Dev\\ShelfScrap\\ML_Algorithms\\AlreadyReadBooks.xlsx")) {
            workbook.write(fileOut);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Helper method to add a book to a user's list
    private ResponseEntity<String> addBookToList(String username, Long bookId, String listType) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        Optional<Book> bookOptional = bookRepository.findById(bookId);

        if (userOptional.isPresent() && bookOptional.isPresent()) {
            User user = userOptional.get();
            Book book = bookOptional.get();

            // Initialize lists if they are null
            if (user.getWantToRead() == null) user.setWantToRead(new ArrayList<>());
            if (user.getCurrentlyReading() == null) user.setCurrentlyReading(new ArrayList<>());
            if (user.getAlreadyRead() == null) user.setAlreadyRead(new ArrayList<>());

            // Remove book from all lists to ensure it only exists in one
            user.getWantToRead().remove(book);
            user.getCurrentlyReading().remove(book);
            user.getAlreadyRead().remove(book);

            // Add the book to the selected list
            switch (listType) {
                case "wantToRead":
                    user.getWantToRead().add(book);
                    break;
                case "currentlyReading":
                    user.getCurrentlyReading().add(book);
                    break;
                case "alreadyRead":
                    user.getAlreadyRead().add(book);
                    generateAlreadyReadExcel();
                    break;
                default:
                    return ResponseEntity.badRequest().body("Invalid list type!");
            }

            userRepository.save(user);
            return ResponseEntity.ok("Book status updated to '" + listType + "' successfully!");
        }

        return ResponseEntity.status(404).body("User or Book not found!");
    }

    // Get Currently Reading Books
    @GetMapping("/{username}/currently_reading")
    public ResponseEntity<?> getCurrentlyReading(@PathVariable String username) {
        return getUserBookList(username, "currentlyReading");
    }

    // Get Want to Read Books
    @GetMapping("/{username}/want_to_read")
    public ResponseEntity<?> getWantToRead(@PathVariable String username) {
        return getUserBookList(username, "wantToRead");
    }

    // Get Already Read Books
    @GetMapping("/{username}/already_read")
    public ResponseEntity<?> getAlreadyRead(@PathVariable String username) {
        return getUserBookList(username, "alreadyRead");
    }

    // Get Liked Books
    @GetMapping("/{username}/liked_books")
    public ResponseEntity<?> getLikedBooks(@PathVariable String username) {
        return getUserBookList(username, "liked");
    }

    // Get Suggested Books
    @GetMapping("/{username}/suggested_books")
    public ResponseEntity<?> getSuggestedBooks(@PathVariable String username) {
        return getUserBookList(username, "suggested");
    }

    // Generic method to fetch a list of books for a user
    private ResponseEntity<?> getUserBookList(String username, String listType) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            List<Book> books;

            switch (listType) {
                case "currentlyReading":
                    books = user.getCurrentlyReading();
                    break;
                case "wantToRead":
                    books = user.getWantToRead();
                    break;
                case "alreadyRead":
                    books = user.getAlreadyRead();
                    break;
                case "liked":
                    books = user.getLiked();
                    break;
                case "suggested":
                    books = user.getSuggested();
                    break;
                default:
                    return ResponseEntity.badRequest().body("Invalid book list type!");
            }

            if (books == null || books.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            return ResponseEntity.ok(books);
        }

        return ResponseEntity.status(404).body("User not found!");
    }

    // Logout
    @PostMapping("/logout/{username}")
    public ResponseEntity<String> logout(@PathVariable String username) {
        return ResponseEntity.ok("User " + username + " logged out successfully!");
    }
}



