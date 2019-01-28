package me.mrdaniel.npcs.events;

import me.mrdaniel.npcs.io.NpcFile;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;
import org.spongepowered.api.plugin.PluginContainer;

import javax.annotation.Nonnull;

public class NpcEvent extends AbstractEvent implements Cancellable {

    private final Player player;
    private final NpcFile file;
    private final Living npc;
    private final Cause cause;

    private boolean cancelled;

    protected NpcEvent(@Nonnull final PluginContainer container, @Nonnull final Player player, @Nonnull final Living npc, @Nonnull final NpcFile file) {
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
    public Living getNpc() {
        return this.npc;
    }

    @Nonnull
    public NpcFile getFile() {
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

    public static class Interact extends NpcEvent {
        public Interact(@Nonnull final PluginContainer container, @Nonnull final Player player, @Nonnull final Living npc, @Nonnull final NpcFile file) {
            super(container, player, npc, file);
        }
    }

    public static class Edit extends NpcEvent {
        public Edit(@Nonnull final PluginContainer container, @Nonnull final Player player, @Nonnull final Living npc, @Nonnull final NpcFile file) {
            super(container, player, npc, file);
        }
    }

    public static class Select extends NpcEvent {
        public Select(@Nonnull final PluginContainer container, @Nonnull final Player player, @Nonnull final Living npc, @Nonnull final NpcFile file) {
            super(container, player, npc, file);
        }
    }

    public static class Remove extends NpcEvent {
        public Remove(@Nonnull final PluginContainer container, @Nonnull final Player player, @Nonnull final Living npc, @Nonnull final NpcFile file) {
            super(container, player, npc, file);
        }
    }
}