package me.mrdaniel.npcs.listeners;

import me.mrdaniel.npcs.Npcs;
import me.mrdaniel.npcs.data.npc.NpcData;
import me.mrdaniel.npcs.events.NpcEvent;
import me.mrdaniel.npcs.exceptions.NpcException;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.CollideEntityEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.game.state.GameStoppingEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;

public class WorldListener {

    public WorldListener(@Nonnull final Npcs npcs) {
    }

    @Listener
    public void onUnloadWorld(final GameStoppingEvent e) {
        Npcs.getNpcManager().getAll().forEach(npcFile -> {
            try {
                if (npcFile.isTemporary()) {
                    Npcs.getNpcManager().remove(npcFile.getId());
                }
            } catch(NpcException exception) {
               Npcs.getInstance().getLogger().error("Npc with ID {} does not exist", npcFile.getId());
            }
        });
    }

    @Listener(order = Order.LATE)
    public void onLoadWorld(final LoadWorldEvent e) {
        World w = e.getTargetWorld();
        Task.builder().delayTicks(100).execute(() -> Npcs.getNpcManager().load(w)).submit(Npcs.getInstance());
    }

    @Listener(order = Order.EARLY)
    public void onEntitySpawn(final SpawnEntityEvent e) {
        e.getEntities().forEach(ent -> ent.get(NpcData.class).ifPresent(data -> data.ifOld(Npcs.getStartup(), ent::remove)));
    }

    @Listener(order = Order.EARLY)
    public void onCollide(final CollideEntityEvent e) {
        e.getEntities().forEach(ent -> ent.get(NpcData.class).ifPresent(data -> e.setCancelled(true)));
    }

    @Listener(order = Order.EARLY)
    public void onDamage(final DamageEntityEvent e) {
        e.getTargetEntity().get(NpcData.class).ifPresent(data -> e.setCancelled(true));
    }

    @Listener(order = Order.EARLY)
    public void onClick(final InteractEntityEvent e, @Root final Player p) {
        e.getTargetEntity().get(NpcData.class).ifPresent(data -> {
            Living npc = (Living) e.getTargetEntity();
            e.setCancelled(!data.canInteract());

            if (e instanceof InteractEntityEvent.Secondary.MainHand) {
                Npcs.getNpcManager().getFile(data.getId()).ifPresent(file -> {
                    if (p.get(Keys.IS_SNEAKING).orElse(false) && p.hasPermission("npc.edit.select")) {
                        Npcs.getMenuManager().select(p, npc, file);
                    } else {
                        if (!Npcs.getGame().getEventManager().post(new NpcEvent.Interact(Npcs.getContainer(), p, npc, file))) {
                            try {
                                Npcs.getActionManager().execute(p.getUniqueId(), file);
                            } catch (final NpcException exc) {
                                p.sendMessage(Text.of(TextColors.RED, "Failed to perform Npc actions: " + exc.getMessage()));
                            }
                        }
                    }
                });
            }
        });
    }

    @Listener(order = Order.LATE)
    public void onQuit(final ClientConnectionEvent.Disconnect e) {
        Npcs.getMenuManager().deselect(e.getTargetEntity().getUniqueId());
        Npcs.getActionManager().removeChoosing(e.getTargetEntity().getUniqueId());
    }
}