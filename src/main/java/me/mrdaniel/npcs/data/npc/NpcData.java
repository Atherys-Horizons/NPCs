package me.mrdaniel.npcs.data.npc;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.base.Preconditions;
import me.mrdaniel.npcs.data.NpcKeys;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.function.BinaryOperator;

public class NpcData extends AbstractData<NpcData, ImmutableNpcData> {

    private int startup;
    private int id;
    private boolean looking;
    private boolean interact;

    public NpcData(final int startup, final int id, final boolean looking, final boolean interact) {
        this.startup = startup;
        this.id = id;
        this.looking = looking;
        this.interact = interact;

        registerGettersAndSetters();
    }

    @Override
    protected void registerGettersAndSetters() {
        registerKeyValue(NpcKeys.STARTUP, this::getStartupValue);
        registerFieldGetter(NpcKeys.STARTUP, this::getStartup);
        registerFieldSetter(NpcKeys.STARTUP, this::setStartup);

        registerKeyValue(NpcKeys.ID, this::getIdValue);
        registerFieldGetter(NpcKeys.ID, this::getId);
        registerFieldSetter(NpcKeys.ID, this::setId);

        registerKeyValue(NpcKeys.LOOKING, this::getLookingValue);
        registerFieldGetter(NpcKeys.LOOKING, this::isLooking);
        registerFieldSetter(NpcKeys.LOOKING, this::setLooking);

        registerKeyValue(NpcKeys.INTERACT, this::getInteractValue);
        registerFieldGetter(NpcKeys.INTERACT, this::canInteract);
        registerFieldSetter(NpcKeys.INTERACT, this::setInteract);
    }

    public Value<Integer> getStartupValue() {
        return NpcKeys.FACTORY.createValue(NpcKeys.STARTUP, this.startup);
    }

    public int getStartup() {
        return this.startup;
    }

    public void setStartup(final int startup) {
        this.startup = startup;
    }

    public Value<Integer> getIdValue() {
        return NpcKeys.FACTORY.createValue(NpcKeys.ID, this.id);
    }

    public int getId() {
        return this.id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public Value<Boolean> getLookingValue() {
        return NpcKeys.FACTORY.createValue(NpcKeys.LOOKING, this.looking);
    }

    public boolean isLooking() {
        return this.looking;
    }

    public void setLooking(final boolean looking) {
        this.looking = looking;
    }

    public Value<Boolean> getInteractValue() {
        return NpcKeys.FACTORY.createValue(NpcKeys.INTERACT, this.interact);
    }

    public boolean canInteract() {
        return this.interact;
    }

    public void setInteract(final boolean interact) {
        this.interact = interact;
    }

    @Nonnull
    public Optional<NpcData> from(@Nonnull final DataView view) {
        return Optional.of(new NpcData(
                view.getInt(NpcKeys.STARTUP.getQuery()).orElse(0),
                view.getInt(NpcKeys.ID.getQuery()).orElse(0),
                view.getBoolean(NpcKeys.LOOKING.getQuery()).orElse(false),
                view.getBoolean(NpcKeys.INTERACT.getQuery()).orElse(true)));
    }

    @Override
    public DataContainer toContainer() {
        return this.asImmutable().toContainer();
    }

    @Override
    public Optional<NpcData> fill(DataHolder holder, MergeFunction overlap) {
        return Optional.of(Preconditions.checkNotNull(overlap).merge(copy(), from(holder.toContainer()).orElse(null)));
    }

    @Override
    public Optional<NpcData> from(DataContainer container) {
        return from((DataView) container);
    }

    @Override
    public NpcData copy() {
        return new NpcData(this.startup, this.id, this.looking, this.interact);
    }

    @Override
    public ImmutableNpcData asImmutable() {
        return new ImmutableNpcData(this.startup, this.id, this.looking, this.interact);
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    public void tick(@Nonnull final Living npc) {
        if (this.looking) {
            Vector3d pos = npc.getLocation().getPosition();
            npc.getNearbyEntities(e -> e instanceof Player && e.getLocation().getPosition().distance(npc.getLocation().getPosition()) <= 20.0).stream().reduce(BinaryOperator.minBy((p1, p2) -> (int) (p1.getLocation().getPosition().distance(pos) - p2.getLocation().getPosition().distance(pos)))).ifPresent(p -> npc.lookAt(p.getLocation().getPosition().add(0.0, 1.62, 0.0)));
        }
    }

    public void ifOld(@Nonnull final int startup, @Nonnull final Runnable run) {
        if (this.startup != startup) {
            run.run();
        }
    }
}