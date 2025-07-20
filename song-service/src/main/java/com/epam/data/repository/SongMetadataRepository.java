package com.epam.data.repository;

import com.epam.data.entity.SongMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SongMetadataRepository extends JpaRepository<SongMetadata, Integer> {

    @Query("SELECT s.id FROM SongMetadata s WHERE s.resourceId IN :ids")
    List<Integer> findExistingIdsByResourceIdIn(@Param("ids") List<Integer> ids);

    Optional<SongMetadata> findByResourceId(Integer resourceId);

    @Modifying
    void deleteByIdIn(List<Integer> ids);
}
