package com.backtobedrock.augmentedhardcore.domain;

/**
 * Simple record representing a ban entry consisting of the ban's id and the {@link Ban} itself.
 */
public record BanEntry(int id, Ban ban) {
}
