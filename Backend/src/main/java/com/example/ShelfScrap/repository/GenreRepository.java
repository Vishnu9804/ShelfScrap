package com.example.ShelfScrap.repository;

import com.example.ShelfScrap.entities.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre,Long> {
}
