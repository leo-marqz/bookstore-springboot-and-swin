
package com.leomarqz.bookstore.repositories;

import com.leomarqz.bookstore.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IBookRepository extends JpaRepository<Book,Integer>
{
}
