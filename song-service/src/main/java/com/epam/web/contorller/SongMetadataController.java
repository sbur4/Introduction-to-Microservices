package com.epam.web.contorller;

import com.epam.core.dto.request.SongMetadataRequestDto;
import com.epam.core.dto.response.DeletedByIdsResponseDto;
import com.epam.core.dto.response.SongMetadataIdResponseDto;
import com.epam.core.dto.response.SongMetadataResponseDto;
import com.epam.core.service.SongMetadataService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/songs")
@RequiredArgsConstructor
public class SongMetadataController {

    private final SongMetadataService songMetadataService;

    @PostMapping
    public ResponseEntity<SongMetadataIdResponseDto> createNewMetadata(@RequestBody @Valid SongMetadataRequestDto requestDto) {
        return new ResponseEntity<>(songMetadataService.saveMetadata(requestDto), HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<SongMetadataResponseDto> getMetadataById(@Positive(message = "ID must be a positive number.")
                                                                   @PathVariable(name = "id") Integer requestId) {
        return ResponseEntity.ok().body(songMetadataService.getMetadataById(requestId));
    }

    @DeleteMapping
    public ResponseEntity<DeletedByIdsResponseDto> deleteMetadataByIds(@RequestParam(name = "id") String requestIds) {
        return ResponseEntity.ok(songMetadataService.deleteMetadataByIds(requestIds));
    }
}
