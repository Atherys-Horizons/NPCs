package me.mrdaniel.npcs.commands.action.edit;

import me.mrdaniel.npcs.Npcs;
import me.mrdaniel.npcs.catalogtypes.actions.ActionTypes;
import me.mrdaniel.npcs.commands.ActionCommand;
import me.mrdaniel.npcs.data.npc.actions.Action;
import me.mrdaniel.npcs.data.npc.actions.ActionCondition;
import me.mrdaniel.npcs.exceptions.ActionException;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

import javax.annotation.Nonnull;

public class CommandSetGotoMet extends ActionCommand {

    public CommandSetGotoMet(@Nonnull final Npcs npcs) {
        super(npcs, ActionTypes.CONDITION);
    }

    @Override
    public void execute(final Player p, final Action a, final CommandContext args) throws ActionException {
        ActionCondition ac = (ActionCondition) a;
        ac.setGotoMet(args.<Integer>getOne("goto").get());
    }
}