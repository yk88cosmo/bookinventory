package com.example.bookInventory.controller;

import com.example.bookInventory.exception.ResourceNotFoundException;
import com.example.bookInventory.model.Book;
import com.example.bookInventory.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
@Validated
public class BookController {
    @Autowired
    private BookService bookService;
    @GetMapping
    public ResponseEntity<Object> getAllBooks() {
        List<Book> bookList = bookService.findAllBooks();
        if(bookList.isEmpty()){
            throw new ResourceNotFoundException();
        }
        return new ResponseEntity<>(bookService.findAllBooks(), HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Book book = bookService.findBookById(id).orElseThrow(() -> new ResourceNotFoundException());
        return new ResponseEntity<>(book, HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<Book> createBook(@Valid @RequestBody Book book) {
        return new ResponseEntity<>(bookService.saveBook(book), HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @Valid @RequestBody Book bookDetails) {
        Book checkBook = bookService.findBookById(id).map(_book -> {
            _book.setTitle(bookDetails.getTitle());
            _book.setAuthor(bookDetails.getAuthor());
            return bookService.saveBook(_book);
        }).orElseThrow(() -> new ResourceNotFoundException());
        return new ResponseEntity<>(checkBook, HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteBook(@PathVariable Long id) {
        Book checkBook = bookService.findBookById(id).map(_book -> {
            bookService.deleteBookById(_book.getId());
            return _book;
        }).orElseThrow(() -> new ResourceNotFoundException());
        String response = String.format("%s deleted successfully", checkBook.getTitle());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
