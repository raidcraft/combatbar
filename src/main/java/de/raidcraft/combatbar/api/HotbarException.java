package de.raidcraft.combatbar.api;

import de.raidcraft.api.RaidCraftException;

public class HotbarException extends RaidCraftException {
    public HotbarException(String message) {
        super(message);
    }

    public HotbarException(String message, Throwable cause) {
        super(message, cause);
    }

    public HotbarException(Throwable cause) {
        super(cause);
    }


    public HotbarException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
