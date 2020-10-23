package dev.lukaesebrot.yamif.components;

import org.bukkit.entity.Player;
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
    private final Consumer<Player> handler;

    /**
     * Creates a new button component
     *
     * @param itemStack The item stack to use as the icon
     * @param handler   The handler which gets called when a player clicks on the button
     */
    public Button(ItemStack itemStack, Consumer<Player> handler) {
        this.itemStack = itemStack;
        this.handler = handler;
    }

    @Override
    public ItemStack buildItemStack() {
        return itemStack;
    }

    @Override
    public Component clone() {
        return new Button(itemStack, handler);
    }

    @Override
    public void onClick(Player player) {
        handler.accept(player);
    }

}
