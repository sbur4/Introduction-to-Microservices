package com.epam.core.extractor;

@FunctionalInterface
public interface Extractor<T, R> {

    T extractData(R data);
}
