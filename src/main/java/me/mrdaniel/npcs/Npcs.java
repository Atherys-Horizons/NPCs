package me.mrdaniel.npcs;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import me.mrdaniel.npcs.bstats.MetricsLite;
import me.mrdaniel.npcs.catalogtypes.actions.ActionType;
import me.mrdaniel.npcs.catalogtypes.actions.ActionTypeRegistryModule;
import me.mrdaniel.npcs.catalogtypes.conditions.ConditionType;
import me.mrdaniel.npcs.catalogtypes.conditions.ConditionTypeRegistryModule;
import me.mrdaniel.npcs.commands.action.CommandActionAdd;
import me.mrdaniel.npcs.commands.action.CommandActionRemove;
import me.mrdaniel.npcs.commands.action.CommandActionRepeat;
import me.mrdaniel.npcs.commands.action.CommandActionSwap;
import me.mrdaniel.npcs.commands.action.condition.CommandActionAddCondition;
import me.mrdaniel.npcs.commands.action.edit.*;
import me.mrdaniel.npcs.commands.armor.CommandEquipmentGive;
import me.mrdaniel.npcs.commands.armor.CommandEquipmentRemove;
import me.mrdaniel.npcs.commands.edit.*;
import me.mrdaniel.npcs.commands.main.*;
import me.mrdaniel.npcs.data.npc.ImmutableNpcData;
import me.mrdaniel.npcs.data.npc.NpcData;
import me.mrdaniel.npcs.data.npc.NpcDataBuilder;
import me.mrdaniel.npcs.data.npc.actions.Action;
import me.mrdaniel.npcs.data.npc.actions.ActionTypeSerializer;
import me.mrdaniel.npcs.data.npc.actions.conditions.Condition;
import me.mrdaniel.npcs.data.npc.actions.conditions.ConditionTypeSerializer;
import me.mrdaniel.npcs.io.Config;
import me.mrdaniel.npcs.listeners.WorldListener;
import me.mrdaniel.npcs.managers.*;
import me.mrdaniel.npcs.managers.placeholders.SimplePlaceHolderManager;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.type.*;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

@Plugin(
    id = "npcs",
    name = "NPCs",
    version = "3.0.3-API7",
    authors = {"Daniel12321, A'therys Horizons"},
    url = "https://github.com/Atherys-Horizons/NPCs",
    description = "A plugin that adds simple custom NPC's to your worlds.",
    dependencies = {
        @Dependency(id = "placeholderapi", optional = true),
    }
)
public class Npcs {

    private static Npcs instance;

    private final Path configDir;

    private NpcManager npcmanager;
    private ActionManager actions;
    private MenuManager menus;
    private GlowColorManager glowcolors;
    private PlaceHolderManager placeholders;

    private int startup;

    @Inject
    private Logger logger;

    @Inject
    private PluginContainer container;

    @Inject
    private Game game;

