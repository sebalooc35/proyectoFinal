package org.backendada.proyectofinal.book.repository;

import org.backendada.proyectofinal.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
    public void deleteById(Long id);
}
