package com.epam.core.cqrs.command;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SaveEntityCommand {

    byte[] data;
    String checksum;
}
