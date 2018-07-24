package de.raidcraft.combatbar.api;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.RaidCraft;
import de.raidcraft.combatbar.HotbarUtils;
import de.raidcraft.combatbar.RCHotbarPlugin;
import de.raidcraft.combatbar.tables.THotbar;
import de.raidcraft.combatbar.tables.THotbarHolder;
import de.raidcraft.util.InventoryUtils;
import lombok.Data;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

@Data
@HotbarName("default")
public class Hotbar {

    private final Map<Integer, HotbarSlot> slots = new HashMap<>();

    private final HotbarHolder holder;

    private int databaseId = -1;
    private String displayName = "Hotbar";
    private int baseSlotIndex = 0;
    private boolean active = false;
    private boolean fillEmptySlots = true;

    private List<Integer> indicies = new ArrayList<Integer>() {{
        add(2);
        add(3);
        add(4);
        add(5);
        add(6);
        add(7);
    }};

    public final Optional<Integer> getDatabaseId() {
        return databaseId < 0 ? Optional.empty() : Optional.of(databaseId);
    }

    public final Player getPlayer() {
        return getHolder().getPlayer();
    }

    public final PlayerInventory getInventory() {
        return getPlayer().getInventory();
    }

    public final int getActiveItemSlot() {
        return getInventory().getHeldItemSlot();
    }

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
     * If the index is out of range of the {@link #indicies} an empty optional will be returned.
     *
     * @param index to place the slot into.
     * @param slot  to place into the hotbar.
     * @return previous hotbarslot value if it existed
     */
    public final Optional<HotbarSlot> setHotbarSlot(int index, HotbarSlot slot) {
        if (!indicies.contains(index)) return Optional.empty();
        slot.setIndex(index);
        slot.
                attach(this);
        Optional<HotbarSlot> result = Optional.ofNullable(this.slots.put(index, slot));
        result.ifPresent(HotbarSlot::detach);
        save();
        return result;
    }

    /**
     * Clears the hotbar and returns all removed {@link HotbarSlot}s.
     *
     * @return a list of all removed {@link HotbarSlot}s.
     */
    public final Collection<HotbarSlot> clearHotbarSlots() {
        Collection<HotbarSlot> slots = this.slots.values();
        slots.forEach(HotbarSlot::detach);
        this.slots.clear();
        save();
        fillEmptySlots();
        return slots;
    }

    /**
     * Removes the {@link HotbarSlot} at the given index.
     *
     * @param index to remove slot from.
     * @return the removed {@link HotbarSlot} if it existed.
     */
    public final Optional<HotbarSlot> removeHotbarSlot(int index) {
        HotbarSlot removedSlot = this.slots.remove(index);
        if (removedSlot != null) removedSlot.detach();
        save();
        fillEmptySlots();
        return Optional.ofNullable(removedSlot);
    }

    public void setActive(boolean active) {
        if (active) {
            activate();
        } else {
            deactivate();
        }
    }

    void activate() {
        if (isActive()) return;
        try {
            onActivate();
            this.active = true;
            new ArrayList<>(getSlots().values()).forEach(slot -> slot.attach(this));
            save();
            fillEmptySlots();
        } catch (HotbarException e) {
            getPlayer().sendMessage(ChatColor.RED + "Konnte Hotbar " + getDisplayName() + " nicht aktivieren!");
            getPlayer().sendMessage(ChatColor.RED + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * onActivate is called just before the hotbar is initialized.
     * Throw a {@link HotbarException} to cancel the activa
     * tion.
     */
    protected void onActivate() throws HotbarException {
    }

    void deactivate() {
        if (!isActive()) return;
        onDeactivate();
        this.active = false;
        save();
        getIndicies().forEach(index -> getInventory().clear(index));
    }

    /**
     * Is called just before the hotbar is deavtivated and all slots are cleared.
     */
    protected void onDeactivate() {
    }

    public void onHotbarSlotChange(PlayerItemHeldEvent event) {
        // handle hotbar slot activation
        if (event.getPreviousSlot() == getBaseSlotIndex() && getIndicies().contains(event.getNewSlot())) {
            getHotbarSlot(event.getNewSlot()).ifPresent(slot -> {
                slot.onSelect(event.getPlayer());
                if (slot.isCancelOnSelect()) event.setCancelled(true);
            });
            return;
        }
    }

    public void onInteract(PlayerInteractEvent event) {
        if (!isActive()) return;
        getHotbarSlot(getActiveItemSlot()).ifPresent(slot -> {
            switch (event.getAction()) {
                case LEFT_CLICK_AIR:
                case LEFT_CLICK_BLOCK:
                    slot.onLeftClickInteract(event);
                    break;
                case RIGHT_CLICK_AIR:
                case RIGHT_CLICK_BLOCK:
                    slot.onRightClickInteract(event);
                    break;
            }
        });

        if (getIndicies().contains(getActiveItemSlot())) {
            event.setCancelled(true);
        }
    }

    public void onInventoryClick(InventoryClickEvent event) {
        if (!isActive()) return;
        if (getHolder().isHotbarSlot(event.getSlot())) event.setCancelled(true);

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

    public void onItemDrop(PlayerDropItemEvent event) {
        if (!isActive()) return;
        if (getHolder().isHotbarSlot(getHolder().getHeldItemSlot())) event.setCancelled(true);
    }

    public final void fillEmptySlots(ItemStack item) {
        if (!isFillEmptySlots()) return;
        // block all empty hotbar slots with a void item
        for (Integer index : getIndicies()) {
            if (!getHotbarSlot(index).isPresent()) {
                ItemStack currentItem = getInventory().getItem(index);
                if (item.isSimilar(currentItem) || HotbarUtils.getEmptySlotItem().isSimilar(currentItem)) {
                    getInventory().setItem(index, item);
                } else {
                    InventoryUtils.setAndDropOrAddItem(getPlayer(), item, index);
                }
            }
        }
    }

    public final void fillEmptySlots() {
        fillEmptySlots(HotbarUtils.getEmptySlotItem());
    }

    public void save() {
        EbeanServer database = RaidCraft.getDatabase(RCHotbarPlugin.class);
        getDatabaseId().map(id -> database.find(THotbar.class, id))
                .ifPresent(hotbar -> {
                    hotbar.setDisplayName(getDisplayName());
                    hotbar.setActive(isActive());
                    getHolder().getDatabaseId().ifPresent(id -> hotbar.setHolder(database.find(THotbarHolder.class, id)));
                    database.save(hotbar);
                });

        getSlots().values().forEach(HotbarSlot::save);
    }

    public void delete() {
        EbeanServer database = RaidCraft.getDatabase(RCHotbarPlugin.class);
        getDatabaseId().map(id -> database.find(THotbar.class, id))
                .ifPresent(database::delete);
    }

    public void onBlockPlace(BlockPlaceEvent event) {
        if (!isActive()) return;
        Optional<HotbarSlot> hotbarSlot = getHotbarSlot(getActiveItemSlot());
        if (hotbarSlot.map(HotbarSlot::isCancelBlockPlacement).orElse(false)) event.setCancelled(true);

        hotbarSlot.ifPresent(slot -> slot.onPlayerBlaceBlock(event));
    }
}
