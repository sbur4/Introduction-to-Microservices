package com.epam.web.controller;

import com.epam.core.dto.response.DeletedByIdsResponseDto;
import com.epam.core.dto.response.SongAndMetadataResponseDto;
import com.epam.core.dto.response.UploadedSongResponseDto;
import com.epam.core.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @PostMapping
    public ResponseEntity<UploadedSongResponseDto> uploadFile(HttpEntity<byte[]> mediaContent) {
        return new ResponseEntity<>(resourceService.saveSongAndMetadata(mediaContent.getBody()), HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<SongAndMetadataResponseDto> getSongAndMetadataById(@PathVariable(name = "id") Integer requestId) {
        return ResponseEntity.ok().body(resourceService.getSongAndMetadataById(requestId));
    }

    @DeleteMapping
    public ResponseEntity<DeletedByIdsResponseDto> deleteSongsAndMetadataByIds(@RequestParam(name = "id") String requestIds) {
        return ResponseEntity.ok(resourceService.deleteSongsAndMetadataByIds(requestIds));
    }
}