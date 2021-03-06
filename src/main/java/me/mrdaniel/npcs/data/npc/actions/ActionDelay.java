package me.mrdaniel.npcs.data.npc.actions;

import me.mrdaniel.npcs.Npcs;
import me.mrdaniel.npcs.catalogtypes.actions.ActionTypes;
import me.mrdaniel.npcs.io.NpcFile;
import me.mrdaniel.npcs.managers.ActionResult;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;

public class ActionDelay extends Action {

    private int ticks;

    public ActionDelay(@Nonnull final ConfigurationNode node) {
        this(node.getNode("Ticks").getInt(20));
    }

    public ActionDelay(@Nonnull final int ticks) {
        super(ActionTypes.DELAY);

        this.ticks = ticks;
    }

    public void setTicks(final int ticks) {
        this.ticks = ticks;
    }

    @Override
    public void execute(final Npcs npcs, final ActionResult result, final Player p, final NpcFile file) {
        result.setNext(result.getCurrent() + 1).setWaitTicks(this.ticks);
    }

    @Override
    public void serializeValue(final ConfigurationNode node) {
        node.getNode("Ticks").setValue(this.ticks);
    }

    @Override
    public Text getLine(final int index) {
        return Text.of(TextColors.GOLD, "Delay: ", TextColors.AQUA, this.ticks, " ticks");
    }
}