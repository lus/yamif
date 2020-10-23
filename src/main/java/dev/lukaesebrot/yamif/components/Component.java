package dev.lukaesebrot.yamif.components;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Represents the component of a GUI
 *
 * @author Lukas Schulte Pelkum
 * @version 1.0.0
 * @since 1.0.0
 */
public interface Component {

    /**
     * @return The item stack of the component
     */
    ItemStack buildItemStack();

    /**
     * @return A cloned version of the component
     */
    Component clone();

    /**
     * Gets called whenever a player clicks on the component
     *
     * @param player The player who clicked on the component
     */
    default void onClick(Player player) {
    }

}
