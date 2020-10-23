package dev.lukaesebrot.yamif.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

/**
 * Represents the inventory holder of a GUI
 *
 * @author Lukas Schulte Pelkum
 * @version 1.0.0
 * @since 1.0.0
 */
public class GUIInventoryHolder implements InventoryHolder {

    // Define and initialize the UUID
    private final UUID uuid = UUID.randomUUID();

    /**
     * @return The UUID of this inventory holder
     */
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }

}
