package me.mrdaniel.npcs.script;

import me.mrdaniel.npcs.io.NPCFile;

import java.util.UUID;
import java.util.function.Function;

/**
 * @jsfunc
 */
public class GetNPCUuid implements Function<NPCFile, UUID> {

    /**
     * Gets the UUID from an NPC. This will be invalidated after a world restart.
     * @param npc The NPC.
     * @return The NPC's UUID.
     */
    @Override
    public UUID apply(NPCFile npc) {
        return npc.getCache().orElse(null);
    }
}
