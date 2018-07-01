package de.raidcraft.combatbar.factories;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.RaidCraft;
import de.raidcraft.combatbar.RCHotbarPlugin;
import de.raidcraft.combatbar.api.Hotbar;
import de.raidcraft.combatbar.api.HotbarSlot;
import de.raidcraft.combatbar.api.HotbarSlotName;
import de.raidcraft.combatbar.tables.THotbar;
import de.raidcraft.combatbar.tables.THotbarSlot;
import de.raidcraft.util.ConfigUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class HotbarSlotFactory<T extends HotbarSlot> {

    private final String name;
    private final Constructor<T> constructor;

    public HotbarSlotFactory(Class<T> clazz) throws NoSuchMethodException {
        this.name = clazz.getAnnotation(HotbarSlotName.class).value();
        this.constructor = clazz.getDeclaredConstructor();
    }

    public T create() {
        try {
            T instance = constructor.newInstance();
            instance.setName(name);
            return instance;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public T create(THotbarSlot dbEntry) {
        T slot = create();
        slot.setDatabaseId(dbEntry.getId());
        slot.setIndex(dbEntry.getPosition());
        slot.setItem(RaidCraft.getUnsafeItem(dbEntry.getItem()));
        slot.load(ConfigUtil.parseKeyValueTable(new ArrayList<>(dbEntry.getData())));
        return slot;
    }

    public T create(Hotbar hotbar) {
        EbeanServer database = RaidCraft.getDatabase(RCHotbarPlugin.class);

        THotbarSlot slot = new THotbarSlot();
        hotbar.getDatabaseId().ifPresent(id -> slot.setHotbar(database.find(THotbar.class, id)));
        database.save(slot);

        return create(slot);
    }
}
