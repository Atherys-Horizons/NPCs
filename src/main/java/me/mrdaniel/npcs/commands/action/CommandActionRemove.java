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

public class CommandActionRemove extends NpcCommand {

    public CommandActionRemove(@Nonnull final Npcs npcs) {
        super(npcs, PageTypes.ACTIONS);
    }

    @Override
    public void execute(final Player p, final NpcMenu menu, final CommandContext args) throws CommandException {
        if (Npcs.getGame().getEventManager().post(new NpcEvent.Edit(Npcs.getContainer(), p, menu.getNpc(), menu.getFile()))) {
            throw new CommandException(Text.of(TextColors.RED, "Could not edit Npc: Event was cancelled!"));
        }

        int id = args.<Integer>getOne("number").get();
        if (id < 0 || id >= menu.getFile().getActions().size()) {
            throw new CommandException(Text.of(TextColors.RED, "No Action with this number exists."));
        }

        menu.getFile().getActions().remove(id);
        menu.getFile().writeActions();
        menu.getFile().save();
    }
}