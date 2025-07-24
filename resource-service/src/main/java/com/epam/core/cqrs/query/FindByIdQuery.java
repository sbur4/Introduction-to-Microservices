package com.epam.core.cqrs.query;

import lombok.Value;

@Value
public class FindByIdQuery {
    int id;
}
