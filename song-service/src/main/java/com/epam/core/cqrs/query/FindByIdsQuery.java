package com.epam.core.cqrs.query;

import lombok.Value;

import java.util.List;

@Value
public class FindByIdsQuery {
    List<Integer> ids;
}
