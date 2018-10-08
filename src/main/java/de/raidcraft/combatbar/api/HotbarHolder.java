package de.raidcraft.combatbar.api;

import de.raidcraft.RaidCraft;
import de.raidcraft.combatbar.HotbarUtils;
import de.raidcraft.combatbar.RCHotbarPlugin;
import de.raidcraft.combatbar.tables.THotbarHolder;
import de.raidcraft.util.InventoryUtils;
import io.ebean.EbeanServer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Data
@EqualsAndHashCode(of = {"player"})
public class HotbarHolder implements Listener {

    private final Player player;
    private final List<Hotbar> hotbars = new ArrayList<>();
    private int databaseId = -1;
    private int activeHotbar = 0;
    private int menuSlotIndex = 8;
    private boolean enabled = false;
    private ItemStack menuSlotItem = null;
    private Consumer<HotbarHolder> menuItemAction = null;

    public Optional<Integer> getDatabaseId() {
        return databaseId < 0 ? Optional.empty() : Optional.of(databaseId);
    }

    public int getActiveHotbarSlot() {
        return this.activeHotbar;
    }

    public Optional<Hotbar> getActiveHotbar() {
        if (this.activeHotbar < 0 || this.hotbars.size() <= this.activeHotbar) return Optional.empty();
        return Optional.ofNullable(this.hotbars.get(this.activeHotbar));
    }

    public final ItemStack getMenuSlotItem() {
        if (this.menuSlotItem != null) return this.menuSlotItem;
        this.menuSlotItem = HotbarUtils.createMenuItem(Material.NETHER_STAR);
        return menuSlotItem;
    }

    public final Optional<Consumer<HotbarHolder>> getMenuItemAction() {
        return Optional.ofNullable(menuItemAction);
    }

    public final PlayerInventory getInventory() {
        return getPlayer().getInventory();
    }

    public final int getHeldItemSlot() {
        return getInventory().getHeldItemSlot();
    }

    public boolean isUsingMenuSlot() {
        return RaidCraft.getComponent(RCHotbarPlugin.class).getConfig().useMenuSlot;
    }

    public final void setActiveHotbar(int index) {
        if (index < 0 || index >= getHotbars().size()) return;
        Hotbar hotbar = getHotbars().get(index);

        if (hotbar.isActive()) return;
        getActiveHotbar().ifPresent(Hotbar::deactivate);
        this.activeHotbar = index;
        hotbar.activate();
    }

    public void addHotbar(Hotbar hotbar) {
        addHotbar(hotbar, false);
    }

    public void addHotbar(Hotbar hotbar, boolean activate) {
        if (hotbar == null) return;
        if (!hotbars.contains(hotbar)) {
            this.hotbars.add(hotbar);
        }

        if (activate) activate(hotbar);
    }

    public void removeHotbar(Hotbar hotbar) {
        if (hotbar == null) return;
        hotbar.deactivate();
        this.hotbars.remove(hotbar);
        hotbar.delete();
        save();
    }

    public void activate(Hotbar hotbar) {
        int index = this.hotbars.indexOf(hotbar);
        if (index > -1) setActiveHotbar(index);
    }

    public void setEnabled(boolean enabled) {
        if (enabled) {
            enable();
        } else {
            disable();
        }
    }

    public void enable() {
        if (isEnabled()) return;
        this.enabled = true;
        getActiveHotbar().ifPresent(Hotbar::activate);
        if (isUsingMenuSlot()) {
            if (!getMenuSlotItem().isSimilar(getInventory().getItem(getMenuSlotIndex()))) {
                InventoryUtils.setAndDropOrAddItem(getPlayer(), getMenuSlotItem(), getMenuSlotIndex());
            }
        }
    }

    public void disable() {
        if (!isEnabled()) return;
        this.enabled = false;
        getActiveHotbar().ifPresent(Hotbar::deactivate);
        save();

        if (isUsingMenuSlot()) {
            getPlayer().getInventory().clear(getMenuSlotIndex());
        }
    }

