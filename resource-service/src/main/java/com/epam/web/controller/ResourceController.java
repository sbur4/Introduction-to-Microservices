package com.epam.web.controller;

import com.epam.core.dto.response.DeletedByIdsResponseDto;
import com.epam.core.dto.response.UploadedSongResponseDto;
import com.epam.core.service.ResourceService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @SneakyThrows
    @GetMapping(path = "/{id}")
    public ResponseEntity<Resource> getSongById(@Positive @PathVariable(name = "id") Integer requestId) {
        Resource resource = resourceService.getSongById(requestId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .contentLength(resource.contentLength())
                .body(resource);
    }

    @DeleteMapping
    public ResponseEntity<DeletedByIdsResponseDto> deleteSongsAndMetadataByIds(@RequestParam(name = "id") String requestIds) {
        return ResponseEntity.ok(resourceService.deleteSongsAndMetadataByIds(requestIds));
    }
}

// todo compl future
