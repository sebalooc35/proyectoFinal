package org.backendada.proyectofinal.loan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.backendada.proyectofinal.book.entity.Book;
import org.backendada.proyectofinal.book.repository.BookRepository;
import org.backendada.proyectofinal.loan.entity.Loan;
import org.backendada.proyectofinal.loan.repository.LoanRepository;
import org.backendada.proyectofinal.user.entity.User;
import org.backendada.proyectofinal.user.repository.UserRepository;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import java.time.LocalDate;

@WebMvcTest(LoanController.class)
public class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanRepository loanRepository;
    @MockBean
    private BookRepository bookRepository;
    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Loan loan;
    private Book book;
    private User user;

    @Captor
    private ArgumentCaptor<Loan> loanCaptor;

    @BeforeEach
    public void setup() {
        book = new Book();
        book.setTitle("Book Title");
        book.setAuthor("Author");
        book.setIsbn("12345");

        user = new User();
        user.setName("Name");
        user.setPassword("Password");
        user.setEmail("email@email.com");

        loan = new Loan();
        loan.setUserId(1L);
        loan.setBookId(1L);
        loan.setLoanDate(LocalDate.of(2024, 5, 26));
        loan.setReturnDate(LocalDate.of(2024, 6, 26));

        when(bookRepository.findAll()).thenReturn(Collections.singletonList(book));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);

        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        when(loanRepository.findAll()).thenReturn(Collections.singletonList(loan));
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(loan)).thenReturn(loan);
    }

    @Test
    public void testGetAll() throws Exception {
        mockMvc.perform(get("/api/loan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1))
                .andExpect(jsonPath("$[0].bookId").value(1))
                .andExpect(jsonPath("$[0].loanDate").value("2024-05-26"))
                .andExpect(jsonPath("$[0].returnDate").value("2024-06-26"));

        verify(loanRepository).findAll();
    }

    @Test
    public void testUpdate() throws Exception {
        loan.setReturnDate(LocalDate.of(2024, 7, 26));
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(loan)).thenReturn(loan);

        mockMvc.perform(put("/api/loan/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loan)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.bookId").value(1))
                .andExpect(jsonPath("$.loanDate").value("2024-05-26"))
                .andExpect(jsonPath("$.returnDate").value("2024-07-26"));

        verify(loanRepository).findById(1L);
        verify(loanRepository).save(loanCaptor.capture());
        assert loanCaptor.getValue().getReturnDate().equals(LocalDate.of(2024, 7, 26));
    }

    @Test
    public void testPost() throws Exception {
        Loan newLoan = new Loan();
        newLoan.setBookId(1L);
        newLoan.setUserId(1L);
        newLoan.setLoanDate(LocalDate.of(2024, 6, 26));
        newLoan.setReturnDate(LocalDate.of(2024, 7, 26));

        when(loanRepository.save(newLoan)).thenReturn(newLoan);

        mockMvc.perform(post("/api/loan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newLoan)))
                .andExpect(status().isOk());

        verify(loanRepository).save(loanCaptor.capture());
        assert loanCaptor.getValue().getLoanDate().equals(LocalDate.of(2024, 6, 26));
        assert loanCaptor.getValue().getReturnDate().equals(LocalDate.of(2024, 7, 26));
    }

    @Test
    public void testDelete() throws Exception {
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        mockMvc.perform(delete("/api/loan/1"))
                .andExpect(status().isOk());

        verify(loanRepository).findById(1L);
        verify(loanRepository).delete(loanCaptor.capture());
        assert loanCaptor.getValue().getLoanDate().equals(LocalDate.of(2024, 5, 26));
        assert loanCaptor.getValue().getReturnDate().equals(LocalDate.of(2024, 6, 26));
    }

    @Test
    public void testDeleteNotFound() throws Exception {
        when(loanRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/loan/1"))
                .andExpect(status().isNotFound());

        verify(loanRepository).findById(1L);
        verify(loanRepository, never()).delete(any(Loan.class));
    }
}


