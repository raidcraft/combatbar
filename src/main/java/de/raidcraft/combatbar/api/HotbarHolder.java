package de.raidcraft.combatbar.api;

import de.raidcraft.combatbar.RCCombatBarPlugin;
import lombok.Data;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
public class HotbarHolder implements Listener {

    private final Player player;
    private final RCCombatBarPlugin.LocalConfiguration config;
    private final List<Hotbar> hotbars = new ArrayList<>();
    private int activeHotbar = 0;

    public Optional<Hotbar> getActiveHotbar() {
        if (this.activeHotbar < 0 || this.hotbars.size() < this.activeHotbar) return Optional.empty();
        return Optional.ofNullable(this.hotbars.get(this.activeHotbar));
    }

    @EventHandler()
    public void onSlotChange(PlayerItemHeldEvent event) {

        if (event.getPreviousSlot() == event.getNewSlot()) return;
        if (!event.getPlayer().equals(getPlayer())) return;

        getActiveHotbar().ifPresent(hotbar -> hotbar.onHotbarSlotChange(event));

        // handle hotbar cylcing
        if (event.getPlayer().isSneaking() && event.getPreviousSlot() == config.menuItemSlot) {
            getActiveHotbar().ifPresent(Hotbar::deactivate);
            if (event.getNewSlot() < event.getPreviousSlot()) {
                this.activeHotbar++;
                if (this.activeHotbar >= this.hotbars.size()) this.activeHotbar = -1;
            } else if (event.getNewSlot() > event.getPreviousSlot() || event.getNewSlot() == 0) {
                if (this.activeHotbar < 0) {
                    this.activeHotbar = this.hotbars.size() - 1;
                } else {
                    this.activeHotbar--;
                }
            }
            getActiveHotbar().ifPresent(Hotbar::activate);
            event.setCancelled(true);
        }
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
}
