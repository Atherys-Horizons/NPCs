package me.mrdaniel.npcs.commands.edit;

import me.mrdaniel.npcs.NPCs;
import me.mrdaniel.npcs.catalogtypes.menupages.PageTypes;
import me.mrdaniel.npcs.commands.NPCCommand;
import me.mrdaniel.npcs.events.NPCEvent;
import me.mrdaniel.npcs.managers.menu.NPCMenu;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;

public class CommandGlowColor extends NPCCommand {

    public CommandGlowColor(@Nonnull final NPCs npcs) {
        super(npcs, PageTypes.MAIN);
    }

    @Override
    public void execute(final Player p, final NPCMenu menu, final CommandContext args) throws CommandException {
        if (!menu.getNPC().supports(Keys.GLOWING))
            throw new CommandException(Text.of(TextColors.RED, "You can only use this on NPC's that can glow."));
        if (NPCs.getGame().getEventManager().post(new NPCEvent.Edit(NPCs.getContainer(), p, menu.getNPC(), menu.getFile()))) {
            throw new CommandException(Text.of(TextColors.RED, "Could not edit NPC: Event was cancelled!"));
        }

        TextColor color = args.<TextColor>getOne("color").get();

        NPCs.getGlowColorManager().setGlowColor(menu.getNPC(), menu.getFile().getId(), color);
        menu.getFile().setGlowColor(color);
        menu.getFile().save();
    }
}