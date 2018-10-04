package me.mrdaniel.npcs.script;

import me.mrdaniel.npcs.NPCs;
import me.mrdaniel.npcs.exceptions.NPCException;
import me.mrdaniel.npcs.io.NPCFile;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.function.BiFunction;

/**
 * @jsfunc
 */
public class CreateNPC implements BiFunction<String, Location<World>, NPCFile> {

    /**
     * Creates an NPC of the given type at the given location.
     */
    @Override
    public NPCFile apply(String entityTypeId, Location<World> location) {
        Optional<EntityType> type = Sponge.getRegistry().getType(EntityType.class, entityTypeId);
        return type.map(entityType-> {
            try {
                NPCFile npc = NPCs.getNPCManager().create(entityType, location, true);
                npc.setTemporary(true);
                return npc;
            } catch (NPCException e) {
                NPCs.getInstance().getLogger().error(e.toString());
                return null;
            }
        }).orElse(null);
    }
}
