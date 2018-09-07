package me.mrdaniel.npcs.catalogtypes.actions;

import org.spongepowered.api.registry.CatalogRegistryModule;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;

public class ActionTypeRegistryModule implements CatalogRegistryModule<ActionType> {

    @Override
    public Optional<ActionType> getById(@Nonnull final String id) {
        return ActionTypes.of(id);
    }

    @Override
    public Collection<ActionType> getAll() {
        return ActionTypes.VALUES;
    }
}