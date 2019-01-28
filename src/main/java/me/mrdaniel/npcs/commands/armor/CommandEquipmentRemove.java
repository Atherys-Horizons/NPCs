package me.mrdaniel.npcs.commands.armor;

import me.mrdaniel.npcs.Npcs;
import me.mrdaniel.npcs.catalogtypes.menupages.PageTypes;
import me.mrdaniel.npcs.commands.NpcCommand;
import me.mrdaniel.npcs.events.NpcEvent;
import me.mrdaniel.npcs.io.NpcFile;
import me.mrdaniel.npcs.managers.menu.NpcMenu;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.ArmorEquipable;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import java.util.Optional;

public abstract class CommandEquipmentRemove extends NpcCommand {

    public CommandEquipmentRemove(@Nonnull final Npcs npcs) {
        super(npcs, PageTypes.ARMOR);
    }

    @Override
    public void execute(final Player p, final NpcMenu menu, final CommandContext args) throws CommandException {
        if (!(menu.getNpc() instanceof ArmorEquipable)) {
            throw new CommandException(Text.of(TextColors.RED, "The selected Npc can not wear armor!"));
        }
        ArmorEquipable ae = (ArmorEquipable) menu.getNpc();

        this.get(ae).orElseThrow(() -> new CommandException(Text.of(TextColors.RED, "The selected Npc does not have that piece of equipment.")));

        if (Npcs.getGame().getEventManager().post(new NpcEvent.Edit(Npcs.getContainer(), p, menu.getNpc(), menu.getFile()))) {
            throw new CommandException(Text.of(TextColors.RED, "Could not edit Npc: Event was cancelled!"));
        }

        this.clear(ae);
        this.clear(menu.getFile());
    }

    public abstract Optional<ItemStack> get(@Nonnull final ArmorEquipable ae);

    public abstract void clear(@Nonnull final ArmorEquipable ae);

    public abstract void clear(@Nonnull final NpcFile file);

    public static class Helmet extends CommandEquipmentRemove {
        public Helmet(@Nonnull final Npcs npcs) {
            super(npcs);
        }

        @Override
        public Optional<ItemStack> get(final ArmorEquipable ae) {
            return ae.getHelmet();
        }

        @Override
        public void clear(final ArmorEquipable ae) {
            ae.setHelmet(null);
        }

        @Override
        public void clear(final NpcFile file) {
            file.setHelmet(null);
        }
    }

    public static class Chestplate extends CommandEquipmentRemove {
        public Chestplate(@Nonnull final Npcs npcs) {
            super(npcs);
        }

        @Override
        public Optional<ItemStack> get(final ArmorEquipable ae) {
            return ae.getChestplate();
        }

        @Override
        public void clear(final ArmorEquipable ae) {
            ae.setChestplate(null);
        }

        @Override
        public void clear(final NpcFile file) {
            file.setChestplate(null);
        }
    }

    public static class Leggings extends CommandEquipmentRemove {
        public Leggings(@Nonnull final Npcs npcs) {
            super(npcs);
        }

        @Override
        public Optional<ItemStack> get(final ArmorEquipable ae) {
            return ae.getLeggings();
        }

        @Override
        public void clear(final ArmorEquipable ae) {
            ae.setLeggings(null);
        }

        @Override
        public void clear(final NpcFile file) {
            file.setLeggings(null);
        }
    }

    public static class Boots extends CommandEquipmentRemove {
        public Boots(@Nonnull final Npcs npcs) {
            super(npcs);
        }

        @Override
        public Optional<ItemStack> get(final ArmorEquipable ae) {
            return ae.getBoots();
        }

        @Override
        public void clear(final ArmorEquipable ae) {
            ae.setBoots(null);
        }

        @Override
        public void clear(final NpcFile file) {
            file.setBoots(null);
        }
    }

    public static class MainHand extends CommandEquipmentRemove {
        public MainHand(@Nonnull final Npcs npcs) {
            super(npcs);
        }

        @Override
        public Optional<ItemStack> get(final ArmorEquipable ae) {
            return ae.getItemInHand(HandTypes.MAIN_HAND);
        }

        @Override
        public void clear(final ArmorEquipable ae) {
            ae.setItemInHand(HandTypes.MAIN_HAND, null);
        }

        @Override
        public void clear(final NpcFile file) {
            file.setMainHand(null);
        }
    }

    public static class OffHand extends CommandEquipmentRemove {
        public OffHand(@Nonnull final Npcs npcs) {
            super(npcs);
        }

        @Override
        public Optional<ItemStack> get(final ArmorEquipable ae) {
            return ae.getItemInHand(HandTypes.OFF_HAND);
        }

        @Override
        public void clear(final ArmorEquipable ae) {
            ae.setItemInHand(HandTypes.OFF_HAND, null);
        }

        @Override
        public void clear(final NpcFile file) {
            file.setOffHand(null);
        }
    }
}