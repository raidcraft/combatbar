package de.raidcraft.combatbar;

import de.raidcraft.api.Component;
import de.raidcraft.combatbar.api.*;
import de.raidcraft.combatbar.factories.HotbarFactory;
import de.raidcraft.combatbar.factories.HotbarHolderFactory;
import de.raidcraft.combatbar.factories.HotbarSlotFactory;
import de.raidcraft.combatbar.hotbars.InventoryHotbar;
import de.raidcraft.combatbar.slots.InventoryHotbarSlot;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.function.Consumer;

public class HotbarManager implements Component {

    @Getter
    private final RCHotbarPlugin plugin;
    @Getter
    private final HotbarHolderFactory holderFactory;
    private final Map<UUID, HotbarHolder> hotbarHolders = new HashMap<>();
    private final Map<String, HotbarFactory<?>> hotbarFactories = new HashMap<>();
    private final Map<String, HotbarSlotFactory<?>> hotbarSlotFactories = new HashMap<>();
    private final List<Consumer<HotbarHolder>> hotbarMenuActions = new ArrayList<>();

    public HotbarManager(RCHotbarPlugin plugin) {
        this.plugin = plugin;
        this.holderFactory = new HotbarHolderFactory(plugin);
        registerGlobalHotbarSlots();
    }

    private void registerGlobalHotbars() {
        registerHotbarType(getPlugin(), Hotbar.class);
        registerHotbarType(getPlugin(), InventoryHotbar.class);
    }

    private void registerGlobalHotbarSlots() {
        registerHotbarSlotType(InventoryHotbarSlot.class);
    }

    public Optional<HotbarFactory<?>> getHotbarFactory(String name) {
        return Optional.ofNullable(hotbarFactories.get(name));
    }

    public Optional<HotbarFactory<?>> getHotbarFactory(Class<? extends Hotbar> hotbarType) {
        return getHotbarFactory(getHotbarName(hotbarType));
    }

    public HotbarFactory<?> getDefaultHotbarFactory() {
        return hotbarFactories.get(RCHotbarPlugin.DEFAULT_HOTBAR);
    }

    public Optional<HotbarSlotFactory<?>> getHotbarSlotFactory(String name) {
        return Optional.ofNullable(hotbarSlotFactories.get(name));
    }

    public void addHotbarMenuAction(Consumer<HotbarHolder> action) {
        this.hotbarMenuActions.add(action);
    }

    private String getHotbarName(Class<? extends Hotbar> hotbarType) {
        return hotbarType.getAnnotation(HotbarName.class).value();
    }

    public void registerHotbarType(Plugin plugin, Class<? extends Hotbar> hotbarClass) {
        if (!hotbarClass.isAnnotationPresent(HotbarName.class)) {
            getPlugin().warning(plugin.getName() + " tried to register Hotbar Class " + hotbarClass.getCanonicalName() + " without annoation!");
            return;
        }

        String hotbarName = getHotbarName(hotbarClass);

        if (hotbarFactories.containsKey(hotbarName)) {
            getPlugin().getLogger().warning("Duplicate Hotbar Registration with displayName: " + hotbarName);
            return;
        }

        try {
            hotbarFactories.put(hotbarName, new HotbarFactory<>(hotbarClass));
            getPlugin().getLogger().info("Registered hotbar type: " + hotbarName);
        } catch (NoSuchMethodException e) {
            getPlugin().warning(hotbarName + " has no matching default constructor that only takes a HotbarHolder! " + hotbarClass.getCanonicalName());
            e.printStackTrace();
        }
    }

    public void registerHotbarSlotType(Class<? extends HotbarSlot> hotbarSlotClass) {

        getHotbarSlotName(hotbarSlotClass)
                .ifPresent(name -> registerHotbarSlot(name, hotbarSlotClass));
    }

    private void registerGlobalHotbarSlot(Class<? extends HotbarSlot> clazz) {

        getHotbarSlotName(clazz).ifPresent(name -> registerHotbarSlot(name, clazz));
    }

    private void registerHotbarSlot(String name, Class<? extends HotbarSlot> clazz) {

        if (hotbarSlotFactories.containsKey(name)) {
            getPlugin().getLogger().warning("Duplicate HotbarSlot Registration with name: " + name);
            return;
        }

        try {
            hotbarSlotFactories.put(name, new HotbarSlotFactory<>(clazz));
            getPlugin().getLogger().info("Registered hotbarslot type: " + name);
        } catch (NoSuchMethodException e) {
            getPlugin().warning(name + " has no matching empty default constructor! " + clazz.getCanonicalName());
            e.printStackTrace();
        }
    }

    private Optional<String> getHotbarSlotName(Class<? extends HotbarSlot> clazz) {
        if (!clazz.isAnnotationPresent(HotbarSlotName.class)) {
            getPlugin().warning("Tried to register HotbarSlot Class " + clazz.getCanonicalName() + " without annoation!");
            return Optional.empty();
        }
        return Optional.of(clazz.getAnnotation(HotbarSlotName.class).value());
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

    public Hotbar getOrCreateHotbar(Player player) {
        HotbarHolder hotbarHolder = getHotbarHolder(player);
        return hotbarHolder.getActiveHotbar().orElseGet(() -> {
            Hotbar hotbar = getDefaultHotbarFactory().createNew(hotbarHolder);
            hotbarHolder.addHotbar(hotbar);
            return hotbar;
        });
    }

    public Hotbar getOrCreateHotbar(Player player, Class<? extends Hotbar> hotbarType) {

        HotbarHolder holder = getHotbarHolder(player);
        Hotbar hotbar = holder.getActiveHotbar().filter(hotbarType::isInstance)
                .orElseGet(() -> getHotbarFactory(hotbarType)
                        .map(hotbarFactory -> hotbarFactory.createNew(holder))
                        .orElse(null));

        holder.addHotbar(hotbar, true);
        return hotbar;
    }

    public HotbarHolder getHotbarHolder(Player player) {
        if (hotbarHolders.containsKey(player.getUniqueId())) {
            return hotbarHolders.get(player.getUniqueId());
        }
        registerPlayer(player);
        return getHotbarHolder(player);
    }

    public void registerPlayer(Player player) {
        if (hotbarHolders.containsKey(player.getUniqueId())) return;
        HotbarHolder holder = getHolderFactory().create(player);
        getPlugin().registerEvents(holder);
        hotbarHolders.put(player.getUniqueId(), holder);
        if (hotbarMenuActions.size() > 0) holder.setMenuItemAction(hotbarMenuActions.get(0));
        holder.enable();
    }

    public void unregisterPlayer(Player player) {
        HotbarHolder holder = hotbarHolders.remove(player.getUniqueId());
        if (holder != null) {
            getPlugin().unregisterEvents(holder);
            holder.disable();
        }
    }
}
