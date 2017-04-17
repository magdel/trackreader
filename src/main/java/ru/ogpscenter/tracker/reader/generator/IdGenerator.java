package ru.ogpscenter.tracker.reader.generator;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface IdGenerator {

    @Nonnull
    Long generate();

}