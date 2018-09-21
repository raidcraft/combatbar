package de.raidcraft.combatbar.factories;

import de.raidcraft.RaidCraft;
import de.raidcraft.combatbar.HotbarManager;
import de.raidcraft.combatbar.RCHotbarPlugin;
import de.raidcraft.combatbar.api.Hotbar;
import de.raidcraft.combatbar.api.HotbarHolder;
import de.raidcraft.combatbar.api.HotbarName;
import de.raidcraft.combatbar.tables.THotbar;
import de.raidcraft.combatbar.tables.THotbarHolder;
import io.ebean.EbeanServer;
import lombok.Data;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@Data
public final class HotbarFactory<T extends Hotbar> {

    private final String name;
    private final Constructor<T> constructor;

    public HotbarFactory(Class<T> clazz) throws NoSuchMethodException {
        this.name = clazz.getAnnotation(HotbarName.class).value();
        this.constructor = clazz.getDeclaredConstructor(HotbarHolder.class);
    }

    public T create(THotbar dbEntry, HotbarHolder holder) {
        try {
            T hotbar = constructor.newInstance(holder);
            hotbar.setDatabaseId(dbEntry.getId());
            hotbar.setActive(dbEntry.isActive());
            hotbar.setDisplayName(dbEntry.getDisplayName());

            loadHotbarSlots(dbEntry, hotbar);
            return hotbar;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadHotbarSlots(THotbar dbEntry, Hotbar hotbar) {

        HotbarManager hotbarManager = RaidCraft.getComponent(HotbarManager.class);

        dbEntry.getHotbarSlots().forEach(slot -> {
            hotbarManager.getHotbarSlotFactory(slot.getName())
                    .map(factory -> factory.create(slot))
                    .ifPresent(createdSlot -> hotbar.setHotbarSlot(createdSlot.getIndex(), createdSlot));
        });
    }


    public T createNew(HotbarHolder holder) {
        EbeanServer database = RaidCraft.getDatabase(RCHotbarPlugin.class);

        THotbar tHotbar = new THotbar();
        tHotbar.setName(getName());
        holder.getDatabaseId().ifPresent(id -> tHotbar.setHolder(database.find(THotbarHolder.class, id)));
        database.save(tHotbar);

        return create(tHotbar, holder);
    }
}
