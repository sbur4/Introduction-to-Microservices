package com.epam.core.cqrs.command;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SaveEntityCommand {

    int resourceId;
    String name;
    String artist;
    String album;
    String duration;
    String year;
}
