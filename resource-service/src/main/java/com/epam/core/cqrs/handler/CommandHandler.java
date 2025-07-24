package com.epam.core.cqrs.handler;

import com.epam.core.cqrs.command.DeleteByIdsCommand;
import com.epam.data.entity.Song;

// Command Query Responsibility Segregation (CQRS) design pattern
public interface CommandHandler {

    Song saveSong(Song rawSong);

    void deleteByIds(DeleteByIdsCommand command);
}
