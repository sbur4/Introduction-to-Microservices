package com.epam.core.cqrs.handler;

import com.epam.core.cqrs.command.DeleteByIdsCommand;
import com.epam.core.cqrs.command.SaveEntityCommand;
import com.epam.core.dto.response.SongMetadataIdResponseDto;

// Command Query Responsibility Segregation (CQRS) design pattern
public interface CommandHandler {

    SongMetadataIdResponseDto saveMetadata(SaveEntityCommand command);

    void deleteByIds(DeleteByIdsCommand command);
}
