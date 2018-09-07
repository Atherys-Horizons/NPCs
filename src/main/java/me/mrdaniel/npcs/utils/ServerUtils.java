package me.mrdaniel.npcs.utils;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.cause.Cause;

import javax.annotation.Nonnull;

public class ServerUtils {

    @Nonnull
    public static Cause getSpawnCause(@Nonnull final Entity e) {
        return Cause.builder().append(e).build(Sponge.getCauseStackManager().getCurrentContext());
    }
}