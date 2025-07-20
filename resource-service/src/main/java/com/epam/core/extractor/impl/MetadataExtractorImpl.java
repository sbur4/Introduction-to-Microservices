package com.epam.core.extractor.impl;

import com.epam.core.dto.request.SongMetadataRequestDto;
import com.epam.core.exception.MetadataExtractException;
import com.epam.core.extractor.Extractor;
import com.epam.core.util.DurationFormatUtil;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.metadata.Metadata;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MetadataExtractorImpl implements Extractor<SongMetadataRequestDto, Metadata> {

    static String DC_TITLE = "dc:title";
    static String XMPDM_ARTIST = "xmpDM:artist";
    static String XMPDM_ALBUM = "xmpDM:album";
    static String XMPDM_DURATION = "xmpDM:duration";
    static String XMPDM_RELEASE_DATE = "xmpDM:releaseDate";

    @Override
    public SongMetadataRequestDto extractData(Metadata audioMetadata) {
        log.debug("Starting metadata extraction process.");

        try {
            String name = getMetadataValue(audioMetadata, DC_TITLE);
            String artist = getMetadataValue(audioMetadata, XMPDM_ARTIST);
            String album = getMetadataValue(audioMetadata, XMPDM_ALBUM);
            String duration = Optional.ofNullable(audioMetadata.get(XMPDM_DURATION))
                    .map(DurationFormatUtil::formatDuration)
                    .orElse(null);
            String year = getMetadataValue(audioMetadata, XMPDM_RELEASE_DATE);

            log.debug("Metadata extraction completed successfully.");
            return SongMetadataRequestDto.builder()
                    .name(name)
                    .artist(artist)
                    .album(album)
                    .duration(duration)
                    .year(year)
                    .build();
        } catch (Exception ex) {
            log.error("Error during metadata extraction: {}", ex.getMessage(), ex);
            throw new MetadataExtractException("Failed to extract metadata from audio file.");
        }
    }

    private String getMetadataValue(Metadata metadata, String key) {
        return Optional.ofNullable(metadata.get(key))
                .map(StringUtils::strip)
                .orElse(null);
    }
}
