package org.backendada.proyectofinal.book.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.backendada.proyectofinal.book.entity.Book;
import org.backendada.proyectofinal.book.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

@WebMvcTest(AdminBookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookRepository bookRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Book book;

    @Captor
    private ArgumentCaptor<Book> bookCaptor;

    @BeforeEach
    public void setup() {
        book = new Book();
        book.setTitle("Book Title");
        book.setAuthor("Author");
        book.setIsbn("12345");

        when(bookRepository.findAll()).thenReturn(Collections.singletonList(book));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);
    }

    @Test
    public void testGetAll() throws Exception {
        mockMvc.perform(get("/api/admin/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Book Title"))
                .andExpect(jsonPath("$[0].author").value("Author"))
                .andExpect(jsonPath("$[0].isbn").value("12345"));

        verify(bookRepository).findAll();
    }

    @Test
    public void testUpdate() throws Exception {
        book.setAuthor("Pepe");
        mockMvc.perform(put("/api/admin/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Book Title"))
                .andExpect(jsonPath("$.author").value("Pepe"))
                .andExpect(jsonPath("$.isbn").value("12345"));

        verify(bookRepository).findById(1L);
    }

    @Test
    public void testPost() throws Exception {
        Book newBook = new Book();
        newBook.setTitle("New Book");
        newBook.setAuthor("New Author");
        newBook.setIsbn("67890");

        when(bookRepository.save(newBook)).thenReturn(newBook);

        mockMvc.perform(post("/api/admin/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBook)))
                .andExpect(status().isOk());

        verify(bookRepository).save(bookCaptor.capture());
        assert "New Book".equals(bookCaptor.getValue().getTitle());
        assert "New Author".equals(bookCaptor.getValue().getAuthor());
        assert "67890".equals(bookCaptor.getValue().getIsbn());
    }

    @Test
    public void testDelete() throws Exception {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        mockMvc.perform(delete("/api/admin/books/1"))
                .andExpect(status().isOk());

        verify(bookRepository).findById(1L);
        verify(bookRepository).delete(bookCaptor.capture());
        assert "Book Title".equals(bookCaptor.getValue().getTitle());
        assert "Author".equals(bookCaptor.getValue().getAuthor());
        assert "12345".equals(bookCaptor.getValue().getIsbn());
    }

    @Test
    public void testDeleteNotFound() throws Exception {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/admin/books/1"))
                .andExpect(status().isNotFound());

        verify(bookRepository).findById(1L);
        verify(bookRepository, never()).delete(any(Book.class));
    }

}
