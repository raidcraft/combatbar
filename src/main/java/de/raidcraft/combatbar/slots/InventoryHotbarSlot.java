package de.raidcraft.combatbar.slots;

import de.raidcraft.combatbar.api.Hotbar;
import de.raidcraft.combatbar.api.HotbarSlot;
import de.raidcraft.combatbar.api.HotbarSlotName;
import lombok.EqualsAndHashCode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;

@HotbarSlotName("inventory")
@EqualsAndHashCode(callSuper = true)
public class InventoryHotbarSlot extends HotbarSlot {

    public InventoryHotbarSlot() {
        setName("inventory");
        setSaveItem(true);
    }

    @Override
    public void load(ConfigurationSection config) {

    }

    @Override
    public void saveData(ConfigurationSection config) {

    }

    @Override
    protected void onDisable(Hotbar hotbar) {
        updateItem(hotbar.getInventory());
    }

    private void updateItem(Inventory inventory) {
        setItem(inventory.getItem(getIndex()));
    }
}
