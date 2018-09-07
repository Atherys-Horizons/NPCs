package me.mrdaniel.npcs.events;

import me.mrdaniel.npcs.io.NPCFile;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;
import org.spongepowered.api.plugin.PluginContainer;

import javax.annotation.Nonnull;

public class NPCEvent extends AbstractEvent implements Cancellable {

    private final Player player;
    private final NPCFile file;
    private final Living npc;
    private final Cause cause;

    private boolean cancelled;

    protected NPCEvent(@Nonnull final PluginContainer container, @Nonnull final Player player, @Nonnull final Living npc, @Nonnull final NPCFile file) {
        this.player = player;
        this.npc = npc;
        this.file = file;
        this.cause = Cause.builder().append(player).append(npc).append(file).build(Sponge.getCauseStackManager().getCurrentContext());

        this.cancelled = false;
    }

    @Nonnull
    public Player getPlayer() {
        return this.player;
    }

    @Nonnull
    public Living getNPC() {
        return this.npc;
    }

    @Nonnull
    public NPCFile getFile() {
        return this.file;
    }

    @Nonnull
    @Override
    public Cause getCause() {
        return this.cause;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }

    public static class Interact extends NPCEvent {
        public Interact(@Nonnull final PluginContainer container, @Nonnull final Player player, @Nonnull final Living npc, @Nonnull final NPCFile file) {
            super(container, player, npc, file);
        }
    }

    public static class Edit extends NPCEvent {
        public Edit(@Nonnull final PluginContainer container, @Nonnull final Player player, @Nonnull final Living npc, @Nonnull final NPCFile file) {
            super(container, player, npc, file);
        }
    }

    public static class Select extends NPCEvent {
        public Select(@Nonnull final PluginContainer container, @Nonnull final Player player, @Nonnull final Living npc, @Nonnull final NPCFile file) {
            super(container, player, npc, file);
        }
    }

    public static class Remove extends NPCEvent {
        public Remove(@Nonnull final PluginContainer container, @Nonnull final Player player, @Nonnull final Living npc, @Nonnull final NPCFile file) {
            super(container, player, npc, file);
        }
    }
}