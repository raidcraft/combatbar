package de.raidcraft.combatbar;

import de.raidcraft.api.Component;
import de.raidcraft.combatbar.api.Hotbar;
import de.raidcraft.combatbar.api.HotbarHolder;
import de.raidcraft.util.items.EnchantGlow;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class HotbarManager implements Component {

    @Getter
    private final RCCombatBarPlugin module;
    private final Map<UUID, HotbarHolder> hotbarHolders = new HashMap<>();

    public HotbarManager(RCCombatBarPlugin module) {
        this.module = module;
    }

    /**
     * Gets the active {@link Hotbar} of the given player.
     *
     * @param player to get hotbar for
     * @return active {@link Hotbar}
     */
    public Optional<Hotbar> getActiveHotbar(Player player) {
        return Optional.ofNullable(this.hotbarHolders.get(player.getUniqueId())).flatMap(HotbarHolder::getActiveHotbar);
    }

    public void addAllHotbars() {
        Bukkit.getOnlinePlayers().forEach(this::addHotbar);
    }

    public void addHotbar(Player player) {
        player.getInventory().setItem(getModule().getConfig().menuItemSlot, createMenuItem());
    }

    public void clearAllHotbars() {
        Bukkit.getOnlinePlayers().forEach(this::clearHotbar);
    }

    public void clearHotbar(Player player) {
        player.getInventory().clear(getModule().getConfig().menuItemSlot);
    }

    private ItemStack createMenuItem() {
        ItemStack itemStack = new ItemStack(Material.matchMaterial(getModule().getConfig().menuItem));

        EnchantGlow.addGlow(itemStack);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GOLD + "-=[ " + ChatColor.AQUA + "Hotbar Menü" + ChatColor.GOLD + " ]=-");

        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        itemMeta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GREEN + "Rechtsklick: Öffnet das Hotbar Menü.");

        itemMeta.setLore(lore);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public void openHotbarMenu(Player player) {
        player.sendMessage("Hotbar opened!");
    }
}
