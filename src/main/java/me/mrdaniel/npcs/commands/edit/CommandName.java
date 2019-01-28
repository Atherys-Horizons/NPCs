package me.mrdaniel.npcs.commands.edit;

import me.mrdaniel.npcs.Npcs;
import me.mrdaniel.npcs.catalogtypes.menupages.PageTypes;
import me.mrdaniel.npcs.commands.NpcCommand;
import me.mrdaniel.npcs.events.NpcEvent;
import me.mrdaniel.npcs.managers.menu.NpcMenu;
import me.mrdaniel.npcs.utils.TextUtils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;

public class CommandName extends NpcCommand {

    public CommandName(@Nonnull final Npcs npcs) {
        super(npcs, PageTypes.MAIN);
    }

    @Override
    public void execute(final Player p, final NpcMenu menu, final CommandContext args) throws CommandException {
        if (Npcs.getGame().getEventManager().post(new NpcEvent.Edit(Npcs.getContainer(), p, menu.getNpc(), menu.getFile()))) {
            throw new CommandException(Text.of(TextColors.RED, "Could not edit Npc: Event was cancelled!"));
        }

        Text name = TextUtils.toText(args.<String>getOne("name").get());

        menu.getNpc().offer(Keys.CUSTOM_NAME_VISIBLE, true);
        menu.getNpc().offer(Keys.DISPLAY_NAME, name);

        menu.getFile().setName(name);
        menu.getFile().save();
    }
}