package me.mrdaniel.npcs.exceptions;

import javax.annotation.Nonnull;

public class NpcException extends Exception {

    private static final long serialVersionUID = 7147632835099311666L;

    public NpcException(@Nonnull final String message) {
        super(message);
    }

    public NpcException(@Nonnull final String message, @Nonnull final Throwable cause) {
        super(message, cause);
    }
}