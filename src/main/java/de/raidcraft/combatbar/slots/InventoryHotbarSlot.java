package de.raidcraft.combatbar.slots;

import de.raidcraft.combatbar.api.Hotbar;
import de.raidcraft.combatbar.api.HotbarSlot;
import de.raidcraft.combatbar.api.HotbarSlotName;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

@HotbarSlotName("inventory")
public class InventoryHotbarSlot extends HotbarSlot {

    public InventoryHotbarSlot() {
        setSaveItem(true);
    }

    @Override
    public ItemStack getItem() {
        return getHotbar()
                .map(Hotbar::getInventory)
                .map(inventory -> inventory.getItem(getIndex()))
                .orElse(null);
    }

    @Override
    public void load(ConfigurationSection config) {
    }

    @Override
    public void saveData(ConfigurationSection config) {
    }
}
