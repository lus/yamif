package dev.lukaesebrot.yamif.components;

import org.bukkit.inventory.ItemStack;

/**
 * Represents the item component
 *
 * @author Lukas Schulte Pelkum
 * @version 1.0.0
 * @since 1.0.0
 */
public class Item implements Component {

    // Define component-related variables
    private final ItemStack itemStack;

    /**
     * Creates a new item component
     *
     * @param itemStack The item stack to use
     */
    public Item(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public ItemStack buildItemStack() {
        return itemStack;
    }

    @Override
    public Component clone() {
        return new Item(itemStack);
    }

}
