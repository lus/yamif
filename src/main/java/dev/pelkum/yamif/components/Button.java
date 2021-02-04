package dev.pelkum.yamif.components;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

/**
 * Represents the button component
 *
 * @author Lukas Schulte Pelkum
 * @version 1.0.0
 * @since 1.0.0
 */
public class Button implements Component {

    // Define component-related variables
    private final ItemStack itemStack;
    private final Consumer<InventoryClickEvent> handler;

    /**
     * Creates a new button component
     *
     * @param itemStack The item stack to use as the icon
     * @param handler   The handler which gets called when a player clicks on the button
     */
    public Button(final ItemStack itemStack, final Consumer<InventoryClickEvent> handler) {
        this.itemStack = itemStack;
        this.handler = handler;
    }

    @Override
    public ItemStack buildItemStack() {
        return this.itemStack;
    }

    @Override
    public Component clone() {
        return new Button(this.itemStack, this.handler);
    }

    @Override
    public void onClick(final InventoryClickEvent event) {
        this.handler.accept(event);
    }

}
