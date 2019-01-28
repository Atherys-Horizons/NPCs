package me.mrdaniel.npcs.commands.main;

import me.mrdaniel.npcs.Npcs;
import me.mrdaniel.npcs.catalogtypes.menupages.PageTypes;
import me.mrdaniel.npcs.commands.PlayerCommand;
import me.mrdaniel.npcs.managers.menu.NpcMenu;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import java.util.Optional;

public class CommandInfo extends PlayerCommand {

    private final Text[] lines;

    public CommandInfo(@Nonnull final Npcs npcs) {
        super(npcs);

        this.lines = new Text[]{
                Text.EMPTY,
                Text.of(TextColors.YELLOW, "---------------=====[ ", TextColors.RED, "Npc Info", TextColors.YELLOW, " ]=====---------------"),
                Text.of(TextColors.AQUA, "You have currently no selected Npc."),
                Text.of(TextColors.AQUA, "You can select an Npc by shift right clicking it."),
                Text.of(TextColors.AQUA, "You can see a list of Npc's by doing: ", TextColors.YELLOW, "/npc list"),
                Text.of(TextColors.AQUA, "You can create an Npc by doing: ", TextColors.YELLOW, "/npc create ", TextColors.GOLD, "[entitytype]"),
                Text.of(TextColors.YELLOW, "--------------------------------------------------")
        };
    }

    @Override
    public void execute(@Nonnull final Player p, @Nonnull final CommandContext args) throws CommandException {
        Optional<NpcMenu> menu = Npcs.getMenuManager().get(p.getUniqueId());
        if (menu.isPresent()) {
            menu.get().send(p, PageTypes.MAIN);
        } else {
            p.sendMessages(this.lines);
        }
    }
}