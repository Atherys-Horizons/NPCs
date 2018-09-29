package me.mrdaniel.npcs.script;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.function.BiFunction;

public class CreateNPC implements BiFunction<String, Location<World>, Boolean> {
    @Override
    public Boolean apply(String entityType, Location<World> location) {
        return true;
    }
}
