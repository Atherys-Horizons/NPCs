package me.mrdaniel.npcs.catalogtypes.conditions;

import org.spongepowered.api.registry.CatalogRegistryModule;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;

public class ConditionTypeRegistryModule implements CatalogRegistryModule<ConditionType> {

    @Override
    public Optional<ConditionType> getById(@Nonnull final String id) {
        return ConditionTypes.of(id);
    }

    @Override
    public Collection<ConditionType> getAll() {
        return ConditionTypes.VALUES;
    }
}