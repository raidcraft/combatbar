package de.raidcraft.combatbar.listeners;

import de.raidcraft.combatbar.RCCombatBarPlugin;
import de.raidcraft.util.InventoryUtils;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class HotbarListener implements Listener {

    @Getter
    private final RCCombatBarPlugin module;
    @Getter
    private final RCCombatBarPlugin.LocalConfiguration config;

    public HotbarListener(RCCombatBarPlugin module) {
        this.module = module;
        config = getModule().getConfig();

    }

    @EventHandler()
    public void onHotbarMenuSelect(PlayerItemHeldEvent event) {

        event.getPlayer().sendMessage("Old Slot: " + event.getPreviousSlot() + " | New Slot: " + event.getNewSlot());
        if (event.getPreviousSlot() == 1 && event.getNewSlot() == 2)
            event.setCancelled(true);
        if (event.getPreviousSlot() == 8 && event.getNewSlot() == 7)
            event.setCancelled(true);
        if (event.getPreviousSlot() == 0 && event.getNewSlot() > 2 && event.getNewSlot() < 8)
            event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {

        if (config.enableMenuItem && event.getSlot() == config.menuItemSlot) {
            event.setCancelled(true);
            if (event.getClick() == ClickType.RIGHT)
                getModule().getHotbarManager().openHotbarMenu((Player) event.getWhoClicked());
        }
    }

    @EventHandler
    public void onItemDropEvent(PlayerDropItemEvent event) {

        if (config.enableMenuItem && event.getPlayer().getInventory().getHeldItemSlot() == config.menuItemSlot)
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        if (!config.enableMenuItem) return;

        ItemStack previousItem = event.getPlayer().getInventory().getItem(config.menuItemSlot);

        getModule().getHotbarManager().addHotbar(event.getPlayer());

        if (previousItem != null) {
            InventoryUtils.addOrDropItems(event.getPlayer(), previousItem);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        if (!config.enableMenuItem) return;

        getModule().getHotbarManager().clearHotbar(event.getPlayer());
    }
}
