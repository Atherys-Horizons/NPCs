package me.mrdaniel.npcs.commands.action.edit;

import me.mrdaniel.npcs.Npcs;
import me.mrdaniel.npcs.catalogtypes.actions.ActionTypes;
import me.mrdaniel.npcs.commands.ActionCommand;
import me.mrdaniel.npcs.data.npc.actions.Action;
import me.mrdaniel.npcs.data.npc.actions.ActionPlayerCommand;
import me.mrdaniel.npcs.exceptions.ActionException;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

import javax.annotation.Nonnull;

public class CommandSetPlayerCommand extends ActionCommand {

    public CommandSetPlayerCommand(@Nonnull final Npcs npcs) {
        super(npcs, ActionTypes.PLAYERCMD);
    }

    @Override
    public void execute(final Player p, final Action a, final CommandContext args) throws ActionException {
        ActionPlayerCommand ac = (ActionPlayerCommand) a;
        ac.setCommand(args.<String>getOne("command").get());
    }
}