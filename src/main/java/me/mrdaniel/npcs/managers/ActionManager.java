package me.mrdaniel.npcs.managers;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.mrdaniel.npcs.Npcs;
import me.mrdaniel.npcs.data.npc.NpcData;
import me.mrdaniel.npcs.exceptions.NpcException;
import me.mrdaniel.npcs.io.NpcFile;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ActionManager {

    private final Map<UUID, NpcFile> choosing;
    private final List<UUID> waiting;

    public ActionManager(@Nonnull final Npcs npcs) {
        this.choosing = Maps.newHashMap();
        this.waiting = Lists.newArrayList();
    }

    public void executeChoice(@Nonnull final NpcFile file, @Nonnull final UUID uuid, final int next) throws NpcException {
        NpcFile f = Optional.ofNullable(this.choosing.remove(uuid)).orElseThrow(() -> new NpcException("You are not choosing!"));
        if (f != file) {
            throw new NpcException("You can't execute old choices!");
        }

        file.getCurrent().put(uuid, next);
        file.writeCurrent();
        file.save();
        this.execute(uuid, file);
    }

    public void execute(@Nonnull final UUID uuid, @Nonnull final Living npc) throws NpcException {
        this.execute(uuid, npc.get(NpcData.class).orElseThrow(() -> new NpcException("This Entity is not an Npc!")));
    }

    public void execute(@Nonnull final UUID uuid, @Nonnull final NpcData data) throws NpcException {
        this.execute(uuid, Npcs.getNpcManager().getFile(data.getId()).orElseThrow(() -> new NpcException("Could not find file for Npc!")));
    }

    public void execute(@Nonnull final UUID uuid, @Nonnull final NpcFile file) throws NpcException {
        if (this.waiting.contains(uuid)) {
            return;
        }
        if (file.getActions().size() == 0) {
            return;
        }

        Player p = Npcs.getGame().getServer().getPlayer(uuid).orElseThrow(() -> new NpcException("Player not found!"));
        int next = Optional.ofNullable(file.getCurrent().get(uuid)).orElse(0);

        if (next >= file.getActions().size()) {
            if (file.getRepeatActions()) {
                file.getCurrent().put(uuid, 0);
                file.writeCurrent();
                file.save();
            }
            return;
        }

        ActionResult result = new ActionResult(next);

        file.getActions().get(next).execute(Npcs.getInstance(), result, p, file);

        if (result.getWaitTicks() > 0) {
            this.waiting.add(uuid);
            Task.builder().delayTicks(result.getWaitTicks()).execute(() -> {
                this.waiting.remove(uuid);
                file.getCurrent().put(uuid, result.getNext());
                file.writeCurrent();
                file.save();
                if (result.getPerformNext()) {
                    try {
                        this.execute(uuid, file);
                    } catch (final NpcException ignored) {
                    }
                }
            }).submit(Npcs.getInstance());
        } else {
            file.getCurrent().put(uuid, result.getNext());
            file.writeCurrent();
            file.save();
            if (result.getPerformNext()) {
                this.execute(uuid, file);
            }
        }
    }

    public void setChoosing(@Nonnull final UUID uuid, @Nonnull final NpcFile file) {
        this.choosing.put(uuid, file);
    }

    public void removeChoosing(@Nonnull final UUID uuid) {
        this.choosing.remove(uuid);
    }

    public void removeChoosing(@Nonnull final Living npc) {
        this.choosing.entrySet().stream().filter(e -> e.getValue() == npc).map(Map.Entry::getKey).forEach(this.choosing::remove);
    }
}