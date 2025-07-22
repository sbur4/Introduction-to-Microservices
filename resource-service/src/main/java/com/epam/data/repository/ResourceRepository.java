package com.epam.data.repository;

import com.epam.data.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

// Database per service design pattern
@Repository
public interface ResourceRepository extends JpaRepository<Song, Integer> {

    @Query(value = "SELECT id FROM songs WHERE id IN :ids", nativeQuery = true)
    List<Integer> findExistingIds(@Param("ids") List<Integer> ids);
}
