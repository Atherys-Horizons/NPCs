package me.mrdaniel.npcs.data.npc.actions;

import me.mrdaniel.npcs.Npcs;
import me.mrdaniel.npcs.catalogtypes.actions.ActionTypes;
import me.mrdaniel.npcs.io.NpcFile;
import me.mrdaniel.npcs.managers.ActionResult;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;

public class ActionPlayerCommand extends Action {

    private String command;

    public ActionPlayerCommand(@Nonnull final ConfigurationNode node) {
        this(node.getNode("Command").getString(""));
    }

    public ActionPlayerCommand(@Nonnull final String command) {
        super(ActionTypes.PLAYERCMD);

        this.command = command;
    }

    public void setCommand(@Nonnull final String command) {
        this.command = command;
    }

    @Override
    public void execute(final Npcs npcs, final ActionResult result, final Player p, final NpcFile file) {
        npcs.getGame().getCommandManager().process(p, npcs.getPlaceHolderManager().formatCommand(p, this.command));
        result.setNext(result.getCurrent() + 1);
    }

    @Override
    public void serializeValue(final ConfigurationNode node) {
        node.getNode("Command").setValue(this.command);
    }

    @Override
    public Text getLine(final int index) {
        return Text.builder().append(Text.of(TextColors.GOLD, "Player Command: "),
                Text.builder().append(Text.of(TextColors.AQUA, this.command))
                        .onHover(TextActions.showText(Text.of(TextColors.YELLOW, "Change")))
                        .onClick(TextActions.suggestCommand("/npc action edit " + index + " playercmd <command...>"))
                        .build()).build();
    }
}