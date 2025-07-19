package com.leomarqz.bookstore.services;

import com.leomarqz.bookstore.models.Book;

import java.util.List;

public interface IBookService
{
    List<Book> listBooks();
    Book findBookById(Integer id);
    void createBook(Book book);
    void updateBook(Book book);
    void deleteBook(Integer id);
}
