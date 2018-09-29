package me.mrdaniel.npcs.commands.edit;

import me.mrdaniel.npcs.NPCs;
import me.mrdaniel.npcs.catalogtypes.menupages.PageTypes;
import me.mrdaniel.npcs.commands.NPCCommand;
import me.mrdaniel.npcs.data.npc.NPCData;
import me.mrdaniel.npcs.events.NPCEvent;
import me.mrdaniel.npcs.managers.menu.NPCMenu;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;

public class CommandInteract extends NPCCommand {

    public CommandInteract(@Nonnull final NPCs npcs) {
        super(npcs, PageTypes.MAIN);
    }

    @Override
    public void execute(final Player p, final NPCMenu menu, final CommandContext args) throws CommandException {
        if (NPCs.getGame().getEventManager().post(new NPCEvent.Edit(NPCs.getContainer(), p, menu.getNPC(), menu.getFile()))) {
            throw new CommandException(Text.of(TextColors.RED, "Could not edit NPC: Event was cancelled!"));
        }

        NPCData data = menu.getNPC().get(NPCData.class).get();
        boolean interact = args.<Boolean>getOne("interact").orElse(!data.canInteract());

        data.setInteract(interact);
        menu.getNPC().offer(data);

        menu.getFile().setInteract(interact);
        menu.getFile().save();
    }
}