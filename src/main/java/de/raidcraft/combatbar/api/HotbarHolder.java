package de.raidcraft.combatbar.api;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.RaidCraft;
import de.raidcraft.combatbar.RCHotbarPlugin;
import de.raidcraft.combatbar.tables.THotbarHolder;
import lombok.Data;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
public class HotbarHolder implements Listener {

    private final Player player;
    private final List<Hotbar> hotbars = new ArrayList<>();
    private int databaseId = -1;
    private int activeHotbar = 0;

    public Optional<Integer> getDatabaseId() {
        return databaseId < 0 ? Optional.empty() : Optional.of(databaseId);
    }

    public int getActiveHotbarSlot() {
        return this.activeHotbar;
    }

    public Optional<Hotbar> getActiveHotbar() {
        if (this.activeHotbar < 0 || this.hotbars.size() < this.activeHotbar) return Optional.empty();
        return Optional.ofNullable(this.hotbars.get(this.activeHotbar));
    }

    public void addHotbar(Hotbar hotbar) {
        this.hotbars.add(hotbar);
    }

    @EventHandler()
    public void onSlotChange(PlayerItemHeldEvent event) {

        if (event.getPreviousSlot() == event.getNewSlot()) return;
        if (!event.getPlayer().equals(getPlayer())) return;

        getActiveHotbar().ifPresent(hotbar -> hotbar.onHotbarSlotChange(event));

        if (hotbars.size() < 2) return;

        // handle hotbar cylcing
        getActiveHotbar().ifPresent(hotbar -> {
            if (event.getPlayer().isSneaking() && event.getPreviousSlot() == hotbar.getMenuSlotIndex()) {
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
            }
        });
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        if (!event.getPlayer().equals(event.getPlayer())) return;

        getActiveHotbar().ifPresent(hotbar -> hotbar.onInteract(event));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (!event.getWhoClicked().equals(getPlayer())) return;

        getActiveHotbar().ifPresent(hotbar -> hotbar.onInventoryClick(event));
    }

    @EventHandler
    public void onItemDropEvent(PlayerDropItemEvent event) {

        int heldItemSlot = event.getPlayer().getInventory().getHeldItemSlot();
        getActiveHotbar().filter(hotbar -> hotbar.getIndicies().contains(heldItemSlot)
                || heldItemSlot == hotbar.getMenuSlotIndex())
                .ifPresent(hotbar -> event.setCancelled(true));
    }

    public void save() {
        EbeanServer database = RaidCraft.getDatabase(RCHotbarPlugin.class);
        getDatabaseId().map(id -> database.find(THotbarHolder.class, id))
                .ifPresent(holder -> {
                    holder.setActiveHotbar(getActiveHotbarSlot());
                    database.save(holder);
                });

        getHotbars().forEach(Hotbar::save);
    }
}
