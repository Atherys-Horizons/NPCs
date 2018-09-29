package me.mrdaniel.npcs.commands.main;

import me.mrdaniel.npcs.NPCs;
import me.mrdaniel.npcs.commands.PlayerCommand;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

import javax.annotation.Nonnull;

public class CommandDeselect extends PlayerCommand {

    public CommandDeselect(@Nonnull final NPCs npcs) {
        super(npcs);
    }

    @Override
    public void execute(final Player p, final CommandContext args) throws CommandException {
        NPCs.getMenuManager().deselect(p.getUniqueId());
    }
}