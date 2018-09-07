package me.mrdaniel.npcs.catalogtypes.menupages;

import org.spongepowered.api.registry.CatalogRegistryModule;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;

public class PageTypeRegistryModule implements CatalogRegistryModule<PageType> {

    @Override
    public Optional<PageType> getById(@Nonnull final String id) {
        return PageTypes.of(id);
    }

    @Override
    public Collection<PageType> getAll() {
        return PageTypes.VALUES;
    }
}