package org.backendada.proyectofinal.loan.controller;

import org.backendada.proyectofinal.book.exception.ResourceNotFoundException;
import org.backendada.proyectofinal.book.repository.BookRepository;
import org.backendada.proyectofinal.loan.entity.Loan;
import org.backendada.proyectofinal.loan.repository.LoanRepository;
import org.backendada.proyectofinal.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/admin/loan")
public class AdminLoanController {

    @Autowired
    LoanRepository loanRepository;
    @Autowired
    BookRepository bookRepository;
    @Autowired
    UserRepository userRepository;

    @GetMapping
    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    @PostMapping
    public Loan createLoan(@RequestBody Loan loan) {
        bookRepository.findById(loan.getBookId()).orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        userRepository.findById(loan.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return loanRepository.save(loan);
    }

    @GetMapping("/{id}")
    public Loan getLoanById(@PathVariable Long id) {
        return loanRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Loan not found"));
    }

    @PutMapping("/{id}")
    public Loan updateLoan(@PathVariable Long id, @RequestBody Loan loanDetails) {
        Loan loan = loanRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Loan not found"));
        loan.setBookId(loanDetails.getBookId());
        loan.setUserId(loanDetails.getUserId());
        loan.setLoanDate(loanDetails.getLoanDate());
        loan.setReturnDate(loanDetails.getReturnDate());
        return loanRepository.save(loan);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLoan(@PathVariable Long id) {
        Loan loan = loanRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Loan not found"));
        loanRepository.delete(loan);
        return ResponseEntity.ok().build();
    }
}
