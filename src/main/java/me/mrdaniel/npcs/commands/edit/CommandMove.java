package me.mrdaniel.npcs.commands.edit;

import me.mrdaniel.npcs.NPCs;
import me.mrdaniel.npcs.catalogtypes.menupages.PageTypes;
import me.mrdaniel.npcs.commands.NPCCommand;
import me.mrdaniel.npcs.events.NPCEvent;
import me.mrdaniel.npcs.managers.menu.NPCMenu;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;

public class CommandMove extends NPCCommand {

    public CommandMove(@Nonnull final NPCs npcs) {
        super(npcs, PageTypes.MAIN);
    }

    @Override
    public void execute(final Player p, final NPCMenu menu, final CommandContext args) throws CommandException {
        if (NPCs.getGame().getEventManager().post(new NPCEvent.Edit(NPCs.getContainer(), p, menu.getNPC(), menu.getFile()))) {
            throw new CommandException(Text.of(TextColors.RED, "Could not edit NPC: Event was cancelled!"));
        }

        menu.getNPC().setLocation(p.getLocation());
        menu.getNPC().setRotation(p.getRotation());
        menu.getNPC().setHeadRotation(p.getHeadRotation());

        menu.getFile().setLocation(p.getLocation());
        menu.getFile().setRotation(p.getRotation());
        menu.getFile().setHead(p.getHeadRotation());
        menu.getFile().save();
    }
}