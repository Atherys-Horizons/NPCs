package me.mrdaniel.npcs.script;

import com.atherys.script.api.library.LibraryExtension;
import com.atherys.script.api.library.ScriptLibrary;

public class NpcExtension implements LibraryExtension {
    private static NpcExtension instance = new NpcExtension();

    private NpcExtension() {

    }

    public static NpcExtension getInstance() {
        return instance;
    }

    @Override
    public void extend(ScriptLibrary scriptLibrary) {
        scriptLibrary.put("createNPC", new CreateNPC());
    }
}
