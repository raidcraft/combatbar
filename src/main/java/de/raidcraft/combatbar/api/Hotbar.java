package de.raidcraft.combatbar.api;

import lombok.Data;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

import java.util.*;

@Data
public class Hotbar {

    private final Map<Integer, HotbarSlot> slots = new HashMap<>();

    private final HotbarHolder holder;

    private String name = "Hotbar";
    private int baseSlotIndex = 0;
    private int menuSlotIndex = 8;
    private boolean active = false;
    private List<Integer> indicies = new ArrayList<Integer>() {{
        add(2);
        add(3);
        add(4);
        add(5);
        add(6);
        add(7);
    }};

    /**
     * Gets the given hotbarslot at the index.
     *
     * @param index to get hotbar slot for.
     * @return optional hotbar slot
     */
    public final Optional<HotbarSlot> getHotbarSlot(int index) {
        return Optional.ofNullable(slots.get(index));
    }

    /**
     * Tries to add the given hotbar slot to the hotbar.
     * Will only place the slot onto the hotbar if the hotbar has free space.
     *
     * @param slot to add to the hotbar
     * @return true if slot was added, false if hotbar has no free space
     */
    public final boolean addHotbarSlot(HotbarSlot slot) {

        for (int index : this.indicies) {
            if (this.slots.containsKey(index)) continue;
            setHotbarSlot(index, slot);
            return true;
        }

        return false;
    }

    /**
     * Will set the given slot in the hotbar replacing any existing slot at the given index.
     *
     * @param index to place the slot into.
     * @param slot  to place into the hotbar.
     * @return previous hotbarslot value if it existed
     */
    public final Optional<HotbarSlot> setHotbarSlot(int index, HotbarSlot slot) {
        return Optional.ofNullable(this.slots.put(index, slot));
    }

    /**
     * Clears the hotbar and returns all removed {@link HotbarSlot}s.
     *
     * @return a list of all removed {@link HotbarSlot}s.
     */
    public final Collection<HotbarSlot> clearHotbarSlots() {
        Collection<HotbarSlot> slots = this.slots.values();
        this.slots.clear();

        return slots;
    }

    /**
     * Removes the {@link HotbarSlot} at the given index.
     *
     * @param index to remove slot from.
     * @return the removed {@link HotbarSlot} if it existed.
     */
    public final Optional<HotbarSlot> clearHotbarSlot(int index) {
        return Optional.ofNullable(this.slots.remove(index));
    }

    public void setActive(boolean active) {
        if (active) {
            activate();
        } else {
            deactivate();
        }
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public void onHotbarSlotChange(PlayerItemHeldEvent event) {
        // handle hotbar slot activation
        if (event.getPreviousSlot() == getBaseSlotIndex() && getIndicies().contains(event.getNewSlot())) {
            getHotbarSlot(event.getNewSlot()).ifPresent(slot -> slot.onSelect(event.getPlayer()));
            event.setCancelled(true);
            return;
        }
    }

    public void onInteract(PlayerInteractEvent event) {
        getHotbarSlot(event.getPlayer().getInventory().getHeldItemSlot()).ifPresent(slot -> {
            switch (event.getAction()) {
                case LEFT_CLICK_AIR:
                case LEFT_CLICK_BLOCK:
                    slot.onLeftClickInteract(event.getPlayer());
                    break;
                case RIGHT_CLICK_AIR:
                case RIGHT_CLICK_BLOCK:
                    slot.onRightClickInteract(event.getPlayer());
                    break;
            }
        });
    }

    public void onInventoryClick(InventoryClickEvent event) {
        getHotbarSlot(event.getSlot()).ifPresent(slot -> {

            Player player = (Player) event.getWhoClicked();
            switch (event.getClick()) {
                case RIGHT:
                    slot.onInventoryRightClick(player);
                    break;
                case LEFT:
                    slot.onInventoryLeftClick(player);
                    break;
                case MIDDLE:
                    slot.onInventoryMiddleClick(player);
                    break;
                case DOUBLE_CLICK:
                    slot.onInventoryDoubleClick(player);
                    break;
            }
        });
    }
}