    @EventHandler()
    public void onSlotChange(PlayerItemHeldEvent event) {

        if (!isEnabled()) return;
        if (event.getPreviousSlot() == event.getNewSlot()) return;
        if (!event.getPlayer().equals(getPlayer())) return;

        // handle hotbar cylcing
        if (RaidCraft.getComponent(RCHotbarPlugin.class).getConfig().allowHotbarSwapping
                && event.getPlayer().isSneaking() && event.getPreviousSlot() == getMenuSlotIndex()) {
            if (getHotbars().size() < 2) return;
            getActiveHotbar().ifPresent(Hotbar::deactivate);
            if (event.getNewSlot() < event.getPreviousSlot()) {
                this.activeHotbar++;
                if (this.activeHotbar >= this.hotbars.size()) this.activeHotbar = 0;
            } else if (event.getNewSlot() > event.getPreviousSlot() || event.getNewSlot() == 0) {
                this.activeHotbar--;
                if (this.activeHotbar < 0) this.activeHotbar = this.hotbars.size() - 1;
            }
            getActiveHotbar().ifPresent(Hotbar::activate);
            event.setCancelled(true);
            return;
        }

        getActiveHotbar().ifPresent(hotbar -> hotbar.onHotbarSlotChange(event));
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        if (!isEnabled()) return;
        if (!event.getPlayer().equals(getPlayer())) return;

        if (isUsingMenuSlot() && getHeldItemSlot() == getMenuSlotIndex()) {
            event.setCancelled(true);
            switch (event.getAction()) {
                case LEFT_CLICK_AIR:
                case RIGHT_CLICK_AIR:
                    getMenuItemAction().ifPresent(consumer -> consumer.accept(this));
                    break;
            }
            return;
        }

        getActiveHotbar().ifPresent(hotbar -> hotbar.onInteract(event));
    }

    @EventHandler
    public void onPlayerPlaceBlock(BlockPlaceEvent event) {

        if (!isEnabled()) return;
        if (!event.getPlayer().equals(getPlayer())) return;

        getActiveHotbar().ifPresent(hotbar -> hotbar.onBlockPlace(event));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (!isEnabled()) return;
        if (!event.getWhoClicked().equals(getPlayer())) return;
        if (event.getSlotType() != InventoryType.SlotType.QUICKBAR) return;

        getActiveHotbar().ifPresent(hotbar -> hotbar.onInventoryClick(event));
    }

    @EventHandler
    public void onItemDropEvent(PlayerDropItemEvent event) {
        if (!isEnabled()) return;
        if (!event.getPlayer().equals(getPlayer())) return;

        getActiveHotbar().ifPresent(hotbar -> hotbar.onItemDrop(event));
    }

    protected boolean isActiveHotbarSlot(int index) {
        return getActiveHotbar().map(hotbar -> hotbar.getHotbarSlot(index).isPresent())
                .orElse(isUsingMenuSlot() && index == getMenuSlotIndex());
    }

    protected boolean isHotbarSlot(int index) {
        return getActiveHotbar().filter(hotbar -> hotbar.getIndicies().contains(index)
                || (isUsingMenuSlot() && index == getMenuSlotIndex())).isPresent();
    }

    public void save() {
        EbeanServer database = RaidCraft.getDatabase(RCHotbarPlugin.class);
        getDatabaseId().map(id -> database.find(THotbarHolder.class, id))
                .ifPresent(holder -> {
                    getActiveHotbar().map(Hotbar::getDatabaseId).ifPresent(integer -> integer.ifPresent(holder::setActiveHotbar));
                    database.save(holder);
                });
    }

    public void delete() {
        EbeanServer database = RaidCraft.getDatabase(RCHotbarPlugin.class);
        getDatabaseId().map(id -> database.find(THotbarHolder.class, id))
                .ifPresent(database::delete);
    }
}
