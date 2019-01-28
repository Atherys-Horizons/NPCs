package me.mrdaniel.npcs.managers;

import com.google.common.collect.Maps;
import me.mrdaniel.npcs.Npcs;
import me.mrdaniel.npcs.catalogtypes.menupages.PageTypes;
import me.mrdaniel.npcs.data.npc.NpcData;
import me.mrdaniel.npcs.exceptions.NpcException;
import me.mrdaniel.npcs.io.Config;
import me.mrdaniel.npcs.io.NpcFile;
import me.mrdaniel.npcs.managers.menu.NpcMenu;
import me.mrdaniel.npcs.utils.TextUtils;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MenuManager {

    private final Map<UUID, NpcMenu> menus;
    private final Text select_message;
    private final boolean open_menu;

    public MenuManager(@Nonnull final Npcs npcs, @Nonnull final Config config) {
        this.menus = Maps.newHashMap();
        this.select_message = TextUtils.toText(config.getNode("npc_select_message").getString("&eYou selected an Npc."));
        this.open_menu = config.getNode("open_menu_on_select").getBoolean(true);
    }

    public void select(@Nonnull final Player p, @Nonnull final NpcFile file) throws NpcException {
        Living npc = Npcs.getNpcManager().getNpc(file).orElseThrow(() -> new NpcException("Failed to select Npc: Npc hasn't been spawned."));
        this.select(p, npc, file);
    }

    public void select(@Nonnull final Player p, @Nonnull final Living npc) throws NpcException {
        NpcData data = npc.get(NpcData.class).orElseThrow(() -> new NpcException("This Entity is not an Npc!"));
        NpcFile file = Npcs.getNpcManager().getFile(data.getId()).orElseThrow(() -> new NpcException("No Npc file with this id was found!"));
        this.select(p, npc, file);
    }

    public void select(@Nonnull final Player p, @Nonnull final Living npc, @Nonnull final NpcFile file) {
        NpcMenu menu = new NpcMenu(npc, file);
        this.menus.put(p.getUniqueId(), menu);

        if (this.open_menu) {
            menu.send(p, PageTypes.MAIN);
        } else {
            p.sendMessage(ChatTypes.ACTION_BAR, Text.of(TextColors.DARK_GRAY, "[", TextColors.GOLD, "Npcs", TextColors.DARK_GRAY, "] ", this.select_message));
        }
    }

    public void deselect(@Nonnull final UUID uuid) {
        this.menus.remove(uuid);
    }

    public void deselect(@Nonnull final NpcFile file) {
        for (final UUID uuid : this.menus.keySet()) {
            if (this.menus.get(uuid).getFile() == file) {
                this.deselect(uuid);
                return;
            }
        }
    }

    @Nonnull
    public Optional<NpcMenu> get(@Nonnull final UUID uuid) {
        return Optional.ofNullable(this.menus.get(uuid));
    }
}