package de.raidcraft.combatbar.factories;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.combatbar.RCHotbarPlugin;
import de.raidcraft.combatbar.api.HotbarHolder;
import de.raidcraft.combatbar.tables.THotbarHolder;
import lombok.Data;
import org.bukkit.entity.Player;

@Data
public class HotbarHolderFactory {

    private final RCHotbarPlugin plugin;

    public HotbarHolder create(Player player) {
        THotbarHolder dbEntry = getOrCreateDbHotbarHolder(player);

        HotbarHolder holder = new HotbarHolder(player);
        holder.setDatabaseId(dbEntry.getId());
        holder.setActiveHotbar(dbEntry.getActiveHotbar());

        loadHotbars(dbEntry, holder);

        return holder;
    }

    private void loadHotbars(THotbarHolder dbEntry, HotbarHolder holder) {
        if (dbEntry.getHotbars().size() < 1 && getPlugin().getConfig().addDefaultHotbar) {
            holder.addHotbar(plugin.getHotbarManager().getDefaultHotbarFactory().createNew(holder));
        } else {
            dbEntry.getHotbars().forEach(hotbar -> {
                plugin.getHotbarManager().getHotbarFactory(hotbar.getName())
                        .map(factory -> factory.create(hotbar, holder))
                        .ifPresent(holder::addHotbar);
            });
        }
    }

    private THotbarHolder getOrCreateDbHotbarHolder(Player player) {

        EbeanServer database = getPlugin().getDatabase();

        THotbarHolder tHotbarHolder = database
                .find(THotbarHolder.class)
                .where().eq("player_id", player.getUniqueId())
                .findUnique();

        if (tHotbarHolder == null) {
            tHotbarHolder = new THotbarHolder();
            tHotbarHolder.setPlayerId(player.getUniqueId());
            tHotbarHolder.setPlayer(player.getName());
            database.save(tHotbarHolder);
        }

        return tHotbarHolder;
    }
}
