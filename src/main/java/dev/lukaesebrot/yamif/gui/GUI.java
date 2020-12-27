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

    // Define the inventory click, drag and close handlers
    private Consumer<InventoryClickEvent> onClickHandler;
    private Consumer<InventoryDragEvent> onDragHandler;
    private Consumer<InventoryCloseEvent> onCloseHandler;

    // Define the shift-click allowment state
    private boolean shiftClickAllowed;

    /**
     * Creates a new GUI
     *
     * @param title The title of the GUI
     * @param rows  The number of rows of the GUI
     */
    public GUI(final String title, final int rows) {
        this.inventory = Bukkit.createInventory(new GUIInventoryHolder(), rows * 9, title);
        this.components = new HashMap<>();
        this.interactionPolicies = new HashMap<>();
        this.shiftClickAllowed = false;
    }

    /**
     * Sets the components in the given range
     *
     * @param range     The range to set the components to
     * @param component The component to take the copies from
     */
    public void setComponents(final SlotRange range, final Component component) {
        range.stripToInventorySize(this.inventory.getSize());
        range.getSlots().forEach(slot -> {
            final Component current = component.clone();
            this.components.put(slot, current);
            this.inventory.setItem(slot, current.buildItemStack());
        });
    }

    /**
     * Removes all the components in the given range
     *
     * @param range The range to remove the components in
     */
    public void removeComponents(final SlotRange range) {
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
    public void setInteractionPolicy(final SlotRange range, final boolean interactionAllowed) {
        range.stripToInventorySize(this.inventory.getSize());
        range.getSlots().forEach(slot -> this.interactionPolicies.put(slot, interactionAllowed));
    }

    /**
     * Registers the GUI click handler
     *
     * @param handler The handler to call when an item gets clicked
     */
    public void doOnClick(final Consumer<InventoryClickEvent> handler) {
        this.onClickHandler = handler;
    }

    /**
     * Registers the GUI drag handler
     *
     * @param handler The handler to call when an item gets dragged inside the GUI
     */
    public void doOnDrag(final Consumer<InventoryDragEvent> handler) {
        this.onDragHandler = handler;
    }

    /**
     * Registers the GUI close handler
     *
     * @param handler The handler to call when the GUI gets closed
     */
    public void doOnClose(final Consumer<InventoryCloseEvent> handler) {
        this.onCloseHandler = handler;
    }

    /**
     * Allows shift-clicking
     */
    public void allowShiftClick() {
        this.shiftClickAllowed = true;
    }

    /**
     * Opens the GUI to a player
     *
     * @param plugin The plugin to register the interaction listener with
     * @param player The player to open the GUI for
     */
    public void open(final JavaPlugin plugin, final Player player) {
        this.registerInteractionListener(plugin);
        player.openInventory(this.inventory);
    }

    /**
     * Registers the interaction listener
     *
     * @param plugin The plugin to register the event listener with
     */
    private void registerInteractionListener(final JavaPlugin plugin) {
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
        public void handleInventoryClick(final InventoryClickEvent event) {
            // Check if the event was called during a shift-click
            if (event.isShiftClick()) {
                // Check if one of the two inventories is the GUI one
                if (!this.corresponds(event.getView().getTopInventory()) && !this.corresponds(event.getView().getBottomInventory())) {
                    return;
                }

                // Cancel the event if needed
                if (!GUI.this.shiftClickAllowed) {
                    event.setCancelled(true);
                    return;
                }
            }

            // Check if the involved inventory corresponds to the GUI
            if (!this.corresponds(event.getClickedInventory())) {
                return;
            }

            // Check if the entity involved is a player
            if (!(event.getWhoClicked() instanceof Player)) {
                return;
            }

            // Check if interaction is allowed in this slot
            final boolean interactionAllowed = GUI.this.interactionPolicies.getOrDefault(event.getSlot(), false);
            event.setCancelled(!interactionAllowed);

            // Trigger the corresponding component
            final Component component = GUI.this.components.get(event.getSlot());
            if (component != null) {
                component.onClick(event);
            }

            // Trigger the GUI click handler
            if (GUI.this.onClickHandler != null) {
                GUI.this.onClickHandler.accept(event);
            }
        }

        @EventHandler
        public void handleInventoryDrag(final InventoryDragEvent event) {
            // Check if the involved inventory corresponds to the GUI
            if (!this.corresponds(event.getInventory())) {
                return;
            }

            // Check if the entity involved is a player
            if (!(event.getWhoClicked() instanceof Player)) {
                return;
            }

            // Check if interaction is allowed in all slots
            final boolean interactionAllowed = event.getRawSlots().stream().allMatch(slot -> GUI.this.interactionPolicies.getOrDefault(slot, false));
            event.setCancelled(!interactionAllowed);

            // Trigger the GUI drag handler
            if (GUI.this.onDragHandler != null) {
                GUI.this.onDragHandler.accept(event);
            }
        }

        @EventHandler
        public void handleInventoryClose(final InventoryCloseEvent event) {
            // Check if the involved inventory corresponds to the GUI
            if (!this.corresponds(event.getInventory())) {
                return;
            }

            // Check if the entity involved is a player
            if (!(event.getPlayer() instanceof Player)) {
                return;
            }

            // Trigger the GUI close handler
            if (GUI.this.onCloseHandler != null) {
                GUI.this.onCloseHandler.accept(event);
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
        private boolean corresponds(final Inventory inventory) {
            // Check if the inventory is present
            if (inventory == null) {
                return false;
            }

            // Check if the holder of the inventory is a GUI-related one
            if (!(inventory.getHolder() instanceof GUIInventoryHolder)) {
                return false;
            }

            // Check if the UUIDs of the inventory holders match
            final GUIInventoryHolder inventoryHolder = (GUIInventoryHolder) inventory.getHolder();
            final GUIInventoryHolder guiHolder = (GUIInventoryHolder) GUI.this.inventory.getHolder();
            return inventoryHolder.getUUID().equals(guiHolder.getUUID());
        }

    }

}
