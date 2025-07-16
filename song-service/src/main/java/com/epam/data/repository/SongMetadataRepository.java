package com.epam.data.repository;

import com.epam.data.entity.SongMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongMetadataRepository extends JpaRepository<SongMetadata, Integer> {

//    @Query(value = "SELECT id FROM songs_metadata WHERE resource_id IN :ids", nativeQuery = true)
//    List<Integer> findExistingIds(@Param("ids") List<Integer> ids);

    List<Integer> findByResourceIdIn(List<Integer> ids);
}
