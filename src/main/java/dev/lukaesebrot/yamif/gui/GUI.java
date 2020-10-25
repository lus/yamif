package dev.lukaesebrot.yamif.gui;

import dev.lukaesebrot.yamif.components.Component;
import dev.lukaesebrot.yamif.grid.SlotRange;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Represents a basic GUI
 *
 * @author Lukas Schulte Pelkum
 * @version 1.0.0
 * @since 1.0.0
 */
public class GUI {

    // Define GUI-related variables
    private final Inventory inventory;
    private final Map<Integer, Component> components;
    private final Map<Integer, Boolean> interactionPolicies;

    // Define the inventory drag and close handlers
    private Consumer<InventoryDragEvent> onDragHandler;
    private Consumer<InventoryCloseEvent> onCloseHandler;

    /**
     * Creates a new GUI
     *
     * @param title The title of the GUI
     * @param rows  The number of rowws of the GUI
     */
    public GUI(String title, int rows) {
        this.inventory = Bukkit.createInventory(new GUIInventoryHolder(), rows * 9, title);
        this.components = new HashMap<>();
        this.interactionPolicies = new HashMap<>();
    }

    /**
     * Sets the components in the given range
     *
     * @param range     The range to set the components to
     * @param component The component to take the copies from
     */
    public void setComponents(SlotRange range, Component component) {
        range.stripToInventorySize(this.inventory.getSize());
        range.getSlots().forEach(slot -> {
            Component current = component.clone();
            this.components.put(slot, current);
            this.inventory.setItem(slot, current.buildItemStack());
        });
    }

    /**
     * Removes all the components in the given range
     *
     * @param range The range to remove the components in
     */
    public void removeComponents(SlotRange range) {
        range.stripToInventorySize(this.inventory.getSize());
        range.getSlots().forEach(slot -> {
            this.components.remove(slot);
            this.inventory.setItem(slot, null);
        });
    }

    /**
     * Sets the interaction policy in the given range
     *
     * @param range              The range to set the interaction policy in
     * @param interactionAllowed Whether or not interaction should be allowed in the given range
     */
    public void setInteractionPolicy(SlotRange range, boolean interactionAllowed) {
        range.stripToInventorySize(this.inventory.getSize());
        range.getSlots().forEach(slot -> this.interactionPolicies.put(slot, interactionAllowed));
    }

    /**
     * Registers the GUI drag handler
     *
     * @param handler The handler to call when an item gets dragged to or from the GUI
     */
    public void doOnDrag(Consumer<InventoryDragEvent> handler) {
        this.onDragHandler = handler;
    }

    /**
     * Registers the GUI close handler
     *
     * @param handler The handler to call when the GUI gets closed
     */
    public void doOnClose(Consumer<InventoryCloseEvent> handler) {
        this.onCloseHandler = handler;
    }

    /**
     * Opens the GUI to a player
     *
     * @param plugin The plugin to register the interaction listener with
     * @param player The player to open the GUI for
     */
    public void open(JavaPlugin plugin, Player player) {
        registerInteractionListener(plugin);
        player.openInventory(this.inventory);
    }

    /**
     * Registers the interaction listener
     *
     * @param plugin The plugin to register the event listener with
     */
    private void registerInteractionListener(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new InteractionListener(), plugin);
    }

    /**
     * Represents the interaction listener of a GUI
     *
     * @author Lukas Schulte Pelkum
     * @version 1.0.0
     * @since 1.0.0
     */
    private class InteractionListener implements Listener {

        @EventHandler
        public void handleInventoryClick(InventoryClickEvent event) {
            // Check if the involved inventory corresponds to the GUI
            if (!corresponds(event.getClickedInventory())) {
                return;
            }

            // Check if the entity involved is a player
            if (!(event.getWhoClicked() instanceof Player)) {
                return;
            }

            // Check if interaction is allowed in this slot
            boolean interactionAllowed = interactionPolicies.getOrDefault(event.getSlot(), false);
            event.setCancelled(!interactionAllowed);

            // Trigger the corresponding component
            Component component = components.get(event.getSlot());
            if (component != null) {
                component.onClick(event);
            }
        }

        @EventHandler
        public void handleInventoryDrag(InventoryDragEvent event) {
            // Check if the involved inventory corresponds to the GUI
            if (!corresponds(event.getInventory())) {
                return;
            }

            // Check if the entity involved is a player
            if (!(event.getWhoClicked() instanceof Player)) {
                return;
            }

            // Check if interaction is allowed in all slots
            boolean interactionAllowed = event.getRawSlots().stream().allMatch(slot -> interactionPolicies.getOrDefault(slot, false));
            event.setCancelled(!interactionAllowed);

            // Trigger the GUI drag handler
            if (onDragHandler != null) {
                onDragHandler.accept(event);
            }
        }

        @EventHandler
        public void handleInventoryClose(InventoryCloseEvent event) {
            // Check if the involved inventory corresponds to the GUI
            if (!corresponds(event.getInventory())) {
                return;
            }

            // Check if the entity involved is a player
            if (!(event.getPlayer() instanceof Player)) {
                return;
            }

            // Trigger the GUI close handler
            if (onCloseHandler != null) {
                onCloseHandler.accept(event);
            }

            // Unregister this listener
            HandlerList.unregisterAll(this);
        }

        /**
         * Checks whether or not an inventory corresponds to the GUI
         *
         * @param inventory The inventory to check
         * @return Whether or not the given inventory corresponds to the GUI
         */
        private boolean corresponds(Inventory inventory) {
            // Check if the inventory is present
            if (inventory == null) {
                return false;
            }

            // Check if the holder of the inventory is a GUI-related one
            if (!(inventory.getHolder() instanceof GUIInventoryHolder)) {
                return false;
            }

            // Check if the UUIDs of the inventory holders match
            GUIInventoryHolder inventoryHolder = (GUIInventoryHolder) inventory.getHolder();
            GUIInventoryHolder guiHolder = (GUIInventoryHolder) GUI.this.inventory.getHolder();
            return inventoryHolder.getUUID().equals(guiHolder.getUUID());
        }

    }

}
