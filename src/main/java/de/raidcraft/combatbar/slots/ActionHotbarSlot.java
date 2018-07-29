package de.raidcraft.combatbar.slots;

import de.raidcraft.combatbar.api.Hotbar;
import de.raidcraft.combatbar.api.HotbarException;
import de.raidcraft.combatbar.api.HotbarSlot;
import de.raidcraft.combatbar.api.HotbarSlotName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Data
@EqualsAndHashCode(callSuper = true)
@HotbarSlotName("action-slot")
public class ActionHotbarSlot extends HotbarSlot {

    private Consumer<Hotbar> onAttach = null;
    private Consumer<Player> onSelect = null;
    private Consumer<PlayerInteractEvent> onRightClickInteract = null;
    private Consumer<PlayerInteractEvent> onLeftClickInteract = null;
    private Consumer<Player> onInventoryRightClick = null;
    private Consumer<Player> onInventoryLeftClick = null;
    private Consumer<Player> onInventoryDoubleClick = null;
    private Consumer<Player> onInventoryMiddleClick = null;
    private Consumer<BlockPlaceEvent> onPlayerPlaceBlock = null;
    private Supplier<ItemStack> itemGetter;

    public ActionHotbarSlot() {
    }

    public ActionHotbarSlot(ItemStack item) {
        setItem(item);
    }

    @Override
    public ActionHotbarSlot setCancelOnSelect(boolean cancelOnSelect) {
        super.setCancelOnSelect(cancelOnSelect);
        return this;
    }

    @Override
    public ActionHotbarSlot setCancelBlockPlacement(boolean cancelBlockPlacement) {
        super.setCancelBlockPlacement(cancelBlockPlacement);
        return this;
    }

    @Override
    public ItemStack getItem() {
        if (getItemGetter() == null) return super.getItem();
        return getItemGetter().get();
    }

    public ActionHotbarSlot setItemGetter(Supplier<ItemStack> itemGetter) {
        this.itemGetter = itemGetter;
        return this;
    }

    public ActionHotbarSlot setOnAttach(Consumer<Hotbar> onAttach) {
        this.onAttach = onAttach;
        return this;
    }

    public ActionHotbarSlot setOnSelect(Consumer<Player> onSelect) {
        this.onSelect = onSelect;
        return this;
    }

    public ActionHotbarSlot setOnInteract(Consumer<PlayerInteractEvent> onInteract) {
        setOnRightClickInteract(onInteract);
        setOnLeftClickInteract(onInteract);
        return this;
    }

    public ActionHotbarSlot setOnRightClickInteract(Consumer<PlayerInteractEvent> onRightClickInteract) {
        this.onRightClickInteract = onRightClickInteract;
        return this;
    }

    public ActionHotbarSlot setOnLeftClickInteract(Consumer<PlayerInteractEvent> onLeftClickInteract) {
        this.onLeftClickInteract = onLeftClickInteract;
        return this;
    }

    public ActionHotbarSlot setOnInventoryClick(Consumer<Player> onInventoryClick) {
        setOnInventoryLeftClick(onInventoryClick);
        setOnInventoryRightClick(onInventoryClick);
        setOnInventoryDoubleClick(onInventoryClick);
        setOnInventoryMiddleClick(onInventoryClick);
        return this;
    }

    public ActionHotbarSlot setOnInventoryRightClick(Consumer<Player> onInventoryRightClick) {
        this.onInventoryRightClick = onInventoryRightClick;
        return this;
    }

    public ActionHotbarSlot setOnInventoryLeftClick(Consumer<Player> onInventoryLeftClick) {
        this.onInventoryLeftClick = onInventoryLeftClick;
        return this;
    }

    public ActionHotbarSlot setOnInventoryDoubleClick(Consumer<Player> onInventoryDoubleClick) {
        this.onInventoryDoubleClick = onInventoryDoubleClick;
        return this;
    }

    public ActionHotbarSlot setOnInventoryMiddleClick(Consumer<Player> onInventoryMiddleClick) {
        this.onInventoryMiddleClick = onInventoryMiddleClick;
        return this;
    }

    public ActionHotbarSlot setOnPlayerPlaceBlock(Consumer<BlockPlaceEvent> onPlayerPlaceBlock) {
        this.onPlayerPlaceBlock = onPlayerPlaceBlock;
        return this;
    }

    @Override
    public void load(ConfigurationSection config) {

    }

    @Override
    public void saveData(ConfigurationSection config) {

    }

    @Override
    public void onAttach(Hotbar hotbar) throws HotbarException {
        if (onAttach != null) onAttach.accept(hotbar);
    }

    @Override
    public void onSelect(Player player) {
        if (onSelect != null) onSelect.accept(player);
    }

    @Override
    public void onRightClickInteract(PlayerInteractEvent event) {
        if (onPlayerPlaceBlock != null) {
            event.setCancelled(false);
            return;
        }
        if (onRightClickInteract != null) onRightClickInteract.accept(event);
    }

    @Override
    public void onLeftClickInteract(PlayerInteractEvent event) {
        if (onLeftClickInteract != null) onLeftClickInteract.accept(event);
    }

    @Override
    public void onInventoryRightClick(Player player) {
        if (onInventoryRightClick != null) onInventoryRightClick.accept(player);
    }

    @Override
    public void onInventoryLeftClick(Player player) {
        if (onInventoryLeftClick != null) onInventoryLeftClick.accept(player);
    }

    @Override
    public void onInventoryDoubleClick(Player player) {
        if (onInventoryDoubleClick != null) onInventoryDoubleClick.accept(player);
    }

    @Override
    public void onInventoryMiddleClick(Player player) {
        if (onInventoryMiddleClick != null) onInventoryMiddleClick.accept(player);
    }

    @Override
    public void onPlayerBlaceBlock(BlockPlaceEvent event) {
        if (onPlayerPlaceBlock != null) onPlayerPlaceBlock.accept(event);
    }
}
