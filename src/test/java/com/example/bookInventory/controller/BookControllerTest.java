package com.example.bookInventory.controller;

import com.example.bookInventory.model.Book;
import com.example.bookInventory.repository.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
class BookControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String API_ENDPOINT = "/api/books";
    private Book book1, book2;
    private List<Book> bookList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();

        book1 = Book.builder()
                .title("Title X")
                .author("Author X")
                .build();
        book2 = Book.builder()
                .title("Title Y")
                .author("Author Y")
                .build();
        bookList.add(book1);
        bookList.add(book2);
    }

    @Test
    @DisplayName("** JUNIT test: Get all books from Book Controller. **")
    void getAllBooks() throws Exception {
        //arrange
        bookRepository.saveAll(bookList);
        //act
        ResultActions resultActions = mockMvc.perform(get(API_ENDPOINT));
        //assert
        resultActions.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()", is(bookList.size())));
    }

    @Test
    @DisplayName("** JUNIT test: Get book by Id. **")
    void getBookById() throws Exception {
        bookRepository.save(book1);
        ResultActions resultActions = mockMvc.perform(get(API_ENDPOINT + "/{id}", book1.getId()));
        resultActions.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.title").value(book1.getTitle()))
                .andExpect(jsonPath("$.author").value(book1.getAuthor()))
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains(book1.getTitle())));
    }

    @Test
    @DisplayName("** JUNIT test: Create a book **")
    void createBook() throws Exception {
        //arrange
        String requestBody = objectMapper.writeValueAsString(book1);
        //act
        ResultActions resultActions = mockMvc.perform(post(API_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));
        //assert
        resultActions.andExpect(status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.title").value(book1.getTitle()))
                .andExpect(jsonPath("$.author").value(book1.getAuthor()))
                .andExpect(result -> assertNotNull(result.getResponse().getContentAsString()))
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains(book1.getTitle())));
    }

    @Test
    @DisplayName("** JUNIT test: Update a book from Book Control **")
    void updateBook() throws Exception {
        //arrange
        bookRepository.save(book1);
        Book updateBook1 = bookRepository.findById(book1.getId()).get();
        updateBook1.setTitle("Updated Title X");
        updateBook1.setAuthor("Updated Author X");
        String requestBody = objectMapper.writeValueAsString(updateBook1);
        //act
        ResultActions resultActions = mockMvc.perform(put(API_ENDPOINT.concat("/{id}"), updateBook1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));
        //assert
        resultActions.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.title").value(updateBook1.getTitle()))
                .andExpect(jsonPath("$.author").value(updateBook1.getAuthor()));
    }

    @Test
    @DisplayName("** JUNIT test: Delete a book from Book Control **")
    void deleteBook() throws Exception {
        //arrange
        bookRepository.save(book1);
        Book deleteBook1 = bookRepository.findById(book1.getId()).get();
        String expectedResponse = String.format("%s deleted successfully", deleteBook1.getTitle());
        //act
        ResultActions resultActions = mockMvc.perform(delete(API_ENDPOINT.concat("/{id}"), deleteBook1.getId())
                .contentType(MediaType.APPLICATION_JSON));
        //assert
        resultActions.andExpect(status().isOk())
                .andDo(print())
                .andExpect(result -> assertEquals(expectedResponse, result.getResponse().getContentAsString()));
    }
}