    @Inject
    public Npcs(@ConfigDir(sharedRoot = false) final Path path) {
        this.configDir = path;

        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (final IOException exc) {
                this.logger.error("Failed to create main config directory: {}", exc);
            }
        }
    }

    private void reload() {
        this.logger.info("Reloading Plugin...");

        this.game.getEventManager().unregisterPluginListeners(this);
        this.game.getScheduler().getScheduledTasks(this).forEach(task -> task.cancel());
        this.game.getCommandManager().getOwnedBy(this).forEach(this.game.getCommandManager()::removeMapping);

        this.onInit(null);

        this.game.getServer().getWorlds().forEach(w -> w.getEntities(ent -> ent.get(NpcData.class).isPresent()).forEach(ent -> ent.remove()));
        Task.builder().delayTicks(100).execute(() -> this.game.getServer().getWorlds().forEach(w -> this.npcmanager.load(w))).submit(this);
    }

    @Listener
    public void onPreInit(@Nullable final GamePreInitializationEvent e) {
        DataRegistration.builder().dataClass(NpcData.class).immutableClass(ImmutableNpcData.class).builder(new NpcDataBuilder()).dataName("npc").manipulatorId("npc").buildAndRegister(this.container);
        this.game.getRegistry().registerModule(ActionType.class, new ActionTypeRegistryModule());
        this.game.getRegistry().registerModule(ConditionType.class, new ConditionTypeRegistryModule());

        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(Action.class), new ActionTypeSerializer());
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(Condition.class), new ConditionTypeSerializer());
    }

    @Listener
    public void onInit(@Nullable final GameInitializationEvent e) {
        this.logger.info("Loading plugin...");
        instance = this;

        Config config = new Config(this.configDir.resolve("config.conf"));


        this.npcmanager = new NpcManager(this, this.configDir.resolve("storage"));
        this.actions = new ActionManager(this);
        this.menus = new MenuManager(this, config);
        this.glowcolors = new GlowColorManager(this);
        this.startup = new Random().nextInt(Integer.MAX_VALUE);

        this.placeholders = new SimplePlaceHolderManager(config);

        this.game.getCommandManager().register(this, CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Main Command"))
                .executor(new CommandInfo(this))
                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | List Command")).permission("npc.list").executor(new CommandList(this)).build(), "list")
                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Create Command")).permission("npc.create").arguments(GenericArguments.optional(GenericArguments.catalogedElement(Text.of("type"), EntityType.class))).executor(new CommandCreate(this)).build(), "create")
                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Remove Command")).permission("npc.remove").executor(new CommandRemove(this)).build(), "remove")
                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Copy Command")).permission("npc.copy").executor(new CommandCopy(this)).build(), "copy")
                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Mount Command")).permission("npc.mount").executor(new CommandMount(this)).build(), "mount")
                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Deselect Command")).permission("npc.deselect").executor(new CommandDeselect(this)).build(), "deselect")
                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | GoTo Command")).permission("npc.goto").executor(new CommandGoto(this)).build(), "goto")
                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Move Command")).permission("npc.edit.move").executor(new CommandMove(this)).build(), "move")
                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Name Command")).permission("npc.edit.name").arguments(GenericArguments.remainingJoinedStrings(Text.of("name"))).executor(new CommandName(this)).build(), "name")
                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Look Command")).permission("npc.edit.look").arguments(GenericArguments.optional(GenericArguments.bool(Text.of("look")))).executor(new CommandLook(this)).build(), "look")
                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Interact Command")).permission("npc.edit.interact").arguments(GenericArguments.optional(GenericArguments.bool(Text.of("interact")))).executor(new CommandInteract(this)).build(), "interact")
                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Glow Command")).permission("npc.edit.glow").arguments(GenericArguments.optional(GenericArguments.bool(Text.of("glow")))).executor(new CommandGlow(this)).build(), "glow")
                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | GlowColor Command")).permission("npc.edit.glowcolor").arguments(GenericArguments.catalogedElement(Text.of("color"), TextColor.class)).executor(new CommandGlowColor(this)).build(), "glowcolor")
                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Skin Command")).permission("npc.edit.skin").arguments(GenericArguments.string(Text.of("name"))).executor(new CommandSkin(this)).build(), "skin")
                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Career Command")).permission("npc.edit.career").arguments(GenericArguments.catalogedElement(Text.of("career"), Career.class)).executor(new CommandCareer(this)).build(), "career")
                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Cat Command")).permission("npc.edit.cat").arguments(GenericArguments.catalogedElement(Text.of("cat"), OcelotType.class)).executor(new CommandCat(this)).build(), "cat")
                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Llama Command")).permission("npc.edit.llama").arguments(GenericArguments.catalogedElement(Text.of("variant"), LlamaVariant.class)).executor(new CommandLlama(this)).build(), "llama")
                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Style Command")).permission("npc.edit.style").arguments(GenericArguments.catalogedElement(Text.of("style"), HorseStyle.class)).executor(new CommandStyle(this)).build(), "style")
                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Color Command")).permission("npc.edit.color").arguments(GenericArguments.catalogedElement(Text.of("color"), HorseColor.class)).executor(new CommandColor(this)).build(), "color")
                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Size Command")).permission("npc.edit.size").arguments(GenericArguments.integer(Text.of("size"))).executor(new CommandSize(this)).build(), "size")
                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Sit Command")).permission("npc.edit.sit").arguments(GenericArguments.optional(GenericArguments.bool(Text.of("sit")))).executor(new CommandSit(this)).build(), "sit")
                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Charged Command")).permission("npc.edit.charge").arguments(GenericArguments.optional(GenericArguments.bool(Text.of("charged")))).executor(new CommandCharged(this)).build(), "charged")
                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Anrgy Command")).permission("npc.edit.angry").arguments(GenericArguments.optional(GenericArguments.bool(Text.of("angry")))).executor(new CommandAngry(this)).build(), "angry")
                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "Npc | Helmet Command"))
                        .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Give Helmet Command")).permission("npc.armor.helmet.give").executor(new CommandEquipmentGive.Helmet(this)).build(), "give")
                        .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Remove Helmet Command")).permission("npc.armor.helmet.remove").executor(new CommandEquipmentRemove.Helmet(this)).build(), "remove")
                        .build(), "helmet")
                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "Npc | Chestplate Command"))
                        .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Give Chestplate Command")).permission("npc.armor.chestplate.give").executor(new CommandEquipmentGive.Chestplate(this)).build(), "give")
                        .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Remove Chestplate Command")).permission("npc.armor.chestplate.remove").executor(new CommandEquipmentRemove.Chestplate(this)).build(), "remove")
                        .build(), "chestplate")
                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "Npc | Leggings Command"))
                        .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Give Leggings Command")).permission("npc.armor.leggings.give").executor(new CommandEquipmentGive.Leggings(this)).build(), "give")
                        .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Remove Leggings Command")).permission("npc.armor.leggings.remove").executor(new CommandEquipmentRemove.Leggings(this)).build(), "remove")
                        .build(), "leggings")
                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "Npc | Boots Command"))
                        .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Give Boots Command")).permission("npc.armor.boots.give").executor(new CommandEquipmentGive.Boots(this)).build(), "give")
                        .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Remove Boots Command")).permission("npc.armor.boots.remove").executor(new CommandEquipmentRemove.Boots(this)).build(), "remove")
                        .build(), "boots")
                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "Npc | MainHand Command"))
                        .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Give MainHand Command")).permission("npc.armor.mainhand.give").executor(new CommandEquipmentGive.MainHand(this)).build(), "give")
                        .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Remove MainHand Command")).permission("npc.armor.mainhand.remove").executor(new CommandEquipmentRemove.MainHand(this)).build(), "remove")
                        .build(), "mainhand")
                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "Npc | OffHand Command"))
                        .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Give OffHand Command")).permission("npc.armor.offhand.give").executor(new CommandEquipmentGive.OffHand(this)).build(), "give")
                        .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Remove OffHand Command")).permission("npc.armor.offhand.remove").executor(new CommandEquipmentRemove.OffHand(this)).build(), "remove")
                        .build(), "offhand")
                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Actions Command"))
                        .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Repeat Actions Command")).permission("npc.action.repeat").arguments(GenericArguments.optional(GenericArguments.bool(Text.of("repeat")))).executor(new CommandActionRepeat(this)).build(), "repeat")
                        .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Swap Actions Command")).permission("npc.action.swap").arguments(GenericArguments.integer(Text.of("first")), GenericArguments.integer(Text.of("second"))).executor(new CommandActionSwap(this)).build(), "swap")
                        .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Remove Action Command")).permission("npc.action.remove").arguments(GenericArguments.integer(Text.of("number"))).executor(new CommandActionRemove(this)).build(), "remove")
                        .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Add Action Command"))
                                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Add Player Command Action Command")).permission("npc.action.command.player").arguments(GenericArguments.remainingRawJoinedStrings(Text.of("command"))).executor(new CommandActionAdd.PlayerCommand(this)).build(), "playercmd")
                                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Add Console Command Action Command")).permission("npc.action.command.console").arguments(GenericArguments.remainingRawJoinedStrings(Text.of("command"))).executor(new CommandActionAdd.ConsoleCommand(this)).build(), "consolecmd")
                                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Add Message Action Command")).permission("npc.action.message").arguments(GenericArguments.remainingRawJoinedStrings(Text.of("message"))).executor(new CommandActionAdd.Message(this)).build(), "message")
                                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Add Delay Action Command")).permission("npc.action.delay").arguments(GenericArguments.integer(Text.of("ticks"))).executor(new CommandActionAdd.Delay(this)).build(), "delay")
                                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Add Pause Action Command")).permission("npc.action.pause").executor(new CommandActionAdd.Pause(this)).build(), "pause")
                                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Add Goto Action Command")).permission("npc.action.goto").arguments(GenericArguments.integer(Text.of("next"))).executor(new CommandActionAdd.Goto(this)).build(), "goto")
                                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Add Choices Action Command")).permission("npc.action.choices").arguments(GenericArguments.string(Text.of("first")), GenericArguments.integer(Text.of("goto_first")), GenericArguments.string(Text.of("second")), GenericArguments.integer(Text.of("goto_second"))).executor(new CommandActionAdd.Choices(this)).build(), "choices")
                                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Add Condition Action Command"))
                                        .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Add Item Condition Action Command")).permission("npc.action.condition.item").arguments(GenericArguments.catalogedElement(Text.of("type"), ItemType.class), GenericArguments.integer(Text.of("amount")), GenericArguments.optionalWeak(GenericArguments.string(Text.of("name")))).executor(new CommandActionAddCondition.Item(this)).build(), "item")
                                        .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Add Level Condition Action Command")).permission("npc.action.condition.level").arguments(GenericArguments.integer(Text.of("level"))).executor(new CommandActionAddCondition.Level(this)).build(), "level")
                                        .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Add Exp Condition Action Command")).permission("npc.action.condition.exp").arguments(GenericArguments.integer(Text.of("exp"))).executor(new CommandActionAddCondition.Exp(this)).build(), "exp")
                                        .build(), "condition")
                                .build(), "add")
                        .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Edit Action Command")).arguments(GenericArguments.integer(Text.of("index")))
                                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Set Console Command Command")).permission("npc.action.edit.command.console").arguments(GenericArguments.remainingRawJoinedStrings(Text.of("command"))).executor(new CommandSetConsoleCommand(this)).build(), "consolecmd")
                                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Set Player Command Command")).permission("npc.action.edit.command.player").arguments(GenericArguments.remainingRawJoinedStrings(Text.of("command"))).executor(new CommandSetPlayerCommand(this)).build(), "playercmd")
                                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Set Message Command")).permission("npc.action.edit.message").arguments(GenericArguments.remainingRawJoinedStrings(Text.of("message"))).executor(new CommandSetMessage(this)).build(), "message")
                                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Set Goto Command")).permission("npc.action.edit.goto").arguments(GenericArguments.integer(Text.of("goto"))).executor(new CommandSetGoto(this)).build(), "goto")
                                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Set Delay Command")).permission("npc.action.edit.delay").arguments(GenericArguments.integer(Text.of("ticks"))).executor(new CommandSetDelay(this)).build(), "delay")
                                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Set Take Command")).permission("npc.action.edit.condition.take").arguments(GenericArguments.bool(Text.of("take"))).executor(new CommandSetTake(this)).build(), "take")
                                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Set Goto Failed Command")).permission("npc.action.edit.condition.goto.failed").arguments(GenericArguments.integer(Text.of("goto"))).executor(new CommandSetGotoFailed(this)).build(), "goto_failed")
                                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Set Goto Met Command")).permission("npc.action.edit.condition.goto.met").arguments(GenericArguments.integer(Text.of("goto"))).executor(new CommandSetGotoMet(this)).build(), "goto_met")
                                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Add Choice Command")).permission("npc.action.edit.choice.add").arguments(GenericArguments.string(Text.of("name")), GenericArguments.integer(Text.of("goto"))).executor(new CommandAddChoice(this)).build(), "addchoice", "setchoice")
                                .child(CommandSpec.builder().description(Text.of(TextColors.GOLD, "NPC's | Remove Choice Command")).permission("npc.action.edit.choice.remove").arguments(GenericArguments.string(Text.of("name"))).executor(new CommandRemoveChoice(this)).build(), "removechoice")
                                .build(), "edit")
                        .build(), "action", "actions")
                .build(), "npc", "npcs");

        this.game.getEventManager().registerListeners(this, new WorldListener(this));

        Task.builder().delayTicks(60).intervalTicks(config.getNode("npc_update_ticks").getInt(2)).execute(() -> this.game.getServer().getWorlds().forEach(w -> w.getEntities().stream().filter(ent -> ent.get(NpcData.class).isPresent()).forEach(ent -> ent.get(NpcData.class).get().tick((Living) ent)))).submit(this);

        this.logger.info("Plugin loaded successfully.");
    }

    @Listener
    public void onReload(@Nullable final GameReloadEvent e) {
        reload();
    }

    @Nonnull
    public static Game getGame() {
        return getInstance().game;
    }

    @Nonnull
    public static PluginContainer getContainer() {
        return getInstance().container;
    }

    @Nonnull
    public static Path getConfigDir() {
        return getInstance().configDir;
    }

    @Nonnull
    public static NpcManager getNpcManager() {
        return getInstance().npcmanager;
    }

    @Nonnull
    public static ActionManager getActionManager() {
        return getInstance().actions;
    }

    @Nonnull
    public static MenuManager getMenuManager() {
        return getInstance().menus;
    }

    @Nonnull
    public static GlowColorManager getGlowColorManager() {
        return getInstance().glowcolors;
    }

    @Nonnull
    public static PlaceHolderManager getPlaceHolderManager() {
        return getInstance().placeholders;
    }

    public static int getStartup() {
        return getInstance().startup;
    }

    public static Logger getLogger() {
        return getInstance().logger;
    }

    public static Npcs getInstance() {
        return instance;
    }

    public static void reload(PluginContainer plugin) {
        getLogger().info("Plugin " + plugin.getName() + " reloaded Npcs.");
        getInstance().reload();
    }
}