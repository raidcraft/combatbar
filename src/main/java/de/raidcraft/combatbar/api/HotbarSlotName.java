package de.raidcraft.combatbar.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The unique displayName of the hotbar slot.
 * The displayName will be referenced in the database to instantiate this {@link HotbarSlot}.
 * All custom {@link HotbarSlot}s must be annotated with a displayName.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface HotbarSlotName {

    String value();
}
