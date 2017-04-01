package me.mrdaniel.npcs.commands;

import java.util.Map;

import javax.annotation.Nonnull;

import org.spongepowered.api.data.type.Career;
import org.spongepowered.api.data.type.OcelotType;
import org.spongepowered.api.data.type.SkeletonType;
import org.spongepowered.api.data.type.ZombieType;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;

import com.google.common.collect.Maps;

import me.mrdaniel.npcs.NPCs;

public class ChoiceMaps {

	private final Map<String, EntityType> npcs;
	private final Map<String, OcelotType> cats;
	private final Map<String, Career> careers;
	private final Map<String, ZombieType> zombies;
	private final Map<String, SkeletonType> skeletons;

	public ChoiceMaps(@Nonnull final NPCs npcs) {
		this.npcs = Maps.newHashMap();
		this.cats = Maps.newHashMap();
		this.careers = Maps.newHashMap();
		this.zombies = Maps.newHashMap();
		this.skeletons = Maps.newHashMap();

		this.npcs.put("bat", EntityTypes.BAT);
		this.npcs.put("blaze", EntityTypes.BLAZE);
		this.npcs.put("cavespider", EntityTypes.CAVE_SPIDER);
		this.npcs.put("chicken", EntityTypes.CHICKEN);
		this.npcs.put("cow", EntityTypes.COW);
		this.npcs.put("creeper", EntityTypes.CREEPER);
		this.npcs.put("enderdragon", EntityTypes.ENDER_DRAGON);
		this.npcs.put("enderman", EntityTypes.ENDERMAN);
		this.npcs.put("endermite", EntityTypes.ENDERMITE);
		this.npcs.put("ghast", EntityTypes.GHAST);
		this.npcs.put("giant", EntityTypes.GIANT);
		this.npcs.put("guardian", EntityTypes.GUARDIAN);
		this.npcs.put("horse", EntityTypes.HORSE);
		this.npcs.put("human", EntityTypes.HUMAN);
		this.npcs.put("irongolem", EntityTypes.IRON_GOLEM);
		this.npcs.put("magmacube", EntityTypes.MAGMA_CUBE);
		this.npcs.put("mushroomcow", EntityTypes.MUSHROOM_COW);
		this.npcs.put("ocelot", EntityTypes.OCELOT);
		this.npcs.put("pig", EntityTypes.PIG);
		this.npcs.put("pigzombie", EntityTypes.PIG_ZOMBIE);
		this.npcs.put("polarbear", EntityTypes.POLAR_BEAR);
		this.npcs.put("rabbit", EntityTypes.RABBIT);
		this.npcs.put("sheep", EntityTypes.SHEEP);
		this.npcs.put("shulker", EntityTypes.SHULKER);
		this.npcs.put("silverfish", EntityTypes.SILVERFISH);
		this.npcs.put("skeleton", EntityTypes.SKELETON);
		this.npcs.put("slime", EntityTypes.SLIME);
		this.npcs.put("snowman", EntityTypes.SNOWMAN);
		this.npcs.put("spider", EntityTypes.SPIDER);
		this.npcs.put("squid", EntityTypes.SQUID);
		this.npcs.put("villager", EntityTypes.VILLAGER);
		this.npcs.put("witch", EntityTypes.WITCH);
		this.npcs.put("wither", EntityTypes.WITHER);
		this.npcs.put("wolf", EntityTypes.WOLF);
		this.npcs.put("zombie", EntityTypes.ZOMBIE);

		npcs.getGame().getRegistry().getAllOf(OcelotType.class).forEach(ocelot -> this.cats.put(ocelot.getId().toLowerCase().replace("minecraft:", "").replace("ocelot", "").replace("cat", "").replace("_", ""), ocelot));
		npcs.getGame().getRegistry().getAllOf(Career.class).forEach(career -> this.careers.put(career.getId().toLowerCase().replace("minecraft:", "").replace("_", ""), career));
		npcs.getGame().getRegistry().getAllOf(ZombieType.class).forEach(zombie -> this.zombies.put(zombie.getId().toLowerCase().replace("minecraft:",  ""), zombie));
		npcs.getGame().getRegistry().getAllOf(SkeletonType.class).forEach(skeleton -> this.skeletons.put(skeleton.getId().toLowerCase().replace("minecraft:",  ""), skeleton));
	}

	@Nonnull
	public Map<String, EntityType> getEntities() {
		return this.npcs;
	}

	@Nonnull
	public Map<String, OcelotType> getCats() {
		return this.cats;
	}

	@Nonnull
	public Map<String, Career> getCareers() {
		return this.careers;
	}

	@Nonnull
	public Map<String, ZombieType> getZombies() {
		return this.zombies;
	}

	@Nonnull
	public Map<String, SkeletonType> getSkeletons() {
		return this.skeletons;
	}
}