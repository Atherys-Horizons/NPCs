package me.mrdaniel.npcs.commands.edit;

import me.mrdaniel.npcs.Npcs;
import me.mrdaniel.npcs.catalogtypes.menupages.PageTypes;
import me.mrdaniel.npcs.commands.NpcCommand;
import me.mrdaniel.npcs.events.NpcEvent;
import me.mrdaniel.npcs.managers.menu.NpcMenu;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;

public class CommandGlowColor extends NpcCommand {

    public CommandGlowColor(@Nonnull final Npcs npcs) {
        super(npcs, PageTypes.MAIN);
    }

    @Override
    public void execute(final Player p, final NpcMenu menu, final CommandContext args) throws CommandException {
        if (!menu.getNpc().supports(Keys.GLOWING))
            throw new CommandException(Text.of(TextColors.RED, "You can only use this on Npc's that can glow."));
        if (Npcs.getGame().getEventManager().post(new NpcEvent.Edit(Npcs.getContainer(), p, menu.getNpc(), menu.getFile()))) {
            throw new CommandException(Text.of(TextColors.RED, "Could not edit Npc: Event was cancelled!"));
        }

        TextColor color = args.<TextColor>getOne("color").get();

        Npcs.getGlowColorManager().setGlowColor(menu.getNpc(), menu.getFile().getId(), color);
        menu.getFile().setGlowColor(color);
        menu.getFile().save();
    }
}