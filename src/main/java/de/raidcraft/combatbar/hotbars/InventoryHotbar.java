package de.raidcraft.combatbar.hotbars;

import de.raidcraft.combatbar.api.Hotbar;
import de.raidcraft.combatbar.api.HotbarHolder;
import de.raidcraft.combatbar.api.HotbarName;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

@HotbarName("inventory")
public class InventoryHotbar extends Hotbar {

    public InventoryHotbar(HotbarHolder holder) {
        super(holder);
        setDisplayName("Inventar");
        setFillEmptySlots(false);
    }

    @Override
    public void onHotbarSlotChange(PlayerItemHeldEvent event) {
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
    }

    @Override
    public void onItemDrop(PlayerDropItemEvent event) {
    }
}
