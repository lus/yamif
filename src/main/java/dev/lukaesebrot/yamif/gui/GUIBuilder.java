package dev.lukaesebrot.yamif.gui;

import dev.lukaesebrot.yamif.components.Component;
import dev.lukaesebrot.yamif.grid.SlotRange;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.util.function.Consumer;

/**
 * Represents a GUI builder
 *
 * @author Lukas Schulte Pelkum
 * @version 1.0.0
 * @since 1.0.0
 */
public class GUIBuilder {

    // Define the GUI
    private final GUI gui;

    /**
     * Creates a new GUI builder
     *
     * @param title The title of the GUI
     * @param rows  The amount of rows of the GUI
     */
    public GUIBuilder(String title, int rows) {
        this.gui = new GUI(title, rows);
    }

    /**
     * Fills the given slot range with copies of the given component
     *
     * @param range     The range to fill
     * @param component The component to take the copies from
     * @return The new GUI builder state
     */
    public GUIBuilder withComponents(SlotRange range, Component component) {
        this.gui.setComponents(range, component);
        return this;
    }

    /**
     * Adds an interaction policy for the given slot range
     *
     * @param range              The slot range to add the policy for
     * @param interactionAllowed Whether or nor interaction should be allowed in the given range
     * @return The new GUI builder state
     */
    public GUIBuilder withInteractionPolicy(SlotRange range, boolean interactionAllowed) {
        this.gui.setInteractionPolicy(range, interactionAllowed);
        return this;
    }

    /**
     * Defines the GUI drag event handler
     *
     * @param handler The GUI drag handler to use
     * @return The new GUI builder state
     */
    public GUIBuilder doOnDrag(Consumer<InventoryDragEvent> handler) {
        this.gui.doOnDrag(handler);
        return this;
    }

    /**
     * Defines the GUI close event handler
     *
     * @param handler The GUI close handler to use
     * @return The new GUI builder state
     */
    public GUIBuilder doOnClose(Consumer<InventoryCloseEvent> handler) {
        this.gui.doOnClose(handler);
        return this;
    }

    /**
     * @return The built GUI
     */
    public GUI build() {
        return this.gui;
    }

}
