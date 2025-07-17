package com.epam.data.repository;

import com.epam.data.entity.SongMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongMetadataRepository extends JpaRepository<SongMetadata, Integer> {

    List<Integer> findByIdIn(List<Integer> ids);
}