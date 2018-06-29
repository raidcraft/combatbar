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
    public void onRightClickInteract(Player player) {
    }

    /**
     * Called when a player has selected the hotbar slot
     * and left clicks with the item in hand.
     * <p>
     * Override and implement your logic.
     *
     * @param player that left clicked
     */
    public void onLeftClickInteract(Player player) {
    }

    /**
     * Called when the player has the inventory open and right clicks the slot.
     * <p>
     * Override and implement your logic.
     *
     * @param player who clicked
     */
    public void onInventoryRightClick(Player player) {
    }

    /**
     * Called when the player has the inventory open and left clicks the slot.
     * <p>
     * Override and implement your logic.
     *
     * @param player who clicked
     */
    public void onInventoryLeftClick(Player player) {
    }

    /**
     * Called when the player has the inventory open and double clicks the slot.
     * <p>
     * Override and implement your logic.
     *
     * @param player who clicked
     */
    public void onInventoryDoubleClick(Player player) {
    }

    /**
     * Called when the player has the inventory open and middle clicks the slot.
     * <p>
     * Override and implement your logic.
     *
     * @param player who clicked
     */
    public void onInventoryMiddleClick(Player player) {
    }
}
