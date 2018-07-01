package de.raidcraft.combatbar.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The unique displayName of the hotbar that will be saved in the database.
 * All custom {@link Hotbar}s must be annotated with a displayName.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface HotbarName {

    String value();
}
