package dev.pelkum.yamif.grid;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a range of slots
 *
 * @author Lukas Schulte Pelkum
 * @version 1.0.0
 * @since 1.0.0
 */
public class SlotRange {

    // Define the set of slots
    private final Set<Integer> slots;

    /**
     * Creates a new slot range
     *
     * @param slots The set of slots
     */
    private SlotRange(final Set<Integer> slots) {
        this.slots = slots;
    }

    /**
     * @return The set of slots
     */
    public Set<Integer> getSlots() {
        return Collections.unmodifiableSet(this.slots);
    }

    /**
     * Combines multiple slot ranges
     *
     * @param ranges The slot ranges to combine
     * @return A slot range consisting of the given ones
     */
    public static SlotRange combine(final SlotRange... ranges) {
        final Set<Integer> slots = ConcurrentHashMap.newKeySet();

        for (final SlotRange range : ranges) {
            slots.addAll(range.getSlots());
        }

        return new SlotRange(slots);
    }

    /**
     * Creates a slot range containing one coordinate
     *
     * @param coordinate The coordinate to use
     * @return The created slot range
     */
    public static SlotRange single(final Coordinate coordinate) {
        return range(coordinate, coordinate);
    }

    /**
     * Creates a slot range containing all possible slots
     *
     * @return The created slot range
     */
    public static SlotRange full() {
        return range(
                new Coordinate(0, 0),
                new Coordinate(8, 5)
        );
    }

    /**
     * Creates a slot range based on a range between two coordinates
     *
     * @param a One corner-coordinate of the rectangle
     * @param b Another corner-coordinate of the rectangle
     * @return The created slot range
     */
    public static SlotRange range(final Coordinate a, final Coordinate b) {
        final Set<Integer> slots = ConcurrentHashMap.newKeySet();

        final int slot0 = Math.min(a.getSlot(), b.getSlot());
        final int slot1 = Math.max(a.getSlot(), b.getSlot());

        for (int slot = slot0; slot <= slot1; slot++) {
            slots.add(slot);
        }

        return new SlotRange(slots);
    }

    /**
     * Creates a slot range based on a rectangle
     *
     * @param a One corner-coordinate of the rectangle
     * @param b Another corner-coordinate of the rectangle
     * @return The created slot range
     */
    public static SlotRange rectangle(final Coordinate a, final Coordinate b) {
        final Set<Integer> slots = ConcurrentHashMap.newKeySet();

        final int x0 = Math.min(a.getX(), b.getX());
        final int x1 = Math.max(a.getX(), b.getX());

        final int y0 = Math.min(a.getY(), b.getY());
        final int y1 = Math.max(a.getY(), b.getY());

        for (int x = x0; x <= x1; x++) {
            for (int y = y0; y <= y1; y++) {
                final int slot = y * 9 + x;
                slots.add(slot);
            }
        }

        return new SlotRange(slots);
    }

    /**
     * Creates a slot range based on a row number
     *
     * @param row The row to use the slots of
     * @return The created slot range
     */
    public static SlotRange row(final int row) {
        return rectangle(
                new Coordinate(0, row),
                new Coordinate(8, row)
        );
    }

    /**
     * Creates a slot range based on a column number
     *
     * @param column The column to use the slots of
     * @return The created slot range
     */
    public static SlotRange column(final int column) {
        return rectangle(
                new Coordinate(column, 0),
                new Coordinate(column, 5)
        );
    }

    /**
     * Removes slots out of the given inventory size
     *
     * @param size The inventory size (has to be a positive multiple of 9)
     */
    public void stripToInventorySize(final int size) {
        if (size % 9 != 0 || size < 9) {
            throw new IllegalArgumentException("size has to be a positive multiple of 9");
        }

        this.slots.stream().filter(slot -> slot >= size).forEach(this.slots::remove);
    }

}
