package me.mrdaniel.npcs.commands.action.edit;

import me.mrdaniel.npcs.NPCs;
import me.mrdaniel.npcs.catalogtypes.actions.ActionTypes;
import me.mrdaniel.npcs.commands.ActionCommand;
import me.mrdaniel.npcs.data.npc.actions.Action;
import me.mrdaniel.npcs.data.npc.actions.ActionMessage;
import me.mrdaniel.npcs.exceptions.ActionException;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

import javax.annotation.Nonnull;

public class CommandSetMessage extends ActionCommand {

    public CommandSetMessage(@Nonnull final NPCs npcs) {
        super(npcs, ActionTypes.MESSAGE);
    }

    @Override
    public void execute(final Player p, final Action a, final CommandContext args) throws ActionException {
        ActionMessage am = (ActionMessage) a;
        am.setMessage(args.<String>getOne("message").get());
    }
}