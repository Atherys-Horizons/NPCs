package me.mrdaniel.npcs.commands.edit;

import me.mrdaniel.npcs.Npcs;
import me.mrdaniel.npcs.catalogtypes.menupages.PageTypes;
import me.mrdaniel.npcs.commands.NpcCommand;
import me.mrdaniel.npcs.data.npc.NpcData;
import me.mrdaniel.npcs.events.NpcEvent;
import me.mrdaniel.npcs.managers.menu.NpcMenu;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;

public class CommandInteract extends NpcCommand {

    public CommandInteract(@Nonnull final Npcs npcs) {
        super(npcs, PageTypes.MAIN);
    }

    @Override
    public void execute(final Player p, final NpcMenu menu, final CommandContext args) throws CommandException {
        if (Npcs.getGame().getEventManager().post(new NpcEvent.Edit(Npcs.getContainer(), p, menu.getNpc(), menu.getFile()))) {
            throw new CommandException(Text.of(TextColors.RED, "Could not edit Npc: Event was cancelled!"));
        }

        NpcData data = menu.getNpc().get(NpcData.class).get();
        boolean interact = args.<Boolean>getOne("interact").orElse(!data.canInteract());

        data.setInteract(interact);
        menu.getNpc().offer(data);

        menu.getFile().setInteract(interact);
        menu.getFile().save();
    }
}