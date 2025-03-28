package com.example.ShelfScrap.repository;
import com.example.ShelfScrap.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book,Long> {
    @Query("select g from Book g where g.genre.genre_id = ?1")
    List<Book> findBookByGenre(@Param("id")Long id);

    @Query("select g from Book g where g.book_status= ?1")
    List<Book> findBooksByBookStatus(@Param("book_status") String book_status);
}
