package de.raidcraft.combatbar.hotbars;

import de.raidcraft.combatbar.api.Hotbar;
import de.raidcraft.combatbar.api.HotbarHolder;
import de.raidcraft.combatbar.api.HotbarName;
import de.raidcraft.combatbar.slots.InventoryHotbarSlot;
import lombok.EqualsAndHashCode;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

@HotbarName("inventory")
@EqualsAndHashCode(callSuper = true)
public class InventoryHotbar extends Hotbar {

    public InventoryHotbar(HotbarHolder holder) {
        super(holder);
        setDisplayName("Inventar");
        setFillEmptySlots(false);
    }

    @Override
    public void onDeactivate() {
        for (Integer index : getIndicies()) {
            ItemStack item = getInventory().getItem(index);
            if (item != null && item.getType() != Material.AIR) {
                if (!getHotbarSlot(index).isPresent()) {
                    InventoryHotbarSlot slot = new InventoryHotbarSlot();
                    slot.setItem(item);
                    setHotbarSlot(index, slot);
                }
            } else {
                removeHotbarSlot(index);
            }
        }
        save();
    }

    @Override
    public void onHotbarSlotChange(PlayerItemHeldEvent event) {
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getSlot() == getHolder().getMenuSlotIndex()) event.setCancelled(true);
    }

    @Override
    public void onItemDrop(PlayerDropItemEvent event) {
        if (getInventory().getHeldItemSlot() == getHolder().getMenuSlotIndex()) event.setCancelled(true);
    }

//    @Override
//    public void save() {
//        if (!isActive()) return;
//        super.save();
//    }
}
