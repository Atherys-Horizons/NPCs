package me.mrdaniel.npcs.commands.action;

import me.mrdaniel.npcs.Npcs;
import me.mrdaniel.npcs.catalogtypes.menupages.PageTypes;
import me.mrdaniel.npcs.commands.NpcCommand;
import me.mrdaniel.npcs.events.NpcEvent;
import me.mrdaniel.npcs.managers.menu.NpcMenu;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;

public class CommandActionSwap extends NpcCommand {

    public CommandActionSwap(@Nonnull final Npcs npcs) {
        super(npcs, PageTypes.ACTIONS);
    }

    @Override
    public void execute(final Player p, final NpcMenu menu, final CommandContext args) throws CommandException {
        if (Npcs.getGame().getEventManager().post(new NpcEvent.Edit(Npcs.getContainer(), p, menu.getNpc(), menu.getFile()))) {
            throw new CommandException(Text.of(TextColors.RED, "Could not edit Npc: Event was cancelled!"));
        }

        int first = args.<Integer>getOne("first").get();
        int second = args.<Integer>getOne("second").get();
        int size = menu.getFile().getActions().size();

        if (first < 0 || second < 0 || first >= size || second >= size) {
            throw new CommandException(Text.of(TextColors.RED, "No Action was found for one of the two numbers."));
        }

        menu.getFile().getActions().set(first, menu.getFile().getActions().set(second, menu.getFile().getActions().get(first)));
        menu.getFile().writeActions();
        menu.getFile().save();
    }
}