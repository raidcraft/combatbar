package de.raidcraft.combatbar;

import de.raidcraft.RaidCraft;
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
        RaidCraft.registerComponent(HotbarManager.class, hotbarManager);

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
        public String menuItem = "nether_star";
        @Setting("menu.items-slot")
        @Comment("The slot the menu item should be placed in. See https://minecraft.gamepedia.com/Inventory for the slot ids.")
        public int menuItemSlot = 8;
        @Setting("hotbar.base-slot")
        @Comment("The base slot of the hotbar. The hotbar will fire an interact event when the selected slot will differ from the given slot.")
        public int baseSlotIndex = 0;
        @Setting("hotbar.indicies")
        @Comment("The inventory indicies hotbar slots can occupy. Make sure to leave one empty index from the baseHotbarSlot to avoid scroll wheel problems. Do not include the menuItemSlot!")
        public int[] hotbarIndicies = new int[]{2, 3, 4, 5, 6, 7};

        public LocalConfiguration(RCCombatBarPlugin plugin) {

            super(plugin, "config.yml");
        }
    }
}
