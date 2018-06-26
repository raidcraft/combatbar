package de.raidcraft.combatbar;

import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.config.Comment;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.combatbar.listeners.HotbarListener;
import de.raidcraft.combatbar.tables.THotbar;
import de.raidcraft.combatbar.tables.THotbarSlot;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class RCCombatBarPlugin extends BasePlugin {

    @Getter
    private LocalConfiguration config;
    @Getter
    private HotbarManager hotbarManager;

    @Override
    public void reload() {
        getConfig().reload();
    }

    @Override
    public void enable() {
        config = configure(new LocalConfiguration(this));
        hotbarManager = new HotbarManager(this);
        registerEvents(new HotbarListener(this));
    }

    @Override
    public void disable() {

    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        ArrayList<Class<?>> classes = new ArrayList<>();
        classes.add(THotbar.class);
        classes.add(THotbarSlot.class);
        return classes;
    }

    public static class LocalConfiguration extends ConfigurationBase<RCCombatBarPlugin> {

        @Setting("menu.enabled")
        @Comment("Disables or enables the special hotbar menu.")
        public boolean enableMenuItem = true;
        @Setting("menu.item")
        @Comment("The name of the item that should be placed in the inventory of the player to open the hotbar menu.")
        public String menuItem = "minecraft:nether_star";
        @Setting("menu.items-slot")
        @Comment("The slot the menu item should be placed in. See https://minecraft.gamepedia.com/Inventory for the slot ids.")
        public int menuItemSlot = 8;

        public LocalConfiguration(RCCombatBarPlugin plugin) {

            super(plugin, "config.yml");
        }
    }
}
