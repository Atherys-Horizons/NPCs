package me.mrdaniel.npcs.managers;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.Maps;
import me.mrdaniel.npcs.NPCs;
import me.mrdaniel.npcs.data.npc.NPCData;
import me.mrdaniel.npcs.events.NPCCreateEvent;
import me.mrdaniel.npcs.exceptions.NPCException;
import me.mrdaniel.npcs.io.NPCFile;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.ArmorEquipable;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class NPCManager {

    private final Path storage_path;

    private final Map<NPCFile, Living> npcs;

    public NPCManager(@Nonnull final NPCs npcs, @Nonnull final Path storage_path) {
        this.storage_path = storage_path;

        this.npcs = Maps.newHashMap();

        if (!Files.exists(this.storage_path)) {
            try {
                Files.createDirectory(this.storage_path);
            } catch (final IOException exc) {
                NPCs.getInstance().getLogger().error("Failed to create main NPC storage directory: {}", exc);
            }
        }

        for (String name : this.storage_path.toFile().list()) {
            this.npcs.put(new NPCFile(this.storage_path, Integer.valueOf(name.replaceAll("[^\\d]", ""))), null);
        }
    }

    public void load(@Nonnull final World world) {
        List<NPCFile> files = this.npcs.keySet().stream().filter(file -> file.getWorldName().equalsIgnoreCase(world.getName())).collect(Collectors.toList());
        files.forEach(file -> {
            world.loadChunk(file.getPosition().toInt().div(16).mul(1, 0, 1), false);

            try {
                this.npcs.put(file, this.spawn(file, world));
            } catch (final NPCException exc) {
                NPCs.getInstance().getLogger().error("Failed to spawn NPC: {}", exc);
            }
        });
    }

    @Nonnull
    public Set<NPCFile> getAll() {
        return this.npcs.keySet();
    }

    @Nonnull
    public Optional<Living> getNPC(@Nonnull final NPCFile file) {
        return Optional.ofNullable(this.npcs.get(file));
    }

    @Nonnull
    public Optional<NPCFile> getFile(final int id) {
        for (NPCFile file : this.npcs.keySet()) {
            if (file.getId() == id) {
                return Optional.of(file);
            }
        }
        return Optional.empty();
    }

    private int getNextId() {
        int highest = 1;
        while (Files.exists(this.storage_path.resolve("npc_" + highest + ".conf"))) {
            highest++;
        }
        return highest;
    }

    public void remove(final int id) throws NPCException {
        NPCFile file = this.getFile(id).orElseThrow(() -> new NPCException("No NPC with that ID is exists!"));
        Living npc = this.npcs.get(file);

        if (npc == null) {
            throw new NPCException("No NPC with that ID is exists!");
        }

        file.delete(this.storage_path);
        npc.remove();
        NPCs.getMenuManager().deselect(file);
    }

    public NPCFile create(@Nonnull final EntityType type, Location<World> location, boolean temporary) throws NPCException {

        NPCFile file = new NPCFile(this.storage_path, this.getNextId());
        file.setType(type);
        file.setLocation(location);
        file.setRotation(Vector3d.FORWARD);
        file.setHead(Vector3d.FORWARD);
        file.setInteract(true);
        file.setLooking(false);
        if (type == EntityTypes.HUMAN) {
            file.setName(Text.of("Steve"));
        }

        Living npc = this.spawn(file, location.getExtent());
        this.npcs.put(file, npc);
        file.save();

        return file;
    }

    @Nonnull
    private Living spawn(@Nonnull final NPCFile file, @Nonnull final World world) throws NPCException {
        Living npc = (Living) world.createEntity(file.getType().orElseThrow(() -> new NPCException("An NPC's EntityType could not be found!")), file.getPosition());

        file.setCache(npc.getUniqueId());

        npc.offer(Keys.PERSISTS, true);
        npc.offer(Keys.AI_ENABLED, false);
        npc.setRotation(file.getRotation());
        npc.setHeadRotation(file.getHead());
        file.getName().ifPresent(name -> {
            npc.offer(Keys.CUSTOM_NAME_VISIBLE, true);
            npc.offer(Keys.DISPLAY_NAME, name);
        });
        file.getSkinUUID().ifPresent(uuid -> npc.offer(Keys.SKIN_UNIQUE_ID, uuid));
        if (file.getGlow()) {
            npc.offer(Keys.GLOWING, true);
            file.getGlowColor().ifPresent(color -> NPCs.getGlowColorManager().setGlowColor(npc, file.getId(), color));
        }
        if (file.getAngry()) {
            npc.offer(Keys.ANGRY, true);
        }
        if (file.getCharged()) {
            npc.offer(Keys.CREEPER_CHARGED, true);
        }
        if (file.getSize() > 0) {
            npc.offer(Keys.SLIME_SIZE, file.getSize());
        }
        file.getCareer().ifPresent(career -> npc.offer(Keys.CAREER, career));
        file.getHorseStyle().ifPresent(style -> npc.offer(Keys.HORSE_STYLE, style));
        file.getHorseColor().ifPresent(color -> npc.offer(Keys.HORSE_COLOR, color));
        file.getVariant().ifPresent(variant -> npc.offer(Keys.LLAMA_VARIANT, variant));
        file.getCat().ifPresent(cat -> npc.offer(Keys.OCELOT_TYPE, cat));

        if (npc instanceof ArmorEquipable) {
            ArmorEquipable ae = (ArmorEquipable) npc;
            file.getHelmet().ifPresent(ae::setHelmet);
            file.getChestplate().ifPresent(ae::setChestplate);
            file.getLeggings().ifPresent(ae::setLeggings);
            file.getBoots().ifPresent(ae::setBoots);
            file.getMainHand().ifPresent(stack -> ae.setItemInHand(HandTypes.MAIN_HAND, stack));
            file.getOffHand().ifPresent(stack -> ae.setItemInHand(HandTypes.OFF_HAND, stack));
        }

        npc.offer(new NPCData(NPCs.getStartup(), file.getId(), file.getLooking(), file.getInteract()));

        world.spawnEntity(npc);
        return npc;
    }

    public void copy(@Nonnull final Player p, @Nonnull final NPCFile file) throws NPCException {
        EntityType type = file.getType().orElseThrow(() -> new NPCException("Invalid EntityType was found!"));
        if (NPCs.getGame().getEventManager().post(new NPCCreateEvent(NPCs.getContainer(), p, file.getType().get()))) {
            throw new NPCException("Event was cancelled!");
        }

        NPCFile copy = new NPCFile(this.storage_path, this.getNextId());
        copy.setType(type);
        copy.setLocation(p.getLocation());
        copy.setHead(file.getHead());
        copy.setInteract(file.getInteract());
        copy.setLooking(file.getLooking());
        copy.setRotation(file.getRotation());

        if (file.getAngry()) {
            copy.setAngry(true);
        }
        if (file.getCharged()) {
            copy.setCharged(true);
        }
        if (file.getGlow()) {
            copy.setGlow(true);
        }
        if (file.getSize() > 0) {
            copy.setSize(file.getSize());
        }
        file.getCareer().ifPresent(copy::setCareer);
        file.getCat().ifPresent(copy::setCat);
        file.getGlowColor().ifPresent(copy::setGlowColor);
        file.getHorseColor().ifPresent(copy::setHorseColor);
        file.getHorseStyle().ifPresent(copy::setHorseStyle);
        file.getName().ifPresent(copy::setName);
        file.getSkinName().ifPresent(copy::setSkinName);
        file.getSkinUUID().ifPresent(copy::setSkinUUID);
        file.getVariant().ifPresent(copy::setVariant);
        for (int i = 0; i < file.getActions().size(); i++) {
            copy.getActions().add(i, file.getActions().get(i));
        }
        file.getCurrent().forEach((uuid, current) -> copy.getCurrent().put(uuid, current));

        Living npc = this.spawn(copy, p.getWorld());
        this.npcs.put(copy, npc);
        NPCs.getMenuManager().select(p, npc, copy);
        file.save();
    }
}