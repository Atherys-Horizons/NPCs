package me.mrdaniel.npcs.commands.main;

import me.mrdaniel.npcs.Npcs;
import me.mrdaniel.npcs.catalogtypes.menupages.PageTypes;
import me.mrdaniel.npcs.commands.NpcCommand;
import me.mrdaniel.npcs.exceptions.NpcException;
import me.mrdaniel.npcs.managers.menu.NpcMenu;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;

public class CommandRemove extends NpcCommand {

    public CommandRemove(@Nonnull final Npcs npcs) {
        super(npcs, PageTypes.MAIN);
    }

    @Override
    public void execute(final Player p, final NpcMenu menu, final CommandContext args) throws CommandException {
        try {
            Npcs.getNpcManager().remove(menu.getFile().getId());
        } catch (final NpcException exc) {
            throw new CommandException(Text.of(TextColors.RED, "Failed to remove Npc: {}", exc));
        }
    }
}