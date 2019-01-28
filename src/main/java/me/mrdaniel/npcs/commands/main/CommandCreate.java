package me.mrdaniel.npcs.commands.main;

import me.mrdaniel.npcs.Npcs;
import me.mrdaniel.npcs.commands.PlayerCommand;
import me.mrdaniel.npcs.events.NpcCreateEvent;
import me.mrdaniel.npcs.exceptions.NpcException;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;

public class CommandCreate extends PlayerCommand {

    public CommandCreate(@Nonnull final Npcs npcs) {
        super(npcs);
    }

    @Override
    public void execute(final Player p, final CommandContext args) throws CommandException {
        EntityType type = args.<EntityType>getOne("type").orElse(EntityTypes.HUMAN);

        if (Npcs.getGame().getEventManager().post(new NpcCreateEvent(Npcs.getContainer(), p, type))) {
            throw new CommandException(Text.of(TextColors.RED, "Could not create Npc: Event was cancelled"));
        }

        try {
            Npcs.getNpcManager().create(type, p.getLocation());
        } catch (final NpcException exc) {
            throw new CommandException(Text.of(TextColors.RED, "Failed to create Npc: {}"), exc);
        }
    }
}