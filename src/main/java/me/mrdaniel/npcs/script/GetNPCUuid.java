package me.mrdaniel.npcs.script;

import me.mrdaniel.npcs.io.NPCFile;

import java.util.UUID;
import java.util.function.Function;

/**
 * @jsfunc
 */
public class GetNPCUuid implements Function<NPCFile, UUID> {

    @Override
    public UUID apply(NPCFile npcFile) {
        return npcFile.getCache().orElse(null);
    }
}
