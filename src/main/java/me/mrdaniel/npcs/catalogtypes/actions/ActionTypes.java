package me.mrdaniel.npcs.catalogtypes.actions;

import com.google.common.collect.Lists;
import me.mrdaniel.npcs.data.npc.actions.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public final class ActionTypes {

    public static final ActionType MESSAGE = new ActionType("Message", "message", ActionMessage.class);
    public static final ActionType CONSOLECMD = new ActionType("Console Command", "consolecmd", ActionConsoleCommand.class);
    public static final ActionType PLAYERCMD = new ActionType("Player Command", "playercmd", ActionPlayerCommand.class);
    public static final ActionType CONDITION = new ActionType("Condition", "condition", ActionCondition.class);
    public static final ActionType CHOICES = new ActionType("Choices", "choices", ActionChoices.class);
    public static final ActionType PAUSE = new ActionType("Pause", "pause", ActionPause.class);
    public static final ActionType GOTO = new ActionType("Goto", "goto", ActionGoto.class);
    public static final ActionType DELAY = new ActionType("Delay", "delay", ActionDelay.class);

    public static final List<ActionType> VALUES = Lists.newArrayList(MESSAGE, CONSOLECMD, PLAYERCMD, CONDITION, CHOICES, PAUSE, GOTO, DELAY);

    @Nullable
    public static Optional<ActionType> of(@Nonnull final String id) {
        for (ActionType type : VALUES) {
            if (type.getId().equalsIgnoreCase(id)) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }
}