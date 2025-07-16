package com.epam.core.extractor.impl;

import com.epam.core.dto.request.SongMetadataRequestDto;
import com.epam.core.extractor.Extractor;
import com.epam.core.util.DurationFormatUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.metadata.Metadata;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MetadataExtractorImpl implements Extractor<SongMetadataRequestDto, Metadata> {

    @Override
    public SongMetadataRequestDto extractData(Metadata audioMetadata) {
        log.debug("Starting metadata extraction process.");

        String id = StringUtils.strip(audioMetadata.get("xmpDM:trackNumber"));
        String name = StringUtils.strip(audioMetadata.get("dc:title"));
        String artist = StringUtils.strip(audioMetadata.get("xmpDM:artist"));
        String album = StringUtils.strip(audioMetadata.get("xmpDM:album"));
        String duration = DurationFormatUtil.formatDuration(audioMetadata.get("xmpDM:duration"));
        String year = StringUtils.strip(audioMetadata.get("xmpDM:releaseDate"));

        return SongMetadataRequestDto.builder()
                .trackNumber(id)
                .name(name)
                .artist(artist)
                .album(album)
                .duration(duration)
                .year(year)
                .build();
    }
}
