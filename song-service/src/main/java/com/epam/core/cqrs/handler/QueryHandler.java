package com.epam.core.cqrs.handler;

import com.epam.core.cqrs.query.FindByIdQuery;
import com.epam.core.cqrs.query.FindByIdsQuery;
import com.epam.core.dto.response.DeletedByIdsResponseDto;
import com.epam.core.dto.response.SongMetadataResponseDto;

public interface QueryHandler {

    SongMetadataResponseDto findById(FindByIdQuery query);

    DeletedByIdsResponseDto findByIds(FindByIdsQuery query);

    boolean isExistByResourceId(FindByIdQuery query);
}
