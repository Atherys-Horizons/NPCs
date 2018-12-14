package me.mrdaniel.npcs.commands.main;

import me.mrdaniel.npcs.NPCs;
import me.mrdaniel.npcs.catalogtypes.menupages.PageTypes;
import me.mrdaniel.npcs.commands.NPCCommand;
import me.mrdaniel.npcs.exceptions.NPCException;
import me.mrdaniel.npcs.managers.menu.NPCMenu;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;

public class CommandCopy extends NPCCommand {

    public CommandCopy(@Nonnull final NPCs npcs) {
        super(npcs, PageTypes.MAIN);
    }

    @Override
    public void execute(final Player p, final NPCMenu menu, final CommandContext args) throws CommandException {
        try {
            NPCs.getNPCManager().create(menu.getFile().getType().orElseThrow(() -> new CommandException(Text.of(TextColors.RED, ""))), p.getLocation());
        } catch (final NPCException exc) {
            throw new CommandException(Text.of(TextColors.RED, "Failed to copy NPC: {}", exc));
        }
    }
}