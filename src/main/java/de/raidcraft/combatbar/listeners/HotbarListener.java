package de.raidcraft.combatbar.listeners;

import de.raidcraft.combatbar.RCHotbarPlugin;
import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class HotbarListener implements Listener {

    @Getter
    private final RCHotbarPlugin module;

    public HotbarListener(RCHotbarPlugin module) {
        this.module = module;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        getModule().getHotbarManager().registerPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        getModule().getHotbarManager().unregisterPlayer(event.getPlayer());
    }
}
