package me.mrdaniel.npcs.script;

import me.mrdaniel.npcs.io.NPCFile;
import org.spongepowered.api.entity.Entity;

import java.util.function.Function;

/**
 * @jsfunc
 */
public class GetNPCEntity implements Function<NPCFile, Entity> {

    /**
     * Gets the entity object from the NPC, or null.
     */
    @Override
    public Entity apply(NPCFile npcFile) {
        return npcFile.getEntity().orElse(null);
    }
}
