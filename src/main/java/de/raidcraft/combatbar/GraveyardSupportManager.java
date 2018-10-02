package de.raidcraft.combatbar;

import de.raidcraft.RaidCraft;
import de.raidcraft.rcgraveyards.RCGraveyardsPlugin;
import de.raidcraft.rcgraveyards.events.RCGraveyardPlayerRevivedEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class GraveyardSupportManager implements Listener {

    private final RCHotbarPlugin plugin;

    public GraveyardSupportManager(RCHotbarPlugin plugin) {
        this.plugin = plugin;
        plugin.registerEvents(this);
    }

    @EventHandler
    public void onPlayerRespawn(RCGraveyardPlayerRevivedEvent event) {
        Player player = event.getGraveyardPlayer().getPlayer();
        plugin.getHotbarManager().registerPlayer(player);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        plugin.getHotbarManager().unregisterPlayer(event.getEntity());
    }

    public boolean isPlayerAlive(Player player) {
        RCGraveyardsPlugin graveyardsPlugin = RaidCraft.getComponent(RCGraveyardsPlugin.class);
        if (graveyardsPlugin == null || graveyardsPlugin.getGhostManager() == null) return true;
        return !graveyardsPlugin.getGhostManager().isGhost(player);
    }
}
