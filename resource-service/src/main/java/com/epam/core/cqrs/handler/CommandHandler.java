package com.epam.core.cqrs.handler;

import com.epam.core.cqrs.command.DeleteByIdsCommand;
import com.epam.core.cqrs.command.SaveEntityCommand;
import com.epam.core.dto.response.UploadedSongResponseDto;

// Command Query Responsibility Segregation (CQRS) design pattern
public interface CommandHandler {

    UploadedSongResponseDto saveSong(SaveEntityCommand command);

    void deleteByIds(DeleteByIdsCommand command);
}
