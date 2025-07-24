package com.epam.core.cqrs.handler;

import com.epam.core.cqrs.query.FindByIdQuery;
import com.epam.core.cqrs.query.FindByIdsQuery;
import com.epam.core.dto.FindByIdDto;
import com.epam.core.dto.response.DeletedByIdsResponseDto;

public interface QueryHandler {

    FindByIdDto findById(FindByIdQuery query);

    DeletedByIdsResponseDto findByIds(FindByIdsQuery query);
}
