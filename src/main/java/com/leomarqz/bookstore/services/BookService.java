package com.leomarqz.bookstore.services;

import com.leomarqz.bookstore.models.Book;
import com.leomarqz.bookstore.repositories.IBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService implements IBookService{

    @Autowired
    private IBookRepository bookRepository;

    @Override
    public List<Book> listBooks() {
        return this.bookRepository.findAll();
    }

    @Override
    public Book findBookById(Integer id) {
        Book book = this.bookRepository.findById(id).orElse(null);
        return book;
    }

    @Override
    public void createBook(Book book) {
        this.bookRepository.save(book);
    }

    @Override
    public void deleteBook(Integer id) {
        if (this.bookRepository.existsById(id)) {
            this.bookRepository.deleteById(id);
        } else {
            throw new RuntimeException("Book not found with id: " + id);
        }
    }
}
