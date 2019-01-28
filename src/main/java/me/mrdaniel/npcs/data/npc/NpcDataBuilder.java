package me.mrdaniel.npcs.data.npc;

import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import javax.annotation.Nonnull;
import java.util.Optional;

public class NpcDataBuilder extends AbstractDataBuilder<NpcData> implements DataManipulatorBuilder<NpcData, ImmutableNpcData> {

    public NpcDataBuilder() {
        super(NpcData.class, 1);
    }

    @Override
    public NpcData create() {
        return new NpcData(0, 0, false, true);
    }

    @Override
    public Optional<NpcData> createFrom(@Nonnull DataHolder dataHolder) {
        return create().fill(dataHolder);
    }

    @Override
    protected Optional<NpcData> buildContent(@Nonnull DataView view) throws InvalidDataException {
        return create().from(view);
    }
}