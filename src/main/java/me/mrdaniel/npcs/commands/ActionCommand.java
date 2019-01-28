package me.mrdaniel.npcs.commands;

import me.mrdaniel.npcs.Npcs;
import me.mrdaniel.npcs.catalogtypes.actions.ActionType;
import me.mrdaniel.npcs.catalogtypes.menupages.PageTypes;
import me.mrdaniel.npcs.data.npc.actions.Action;
import me.mrdaniel.npcs.exceptions.ActionException;
import me.mrdaniel.npcs.managers.menu.NpcMenu;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;

public abstract class ActionCommand extends NpcCommand {

    protected final ActionType type;

    public ActionCommand(@Nonnull final Npcs npcs, @Nonnull final ActionType type) {
        super(npcs, PageTypes.ACTIONS);

        this.type = type;
    }

    @Override
    public void execute(final Player p, final NpcMenu menu, final CommandContext args) throws CommandException {
        Action a = menu.getFile().getActions().get(args.<Integer>getOne("index").get());
        if (a.getType() != this.type) {
            throw new CommandException(Text.of(TextColors.RED, "This action does not match the required action for this command!"));
        }

        try {
            this.execute(p, a, args);
            menu.getFile().writeActions();
            menu.getFile().save();
        } catch (final ActionException exc) {
            throw new CommandException(Text.of(TextColors.RED, "Failed to edit action!"), exc);
        }
    }

    public abstract void execute(@Nonnull final Player p, @Nonnull final Action a, @Nonnull final CommandContext args) throws ActionException;
}