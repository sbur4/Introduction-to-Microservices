//package com.epam.web.feign;
//
//import com.epam.core.dto.response.DeleteByIdsResponseDto;
//import com.epam.core.dto.request.SongMetadataRequestDto;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.client.ResourceAccessException;
//import org.springframework.web.client.RestTemplate;
//
//@Slf4j
//@Component
//public class CustomFeignClient {
//
//    @Autowired
//    private RestTemplate restTemplate;
//
//    private static final String BASE_URL = "http://localhost:8072/songs";
//
//    public ResponseEntity<String> createNewSongMetadata(SongMetadataRequestDto dto) {
//        log.info("Sending POST request to create new song metadata. Payload: {}", dto);
//
//        try {
//            ResponseEntity<String> response = restTemplate.postForEntity(BASE_URL, dto, String.class);
//            log.info("Received response: {}", response.getBody());
//            return response;
//        } catch (HttpClientErrorException e) {
//            log.error("HTTP error while creating new song metadata: {}", e.getMessage());
//            throw new RuntimeException("Error creating metadata: " + e.getStatusCode(), e);
//        } catch (ResourceAccessException e) {
//            log.error("Error accessing the song service: {}", e.getMessage());
//            throw new RuntimeException("Service is unavailable.", e);
//        } catch (Exception e) {
//            log.error("Unexpected error occurred: {}", e.getMessage());
//            throw new RuntimeException("An unexpected error occurred.", e);
//        }
//    }
//
//    public ResponseEntity<DeleteByIdsResponseDto> deleteSongsMetadataByIds(String requestIds) {
//        String url = BASE_URL + "?id=" + requestIds;
//        log.info("Sending DELETE request for deleting songs metadata by IDs: {}", requestIds);
//
//        try {
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//
//            HttpEntity<?> requestEntity = new HttpEntity<>(headers);
//
//            ResponseEntity<DeleteByIdsResponseDto> response = restTemplate.exchange(
//                    url,
//                    HttpMethod.DELETE,
//                    requestEntity,
//                    DeleteByIdsResponseDto.class
//            );
//
//            log.info("Successfully deleted metadata for IDs: {}", requestIds);
//            return response;
//        } catch (HttpClientErrorException e) {
//            log.error("HTTP error while deleting songs metadata: {}", e.getMessage());
//            throw new RuntimeException("Error deleting song metadata: " + e.getStatusCode(), e);
//        } catch (ResourceAccessException e) {
//            log.error("Error accessing the song service: {}", e.getMessage());
//            throw new RuntimeException("Service is unavailable.", e);
//        } catch (Exception e) {
//            log.error("Unexpected error occurred: {}", e.getMessage());
//            throw new RuntimeException("An unexpected error occurred.", e);
//        }
//    }
//}
