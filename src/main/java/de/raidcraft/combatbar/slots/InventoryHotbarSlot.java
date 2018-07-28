package de.raidcraft.combatbar.slots;

import de.raidcraft.RaidCraft;
import de.raidcraft.combatbar.api.Hotbar;
import de.raidcraft.combatbar.api.HotbarSlot;
import de.raidcraft.combatbar.api.HotbarSlotName;
import lombok.EqualsAndHashCode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

@HotbarSlotName("inventory")
@EqualsAndHashCode(callSuper = true)
public class InventoryHotbarSlot extends HotbarSlot {

    private ItemStack item;

    public InventoryHotbarSlot() {
        setName("inventory");
        setSaveItem(false);
    }

    @Override
    public ItemStack getItem() {
        return getHotbar()
                .map(Hotbar::getInventory)
                .map(inventory -> inventory.getItem(getIndex()))
                .orElse(item);
    }

    @Override
    public void load(ConfigurationSection config) {
        String itemId = config.getString("item");
        item = RaidCraft.getUnsafeItem(itemId);
        RaidCraft.removeStoredItem(itemId);
        getHotbar().map(Hotbar::getInventory).ifPresent(inventory -> inventory.setItem(getIndex(), item));
    }

    @Override
    public void saveData(ConfigurationSection config) {
        item = getItem();
        if (item == null) return;
        config.set("item", RaidCraft.getItemIdString(item, true));
    }

    @Override
    protected void save() {
        if (!getHotbar().map(Hotbar::isActive).orElse(false)) return;
        super.save();
    }
}
