package me.mrdaniel.npcs.script;

import me.mrdaniel.npcs.io.NPCFile;
import org.spongepowered.api.text.Text;

import java.util.function.BiFunction;

/**
 * @jsfunc
 */
public class SetNPCName implements BiFunction<NPCFile, Text, Boolean> {

    @Override
    public Boolean apply(NPCFile npcFile, Text name) {
        npcFile.setName(name);
        return true;
    }
}
