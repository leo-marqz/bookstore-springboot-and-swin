package com.leomarqz.bookstore.services;

import com.leomarqz.bookstore.models.Book;

import java.util.List;

public interface IBookService
{
    List<Book> listBooks();
    void findBookById(Integer id);
    void createBook(String title, String author, Double price, Integer stock);
    void updateBook(Integer id, String title, String author, Double price, Integer stock);
    void deleteBook(Integer id);
}
