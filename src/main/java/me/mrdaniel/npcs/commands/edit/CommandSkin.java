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
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;

public class CommandSkin extends NpcCommand {

    public CommandSkin(@Nonnull final Npcs npcs) {
        super(npcs, PageTypes.MAIN);
    }

    @Override
    public void execute(final Player p, final NpcMenu menu, final CommandContext args) throws CommandException {
        if (!menu.getNpc().supports(Keys.SKIN_UNIQUE_ID)) {
            throw new CommandException(Text.of(TextColors.RED, "You can only give human Npc's a skin."));
        }
        if (Npcs.getGame().getEventManager().post(new NpcEvent.Edit(Npcs.getContainer(), p, menu.getNpc(), menu.getFile()))) {
            throw new CommandException(Text.of(TextColors.RED, "Could not edit Npc: Event was cancelled!"));
        }

        String name = args.<String>getOne("name").get();
        menu.getFile().setSkinName(name);

        Npcs.getGame().getServer().getGameProfileManager().get(name).thenAccept(gp -> Task.builder().delayTicks(0).execute(() -> {
            menu.getNpc().offer(Keys.SKIN_UNIQUE_ID, gp.getUniqueId());
            menu.getFile().setSkinUUID(gp.getUniqueId());
            menu.getFile().save();
        }).submit(Npcs.getInstance()));
    }
}