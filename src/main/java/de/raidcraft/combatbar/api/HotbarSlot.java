package de.raidcraft.combatbar.api;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class HotbarSlot {

    /**
     * The item that will be displayed in the hotbar slot.
     */
    @Getter
    private final ItemStack item;

    public HotbarSlot(ItemStack item) {
        this.item = item;
    }

    /**
     * Called when a player selects the hotbar slot directly by pressing the number keys.
     * <p>
     * Override and implement your logic.
     *
     * @param player that selected the slot
     */
    public void onSelect(Player player) {
    }

    /**
     * Called when a player has selected the hotbar slot
     * and right clicks with the item in hand.
     * <p>
     * Override and implement your logic.
     *
     * @param player that right clicked
     */
    public void onRightClick(Player player) {
    }

    /**
     * Called when a player has selected the hotbar slot
     * and left clicks with the item in hand.
     * <p>
     * Override and implement your logic.
     *
     * @param player that left clicked
     */
    public void onLeftClick(Player player) {
    }
}
