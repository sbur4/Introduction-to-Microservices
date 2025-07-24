package com.epam.core.cqrs.command;

import lombok.Value;

import java.util.List;

@Value
public class DeleteByIdsCommand {
    List<Integer> ids;
}
