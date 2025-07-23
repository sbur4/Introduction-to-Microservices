package com.epam.core.cqrs.handler.impl;

import com.epam.core.cqrs.command.DeleteByIdsCommand;
import com.epam.core.cqrs.command.SaveEntityCommand;
import com.epam.core.cqrs.handler.CommandHandler;
import com.epam.core.dto.response.UploadedSongResponseDto;
import com.epam.data.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SongCommandHandlerImpl implements CommandHandler {

    private final ResourceRepository resourceRepository;

    @Override
    @Transactional
    public UploadedSongResponseDto saveSong(SaveEntityCommand command) {
        return null;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteByIds(DeleteByIdsCommand command) {
        resourceRepository.deleteAllById(command.getIds());
    }
}
