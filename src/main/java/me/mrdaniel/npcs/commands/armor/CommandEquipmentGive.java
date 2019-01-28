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
import javax.annotation.Nullable;

public abstract class CommandEquipmentGive extends NpcCommand {

    public CommandEquipmentGive(@Nonnull final Npcs npcs) {
        super(npcs, PageTypes.ARMOR);
    }

    @Override
    public void execute(final Player p, final NpcMenu menu, final CommandContext args) throws CommandException {
        if (!(menu.getNpc() instanceof ArmorEquipable)) {
            throw new CommandException(Text.of(TextColors.RED, "The selected Npc can not wear armor!"));
        }
        ArmorEquipable ae = (ArmorEquipable) menu.getNpc();

        ItemStack hand = p.getItemInHand(HandTypes.MAIN_HAND).orElseThrow(() -> new CommandException(Text.of(TextColors.RED, "You must be holding an item.")));

        if (Npcs.getGame().getEventManager().post(new NpcEvent.Edit(Npcs.getContainer(), p, menu.getNpc(), menu.getFile()))) {
            throw new CommandException(Text.of(TextColors.RED, "Could not edit Npc: Event was cancelled!"));
        }

        this.set(ae, hand);
        this.set(menu.getFile(), hand);
        menu.getFile().save();
    }

    public abstract void set(@Nonnull final ArmorEquipable ae, @Nullable final ItemStack stack);

    public abstract void set(@Nonnull final NpcFile file, @Nullable final ItemStack stack);

    public static class Helmet extends CommandEquipmentGive {
        public Helmet(@Nonnull final Npcs npcs) {
            super(npcs);
        }

        @Override
        public void set(final ArmorEquipable ae, final ItemStack stack) {
            ae.setHelmet(stack);
        }

        @Override
        public void set(NpcFile file, ItemStack stack) {
            file.setHelmet(stack);
        }
    }

    public static class Chestplate extends CommandEquipmentGive {
        public Chestplate(@Nonnull final Npcs npcs) {
            super(npcs);
        }

        @Override
        public void set(final ArmorEquipable ae, final ItemStack stack) {
            ae.setChestplate(stack);
        }

        @Override
        public void set(NpcFile file, ItemStack stack) {
            file.setChestplate(stack);
        }
    }

    public static class Leggings extends CommandEquipmentGive {
        public Leggings(@Nonnull final Npcs npcs) {
            super(npcs);
        }

        @Override
        public void set(final ArmorEquipable ae, final ItemStack stack) {
            ae.setLeggings(stack);
        }

        @Override
        public void set(NpcFile file, ItemStack stack) {
            file.setLeggings(stack);
        }
    }

    public static class Boots extends CommandEquipmentGive {
        public Boots(@Nonnull final Npcs npcs) {
            super(npcs);
        }

        @Override
        public void set(final ArmorEquipable ae, final ItemStack stack) {
            ae.setBoots(stack);
        }

        @Override
        public void set(NpcFile file, ItemStack stack) {
            file.setBoots(stack);
        }
    }

    public static class MainHand extends CommandEquipmentGive {
        public MainHand(@Nonnull final Npcs npcs) {
            super(npcs);
        }

        @Override
        public void set(final ArmorEquipable ae, final ItemStack stack) {
            ae.setItemInHand(HandTypes.MAIN_HAND, stack);
        }

        @Override
        public void set(NpcFile file, ItemStack stack) {
            file.setMainHand(stack);
        }
    }

    public static class OffHand extends CommandEquipmentGive {
        public OffHand(@Nonnull final Npcs npcs) {
            super(npcs);
        }

        @Override
        public void set(final ArmorEquipable ae, final ItemStack stack) {
            ae.setItemInHand(HandTypes.OFF_HAND, stack);
        }

        @Override
        public void set(NpcFile file, ItemStack stack) {
            file.setOffHand(stack);
        }
    }
}