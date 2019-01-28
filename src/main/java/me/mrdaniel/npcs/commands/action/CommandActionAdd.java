package me.mrdaniel.npcs.commands.action;

import com.google.common.collect.Maps;
import me.mrdaniel.npcs.Npcs;
import me.mrdaniel.npcs.catalogtypes.menupages.PageTypes;
import me.mrdaniel.npcs.commands.NpcCommand;
import me.mrdaniel.npcs.data.npc.actions.*;
import me.mrdaniel.npcs.events.NpcEvent;
import me.mrdaniel.npcs.managers.menu.NpcMenu;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import java.util.Map;

public abstract class CommandActionAdd extends NpcCommand {

    public CommandActionAdd(@Nonnull final Npcs npcs) {
        super(npcs, PageTypes.ACTIONS);
    }

    @Override
    public void execute(final Player p, final NpcMenu menu, final CommandContext args) throws CommandException {
        if (Npcs.getGame().getEventManager().post(new NpcEvent.Edit(Npcs.getContainer(), p, menu.getNpc(), menu.getFile()))) {
            throw new CommandException(Text.of(TextColors.RED, "Could not edit Npc: Event was cancelled!"));
        }

        menu.getFile().getActions().add(this.create(args));
        menu.getFile().writeActions();
        menu.getFile().save();
    }

    @Nonnull
    public abstract Action create(@Nonnull final CommandContext args);

    public static class PlayerCommand extends CommandActionAdd {
        public PlayerCommand(@Nonnull final Npcs npcs) {
            super(npcs);
        }

        @Override
        public Action create(final CommandContext args) {
            return new ActionPlayerCommand(args.<String>getOne("command").get());
        }
    }

    public static class ConsoleCommand extends CommandActionAdd {
        public ConsoleCommand(@Nonnull final Npcs npcs) {
            super(npcs);
        }

        @Override
        public Action create(final CommandContext args) {
            return new ActionConsoleCommand(args.<String>getOne("command").get());
        }
    }

    public static class Message extends CommandActionAdd {
        public Message(@Nonnull final Npcs npcs) {
            super(npcs);
        }

        @Override
        public Action create(final CommandContext args) {
            return new ActionMessage(args.<String>getOne("message").get());
        }
    }

    public static class Delay extends CommandActionAdd {
        public Delay(@Nonnull final Npcs npcs) {
            super(npcs);
        }

        @Override
        public Action create(final CommandContext args) {
            return new ActionDelay(args.<Integer>getOne("ticks").get());
        }
    }

    public static class Pause extends CommandActionAdd {
        public Pause(@Nonnull final Npcs npcs) {
            super(npcs);
        }

        @Override
        public Action create(final CommandContext args) {
            return new ActionPause();
        }
    }

    public static class Goto extends CommandActionAdd {
        public Goto(@Nonnull final Npcs npcs) {
            super(npcs);
        }

        @Override
        public Action create(final CommandContext args) {
            return new ActionGoto(args.<Integer>getOne("next").get());
        }
    }

    public static class Choices extends CommandActionAdd {
        public Choices(@Nonnull final Npcs npcs) {
            super(npcs);
        }

        @Override
        public Action create(final CommandContext args) {
            Map<String, Integer> choices = Maps.newHashMap();
            choices.put(args.<String>getOne("first").get(), args.<Integer>getOne("goto_first").get());
            choices.put(args.<String>getOne("second").get(), args.<Integer>getOne("goto_second").get());
            return new ActionChoices(choices);
        }
    }
}