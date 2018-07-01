package de.raidcraft.combatbar;

import de.raidcraft.combatbar.api.Hotbar;
import de.raidcraft.combatbar.api.HotbarName;
import de.raidcraft.util.items.EnchantGlow;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public final class HotbarUtils {

    private HotbarUtils() {
    }

    public static ItemStack getEmptySlotItem() {
        ItemStack item = new ItemStack(Material.STRUCTURE_VOID);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName("Hotbar Slot nicht belegt.");
        item.setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack createMenuItem(Material material) {
        ItemStack itemStack = new ItemStack(material);

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

    public static String getHotbarName(Class<? extends Hotbar> hotbarClass) {
        HotbarName annotation = hotbarClass.getAnnotation(HotbarName.class);
        return annotation != null ? annotation.value() : null;
    }
}
