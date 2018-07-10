package de.raidcraft.combatbar.api;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.RaidCraft;
import de.raidcraft.combatbar.RCHotbarPlugin;
import de.raidcraft.combatbar.tables.THotbar;
import de.raidcraft.combatbar.tables.THotbarSlot;
import de.raidcraft.combatbar.tables.THotbarSlotData;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * A Hotbar slot can be attached to hotbar and therefor be useable.
 * A Hotbar slot can also exist without a hotbar to allow displaying it in a configuration menu.
 */
public abstract class HotbarSlot {

    @Getter
    @Setter
    private String name;

    @Setter
    private int databaseId = -1;
    /**
     * The item that will be displayed in the hotbar slot.
     */
    @Getter
    @Setter
    private ItemStack item = new ItemStack(Material.STRUCTURE_VOID);
    @Getter
    @Setter
    private int index = -1;
    @Setter
    @Getter
    private boolean saveItem = true;
    /**
     * The {@link Hotbar} this slot is attached to.
     * Can be null if the slot has not been attached to a hotbar.
     */
    @Getter
    private Hotbar hotbar;

    public Optional<Hotbar> getHotbar() {
        return Optional.ofNullable(hotbar);
    }

    public final void attach(Hotbar hotbar) {
        try {
            this.hotbar = hotbar;
            onAttach(hotbar);
            hotbar.getInventory().setItem(getIndex(), getItem());
        } catch (HotbarException e) {
            hotbar.getHolder().getPlayer().sendMessage(ChatColor.RED + "Hotbar Slot " + getIndex() + " deaktiviert:");
            hotbar.getHolder().getPlayer().sendMessage(ChatColor.RED + e.getMessage());
            e.printStackTrace();
            hotbar.removeHotbarSlot(getIndex());
        }
    }

    /**
     * Called when the hotbar slot is attached to the {@link Hotbar}.
     * <p>
     * Override and implement your logic.
     *
     * @param hotbar the slot is attached to.
     */
    public void onAttach(Hotbar hotbar) throws HotbarException {
    }

    public final void detach() {
        getHotbar().ifPresent(hotbar -> hotbar.getInventory().setItem(getIndex(), new ItemStack(Material.AIR)));
        delete();
        this.hotbar = null;
    }

    public final Optional<Integer> getDatabaseId() {
        return databaseId < 0 ? Optional.empty() : Optional.of(databaseId);
    }

    /**
     * Will be called when all needed data is available and the hotbarslot should be loaded.
     * After loading the slot will be displayed to the player and is active.
     *
     * @param config to load data from
     */
    public abstract void load(ConfigurationSection config);

    /**
     * Called when the {@link HotbarSlot} is saved.
     * Set your data that you want to save in the {@link ConfigurationSection}.
     *
     * @param config to save data to
     */
    public abstract void saveData(ConfigurationSection config);

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

    protected final void save() {
        EbeanServer database = RaidCraft.getDatabase(RCHotbarPlugin.class);
        MemoryConfiguration config = new MemoryConfiguration();
        saveData(config);
        THotbarSlot slot = getDatabaseId().map(id -> database.find(THotbarSlot.class, id))
                .orElse(new THotbarSlot());

        getHotbar().flatMap(Hotbar::getDatabaseId)
                .map(id -> database.find(THotbar.class, id))
                .ifPresent(slot::setHotbar);

        slot.setName(getName());
        if (isSaveItem()) {
            ItemStack item = RaidCraft.getUnsafeItem(slot.getItem());
            if (item != null && !item.isSimilar(getItem())) {
                RaidCraft.removeStoredItem(slot.getItem());
            }
            slot.setItem(RaidCraft.getItemIdString(getItem(), item == null));
        }
        slot.setPosition(getIndex());

        slot.getData().clear();
        database.save(slot);
        setDatabaseId(slot.getId());

        for (String key : config.getKeys(false)) {
            THotbarSlotData data = database.find(THotbarSlotData.class).where()
                    .eq("slot_id", slot.getId())
                    .eq("data_key", key)
                    .findUnique();
            if (data == null) data = new THotbarSlotData();
            data.setDataKey(key);
            data.setDataValue(config.get(key, "").toString());
            data.setSlot(slot);
            database.save(data);
        }
    }

    public final void delete() {
        EbeanServer database = RaidCraft.getDatabase(RCHotbarPlugin.class);
        getDatabaseId().map(id -> database.find(THotbarSlot.class, id))
                .ifPresent(entry -> {
                    RaidCraft.removeStoredItem(entry.getItem());
                    database.delete(entry);
                });
    }
}
