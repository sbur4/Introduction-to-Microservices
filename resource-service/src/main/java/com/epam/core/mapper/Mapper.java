package com.epam.core.mapper;

@FunctionalInterface
public interface Mapper<T> {

    T mapData(Object... objs);
}
