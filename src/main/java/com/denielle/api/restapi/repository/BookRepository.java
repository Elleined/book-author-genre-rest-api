package com.denielle.api.restapi.repository;

import com.denielle.api.restapi.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Integer> {
    @Query("select b from Book b where b.isbn = ?1")
    Optional<Book> fetchByIsbn(String isbn);
    @Query("select b from Book b where b.title = ?1")
    Optional<Book> fetchByTitle(String title);

    @Query("SELECT b FROM Book b WHERE b.title LIKE CONCAT(:firstLetter, '%') ORDER BY title")
    List<Book> getAllByTitleFirstLetter(@Param("firstLetter") char firstLetter);

    @Query(value = """
            SELECT
            	b.*
            FROM
            	book b,
                genre g,
                book_genre bg
            WHERE
            	b.id = bg.book_id
            AND
            	g.id = bg.genre_id
            AND
            	g.name = :genreName
            ORDER BY
                b.title
            """, nativeQuery = true)
    List<Book> getAllByGenre(@Param("genreName") String genreName);
}