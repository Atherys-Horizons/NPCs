package me.mrdaniel.npcs.script;

import me.mrdaniel.npcs.io.NPCFile;
import org.spongepowered.api.entity.ArmorEquipable;

import java.util.UUID;
import java.util.function.BiFunction;

/**
 * @jsfunc
 */
public class SetNPCSkin implements BiFunction<NPCFile, UUID, Boolean> {

    @Override
    public Boolean apply(NPCFile npcFile, UUID uuid) {
        if (npcFile.getType().get() instanceof ArmorEquipable) {
            npcFile.setSkinUUID(uuid);
            return true;
        }

        return false;
    }
}
