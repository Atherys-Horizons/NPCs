package me.mrdaniel.npcs.commands;

import me.mrdaniel.npcs.Npcs;
import me.mrdaniel.npcs.catalogtypes.menupages.PageType;
import me.mrdaniel.npcs.managers.menu.NpcMenu;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;

public abstract class NpcCommand extends PlayerCommand {

    protected final PageType page;

    public NpcCommand(@Nonnull final Npcs npcs, @Nonnull final PageType page) {
        super(npcs);

        this.page = page;
    }

    @Override
    public void execute(final Player p, final CommandContext args) throws CommandException {
        NpcMenu menu = Npcs.getMenuManager().get(p.getUniqueId()).orElseThrow(() -> new CommandException(Text.of(TextColors.RED, "You don't have an Npc selected!")));
        this.execute(p, menu, args);
        menu.updateAndSend(p, this.page);
    }

    public abstract void execute(@Nonnull final Player p, @Nonnull final NpcMenu menu, @Nonnull final CommandContext args) throws CommandException;
}