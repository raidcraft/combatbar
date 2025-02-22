package de.raidcraft.combatbar;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.config.Comment;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.combatbar.listeners.HotbarListener;
import de.raidcraft.combatbar.tables.THotbar;
import de.raidcraft.combatbar.tables.THotbarHolder;
import de.raidcraft.combatbar.tables.THotbarSlot;
import de.raidcraft.combatbar.tables.THotbarSlotData;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class RCHotbarPlugin extends BasePlugin {

    public static final String DEFAULT_HOTBAR = "inventory";

    @Getter
    private LocalConfiguration config;
    @Getter
    private HotbarManager hotbarManager;
    @Getter
    private GraveyardSupportManager graveyardSupportManager;

    @Override
    public void reload() {
        getConfig().reload();
    }

    @Override
    public void enable() {
        config = configure(new LocalConfiguration(this));

        hotbarManager = new HotbarManager(this);
        RaidCraft.registerComponent(HotbarManager.class, hotbarManager);

        registerEvents(new HotbarListener(this));
        if (isRCGraveyardsEnabled()) {
            graveyardSupportManager = new GraveyardSupportManager(this);
        }
    }

    @Override
    public void disable() {

        getServer().getOnlinePlayers().forEach(player -> getHotbarManager().unregisterPlayer(player));
    }

    public boolean canEnableHotbarHolder(Player player) {
        return !isRCGraveyardsEnabled() || (graveyardSupportManager != null && graveyardSupportManager.isPlayerAlive(player));
    }

    private boolean isRCGraveyardsEnabled() {
        Plugin rcGraveyards = Bukkit.getPluginManager().getPlugin("RCGraveyards");
        return rcGraveyards != null && rcGraveyards.isEnabled();
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        ArrayList<Class<?>> classes = new ArrayList<>();
        classes.add(THotbarHolder.class);
        classes.add(THotbar.class);
        classes.add(THotbarSlot.class);
        classes.add(THotbarSlotData.class);
        return classes;
    }

    public static class LocalConfiguration extends ConfigurationBase<RCHotbarPlugin> {

        @Setting("add-default-hotbar")
        @Comment("Adds a default hotbar to the player if the player has no configured hotbar.")
        public boolean addDefaultHotbar = false;
        @Setting("use-menu-slot")
        @Comment("Places a menu item in the last hotbar slot.")
        public boolean useMenuSlot = false;
        @Setting("allow-hotbar-swapping")
        @Comment("Set to true if you want to allow players to cycle hotbars by holding shift")
        public boolean allowHotbarSwapping = false;

        public LocalConfiguration(RCHotbarPlugin plugin) {

            super(plugin, "config.yml");
        }
    }
}
