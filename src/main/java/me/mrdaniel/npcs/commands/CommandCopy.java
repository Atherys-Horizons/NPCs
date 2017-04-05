package me.mrdaniel.npcs.commands;

import javax.annotation.Nonnull;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.ArmorEquipable;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import me.mrdaniel.npcs.NPCs;
import me.mrdaniel.npcs.data.npc.NPCData;
import me.mrdaniel.npcs.event.NPCEvent;
import me.mrdaniel.npcs.utils.ServerUtils;

public class CommandCopy extends NPCCommand {

	public CommandCopy(@Nonnull final NPCs npcs) {
		super(npcs);
	}

	@Override
	public void execute(final Player player, final Living npc, final CommandContext args) throws CommandException {
		if (super.getGame().getEventManager().post(new NPCEvent.Create(super.getContainer(), player, npc.getType()))) {
			throw new CommandException(Text.of(TextColors.RED, "Could not edit NPC: Event was cancelled!"));
		}
		Living copy = (Living) player.getWorld().createEntity(npc.getType(), player.getLocation().getPosition());
		copy.offer(npc.get(NPCData.class).orElse(new NPCData()));
		copy.setRotation(npc.getRotation());
		copy.setHeadRotation(npc.getHeadRotation());
		copy.offer(Keys.AI_ENABLED, false);

		npc.get(Keys.SKIN_UNIQUE_ID).ifPresent(value -> copy.offer(Keys.SKIN_UNIQUE_ID, value));
		npc.get(Keys.CUSTOM_NAME_VISIBLE).ifPresent(value -> copy.offer(Keys.CUSTOM_NAME_VISIBLE, value));
		npc.get(Keys.DISPLAY_NAME).ifPresent(value -> copy.offer(Keys.DISPLAY_NAME, value));
		npc.get(Keys.OCELOT_TYPE).ifPresent(value -> copy.offer(Keys.OCELOT_TYPE, value));
		npc.get(Keys.LLAMA_VARIANT).ifPresent(value -> copy.offer(Keys.LLAMA_VARIANT, value));
		npc.get(Keys.HORSE_STYLE).ifPresent(value -> copy.offer(Keys.HORSE_STYLE, value));
		npc.get(Keys.HORSE_COLOR).ifPresent(value -> copy.offer(Keys.HORSE_COLOR, value));
		npc.get(Keys.LLAMA_VARIANT).ifPresent(value -> copy.offer(Keys.LLAMA_VARIANT, value));
		npc.get(Keys.CAREER).ifPresent(value -> copy.offer(Keys.CAREER, value));
		npc.get(Keys.SLIME_SIZE).ifPresent(value -> copy.offer(Keys.SLIME_SIZE, value));
		npc.get(Keys.GLOWING).ifPresent(value -> copy.offer(Keys.GLOWING, value));
		npc.get(Keys.IS_SITTING).ifPresent(value -> copy.offer(Keys.IS_SITTING, value));
		npc.get(Keys.CREEPER_CHARGED).ifPresent(value -> copy.offer(Keys.CREEPER_CHARGED, value));

		if (copy instanceof ArmorEquipable) {
			ArmorEquipable from = (ArmorEquipable) npc;
			ArmorEquipable to = (ArmorEquipable) copy;
			to.setHelmet(from.getHelmet().orElse(null));
			to.setChestplate(from.getChestplate().orElse(null));
			to.setLeggings(from.getLeggings().orElse(null));
			to.setBoots(from.getBoots().orElse(null));
			to.setItemInHand(HandTypes.MAIN_HAND, from.getItemInHand(HandTypes.MAIN_HAND).orElse(null));
			to.setItemInHand(HandTypes.OFF_HAND, from.getItemInHand(HandTypes.OFF_HAND).orElse(null));
		}
		player.getWorld().spawnEntity(copy, ServerUtils.getSpawnCause(copy));
	}
}