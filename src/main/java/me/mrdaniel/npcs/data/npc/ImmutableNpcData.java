package me.mrdaniel.npcs.data.npc;

import me.mrdaniel.npcs.data.NpcKeys;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableData;

public class ImmutableNpcData extends AbstractImmutableData<ImmutableNpcData, NpcData> {

    private final int startup;
    private final int id;
    private final boolean looking;
    private final boolean interact;

    public ImmutableNpcData(final int startup, final int id, final boolean looking, final boolean interact) {
        this.startup = startup;
        this.id = id;
        this.looking = looking;
        this.interact = interact;

        registerGetters();
    }

    @Override
    protected void registerGetters() {
        registerFieldGetter(NpcKeys.STARTUP, () -> this.startup);
        registerFieldGetter(NpcKeys.ID, () -> this.id);
        registerFieldGetter(NpcKeys.LOOKING, () -> this.looking);
        registerFieldGetter(NpcKeys.INTERACT, () -> this.interact);
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer()
                .set(NpcKeys.STARTUP.getQuery(), this.startup)
                .set(NpcKeys.ID.getQuery(), this.id)
                .set(NpcKeys.LOOKING.getQuery(), this.looking)
                .set(NpcKeys.INTERACT.getQuery(), this.interact);
    }

    @Override
    public NpcData asMutable() {
        return new NpcData(this.startup, this.id, this.looking, this.interact);
    }

    @Override
    public int getContentVersion() {
        return 1;
    }
}